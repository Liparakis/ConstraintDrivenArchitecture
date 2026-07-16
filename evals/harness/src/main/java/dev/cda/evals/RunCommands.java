package dev.cda.evals;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import javax.tools.ToolProvider;

final class RunCommands {
    private final Path evalRoot, skillRoot;
    private final CaseCatalog catalog;
    private final WorkspaceStore store;

    RunCommands(Path evalRoot, Path skillRoot, WorkspaceStore store) { this.evalRoot = evalRoot; this.skillRoot = skillRoot.toAbsolutePath().normalize(); this.catalog = new CaseCatalog(evalRoot); this.store = store; }

    void validate() throws IOException {
        catalog.validate();
        validateFixtures();
        DualModelConfig.validate(evalRoot.getParent());
        Map<String, String> hashes = skillFiles();
        if (!hashes.containsKey("SKILL.md")) throw new IOException("Skill root is missing SKILL.md");
        try (var stream = Files.list(evalRoot.resolve("schemas"))) { for (Path p : stream.filter(Files::isRegularFile).toList()) JsonFiles.object(p); }
        String forbidden = "evals/";
        for (String rel : List.of("SKILL.md", "agents/openai.yaml")) {
            Path p = skillRoot.resolve(rel); if (Files.isRegularFile(p) && Files.readString(p).toLowerCase(Locale.ROOT).contains(forbidden)) throw new IOException("Skill file references evals/: " + p);
        }
        try (var refs = Files.walk(skillRoot.resolve("references"))) {
            for (Path p : refs.filter(Files::isRegularFile).toList()) if (Files.readString(p).toLowerCase(Locale.ROOT).contains(forbidden)) throw new IOException("Reference references evals/: " + p);
        }
        System.out.println("Validation passed: " + catalog.all().size() + " cases/mutations; skill files " + hashes.size());
    }

    private void validateFixtures() throws IOException {
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IOException("A JDK is required to validate Java fixtures");
        Map<String, List<Path>> fixtureSources = new LinkedHashMap<>();
        for (Models.CaseEntry entry : catalog.all()) {
            if (entry.base().fixture() == null) continue;
            for (Path input : catalog.publicFiles(entry).values()) if (input.toString().endsWith(".java")) fixtureSources.computeIfAbsent(entry.base().fixture(), ignored -> new ArrayList<>()).add(input);
        }
        for (List<Path> sources : fixtureSources.values()) {
            Path output = Files.createTempDirectory("cda-fixture-");
            try {
                String[] sourceNames = sources.stream().map(Path::toString).distinct().toArray(String[]::new);
                List<String> args = new ArrayList<>(List.of("-d", output.toString(), "--release", "25")); args.addAll(List.of(sourceNames));
                if (sourceNames.length > 0 && compiler.run(null, null, null, args.toArray(String[]::new)) != 0) throw new IOException("Fixture does not compile: " + sources.getFirst());
            } finally {
                try (var paths = Files.walk(output)) { paths.sorted(Comparator.reverseOrder()).forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} }); }
            }
        }
    }

    void snapshot(String label, String notes) throws IOException {
        if (!label.matches("[A-Za-z0-9._-]+")) throw new IOException("Invalid snapshot label: " + label);
        Map<String, String> files = skillFiles();
        Models.Snapshot snapshot = new Models.Snapshot(1, label, Instant.now().toString(), files, Hashing.combined(files),
                List.of("evals/**", ".git/**", ".idea/**", "build/**", "reports/**"), notes);
        Path target = store.snapshots().resolve(label + ".json");
        if (Files.exists(target)) {
            Models.Snapshot old = JsonFiles.read(target, Models.Snapshot.class);
            if (!old.combinedHash().equals(snapshot.combinedHash())) throw new IOException("Snapshot label already exists with a different hash: " + label);
        } else JsonFiles.write(target, snapshot);
        System.out.println("Snapshot " + label + " " + snapshot.combinedHash());
    }

    void listCases(String mode) throws IOException {
        for (Models.CaseEntry e : catalog.all()) if (mode == null || e.mode().name().equals(mode)) System.out.printf("%-48s %-10s %-8s %s%n", e.id(), e.mode(), e.isMutation() ? "mutation" : "base", e.title());
    }

    String prepare(String caseId, String snapshotLabel, String model, String reasoning) throws IOException {
        Models.CaseEntry e = catalog.find(caseId);
        Models.Snapshot snapshot = JsonFiles.read(store.snapshots().resolve(snapshotLabel + ".json"), Models.Snapshot.class);
        String currentSkillHash = Hashing.combined(skillFiles());
        if (!snapshot.combinedHash().equals(currentSkillHash)) throw new IOException("Current skill does not match snapshot " + snapshotLabel);
        String id = Instant.now().toString().replaceAll("[-:.TZ]", "").substring(0, 14) + "-" + UUID.randomUUID().toString().substring(0, 8);
        Path run = store.run(id), generation = run.resolve("generation"); Files.createDirectories(generation);
        Map<String, Path> inputs = catalog.publicFiles(e); Map<String, String> inputHashes = new TreeMap<>();
        for (var entry : inputs.entrySet()) {
            String logical = entry.getKey(); Path target = logical.startsWith("fixture/") ? generation.resolve(logical) : generation.resolve(logical);
            Files.createDirectories(target.getParent()); Files.copy(entry.getValue(), target, StandardCopyOption.REPLACE_EXISTING); inputHashes.put(logical, Hashing.file(entry.getValue()));
        }
        String briefHash = inputHashes.get("brief.md"), promptHash = inputHashes.get("prompt.md");
        Map<String, String> fixtureHashes = new TreeMap<>(); inputHashes.forEach((k,v) -> { if (k.startsWith("fixture/")) fixtureHashes.put(k.substring("fixture/".length()), v); });
        String fixtureHash = fixtureHashes.isEmpty() ? null : Hashing.combined(fixtureHashes);
        Models.RunManifest manifest = new Models.RunManifest(1, id, e.id(), e.version(), e.familyId(), e.mode(), model, reasoning, snapshot.label(), snapshot.combinedHash(), briefHash, promptHash, fixtureHash, e.isMutation() ? e.mutation().baseCaseId() : null, Instant.now().toString());
        JsonFiles.write(run.resolve("manifest.json"), manifest);
        Map<String, Object> inputManifest = new LinkedHashMap<>(); inputManifest.put("schemaVersion", 1); inputManifest.put("caseId", e.id()); inputManifest.put("caseVersion", e.version()); inputManifest.put("familyId", e.familyId()); inputManifest.put("mode", e.mode()); inputManifest.put("skillVersion", snapshot.label()); inputManifest.put("skillHash", snapshot.combinedHash()); inputManifest.put("briefHash", briefHash); inputManifest.put("promptHash", promptHash); inputManifest.put("fixtureHash", fixtureHash); inputManifest.put("mutationBaseCaseId", e.isMutation() ? e.mutation().baseCaseId() : null); inputManifest.put("files", inputHashes);
        Path inputManifestPath = generation.resolve("input-manifest.json"); JsonFiles.write(inputManifestPath, inputManifest); Files.writeString(generation.resolve("input-manifest.sha256"), Hashing.file(inputManifestPath) + System.lineSeparator(), StandardCharsets.UTF_8);
        store.status(id, Models.Status.PREPARED.name(), Map.of("createdAt", Instant.now().toString()));
        System.out.println("Run " + id + " prepared at " + generation);
        return id;
    }

    void ingest(String id, String candidatePath) throws IOException {
        Path run = store.requireRun(id); Models.RunManifest m = store.manifest(id);
        if (!Hashing.combined(skillFiles()).equals(m.skillHash())) { store.status(id, Models.Status.INVALID.name(), Map.of("reason", "skill hash changed")); throw new IOException("Run invalid: skill hash changed after preparation"); }
        Path inputManifestPath = run.resolve("generation/input-manifest.json");
        Path inputDigestPath = run.resolve("generation/input-manifest.sha256");
        if (!Files.isRegularFile(inputDigestPath) || !Hashing.file(inputManifestPath).equals(Files.readString(inputDigestPath).trim())) {
            store.status(id, Models.Status.INVALID.name(), Map.of("reason", "input manifest changed"));
            throw new IOException("Run invalid: input manifest changed");
        }
        JsonObject input = JsonFiles.object(inputManifestPath);
        for (String logical : input.getAsJsonObject("files").keySet()) {
            Path p = run.resolve("generation").resolve(logical); String expected = input.getAsJsonObject("files").get(logical).getAsString();
            if (!Files.isRegularFile(p) || !Hashing.file(p).equals(expected)) { store.status(id, Models.Status.INVALID.name(), Map.of("reason", "generation input changed", "file", logical)); throw new IOException("Run invalid: generation input changed: " + logical); }
        }
        Path source = candidatePath == null ? run.resolve("generation/candidate.md") : Path.of(candidatePath); if (!Files.isRegularFile(source)) throw new IOException("Candidate file not found: " + source);
        Path result = run.resolve("result/candidate.md"); Files.createDirectories(result.getParent()); Files.copy(source, result, StandardCopyOption.REPLACE_EXISTING);
        store.status(id, Models.Status.INGESTED.name(), Map.of("ingestedAt", Instant.now().toString(), "candidateHash", Hashing.file(result)));
        System.out.println("Candidate ingested for " + id);
    }

    void incomplete(String id, String reason) throws IOException {
        store.requireRun(id);
        store.status(id, Models.Status.INCOMPLETE.name(), Map.of("reason", reason, "updatedAt", Instant.now().toString()));
        System.out.println("Run marked incomplete: " + id);
    }

    Map<String, String> skillFiles() throws IOException {
        Map<String, String> files = new TreeMap<>();
        add(files, "SKILL.md", skillRoot.resolve("SKILL.md")); add(files, "agents/openai.yaml", skillRoot.resolve("agents/openai.yaml"));
        Path refs = skillRoot.resolve("references"); if (Files.isDirectory(refs)) try (var stream = Files.walk(refs)) {
            for (Path p : stream.filter(Files::isRegularFile).sorted().toList()) add(files, skillRoot.relativize(p).toString().replace('\\','/'), p);
        }
        return files;
    }
    private void add(Map<String,String> files, String rel, Path path) throws IOException { if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) files.put(rel, Hashing.file(path)); }
}
