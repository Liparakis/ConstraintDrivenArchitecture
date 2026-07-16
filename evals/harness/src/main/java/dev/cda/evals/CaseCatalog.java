package dev.cda.evals;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

final class CaseCatalog {
    private final Path evalRoot, casesRoot;
    private List<Models.CaseEntry> cached;

    CaseCatalog(Path evalRoot) { this.evalRoot = evalRoot.toAbsolutePath().normalize(); this.casesRoot = this.evalRoot.resolve("cases"); }

    List<Models.CaseEntry> all() throws IOException {
        if (cached != null) return cached;
        List<Models.CaseEntry> result = new ArrayList<>();
        if (!Files.isDirectory(casesRoot)) throw new IOException("Missing cases directory: " + casesRoot);
        try (var dirs = Files.list(casesRoot)) {
            for (Path dir : dirs.filter(Files::isDirectory).sorted().toList()) {
                Path caseFile = dir.resolve("case.json");
                if (!Files.isRegularFile(caseFile)) continue;
                Models.CaseDef base = readCase(caseFile);
                result.add(new Models.CaseEntry(base, null, dir));
                Path mutations = dir.resolve("mutations");
                if (Files.isDirectory(mutations)) try (var ms = Files.list(mutations)) {
                    for (Path md : ms.filter(Files::isDirectory).sorted().toList()) {
                        Path mf = md.resolve("mutation.json");
                        if (Files.isRegularFile(mf)) result.add(new Models.CaseEntry(base, readMutation(mf), md));
                    }
                }
            }
        }
        result.sort(Comparator.comparing(Models.CaseEntry::id));
        cached = List.copyOf(result); return cached;
    }
    Models.CaseEntry find(String id) throws IOException { return all().stream().filter(c -> c.id().equals(id)).findFirst().orElseThrow(() -> new IOException("Unknown case: " + id)); }

    void validate() throws IOException {
        List<Models.CaseEntry> entries = all();
        if (entries.stream().map(Models.CaseEntry::id).distinct().count() != entries.size()) throw new IOException("Duplicate case ID");
        Set<String> baseIds = new HashSet<>();
        for (Models.CaseEntry e : entries) {
            if (!e.isMutation()) baseIds.add(e.id());
            validatePublic(e);
            JsonObject expectations = JsonFiles.object(expectationsPath(e));
            JsonFiles.requireKeys(expectations, Set.of("requiredReasoning", "allowedVariability", "suspiciousAssumptions", "architectureChangingUnknowns"), Set.of("requiredReasoning", "allowedVariability", "suspiciousAssumptions", "architectureChangingUnknowns"), expectationsPath(e));
            if (e.isMutation()) {
                if (!baseIds.contains(e.mutation().baseCaseId()) && all().stream().noneMatch(x -> x.id().equals(e.mutation().baseCaseId()))) throw new IOException("Mutation base missing: " + e.id());
                if (!e.mutation().familyId().equals(e.base().familyId())) throw new IOException("Mutation family mismatch: " + e.id());
                if (e.mutation().affectedDimensions().isEmpty() || e.mutation().stableDimensions().isEmpty()) throw new IOException("Mutation dimensions missing: " + e.id());
            }
        }
    }

    Map<String, Path> publicFiles(Models.CaseEntry e) throws IOException {
        Map<String, Path> files = new LinkedHashMap<>();
        Models.CaseDef base = e.base();
        files.put("brief.md", safe(e.isMutation() ? e.caseDir().resolve("../../public/brief.md") : e.caseDir().resolve(base.brief())));
        files.put("prompt.md", safe(e.isMutation() ? e.caseDir().resolve("../../public/prompt.md") : e.caseDir().resolve(base.prompt())));
        if (e.isMutation()) files.put("constraint-change.md", safe(e.caseDir().resolve(e.mutation().change())));
        String fixture = base.fixture();
        if (fixture != null && !fixture.isBlank()) {
            Path fixtureRoot = safe(e.base().fixture() == null ? e.caseDir() : basePath(e).resolve(fixture));
            if (!Files.isDirectory(fixtureRoot)) throw new IOException("Missing fixture: " + fixtureRoot);
            try (var stream = Files.walk(fixtureRoot)) {
                for (Path p : stream.sorted().toList()) {
                    if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) files.put("fixture/" + fixtureRoot.relativize(p).toString().replace('\\', '/'), p);
                }
            }
        }
        for (Path p : files.values()) if (!Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) throw new IOException("Missing public input: " + p);
        return files;
    }

    Path expectationsPath(Models.CaseEntry e) { return safeUnchecked(e.caseDir().resolve(e.isMutation() ? e.mutation().expectations() : e.base().expectations())); }
    Path evalRoot() { return evalRoot; }

    private Models.CaseDef readCase(Path p) throws IOException {
        JsonObject o = JsonFiles.object(p);
        JsonFiles.requireKeys(o, Set.of("schemaVersion","id","familyId","title","version","mode","brief","prompt","expectations"), Set.of("schemaVersion","id","familyId","title","version","mode","brief","prompt","expectations","fixture"), p);
        return JsonFiles.read(p, Models.CaseDef.class);
    }
    private Models.MutationDef readMutation(Path p) throws IOException {
        JsonObject o = JsonFiles.object(p);
        JsonFiles.requireKeys(o, Set.of("schemaVersion","id","baseCaseId","familyId","title","version","change","expectations","affectedDimensions","stableDimensions"), Set.of("schemaVersion","id","baseCaseId","familyId","title","version","change","expectations","affectedDimensions","stableDimensions"), p);
        return JsonFiles.read(p, Models.MutationDef.class);
    }
    private void validatePublic(Models.CaseEntry e) throws IOException {
        Map<String, Path> inputs = publicFiles(e);
        if (!inputs.keySet().containsAll(List.of("brief.md", "prompt.md"))) throw new IOException("Missing brief/prompt: " + e.id());
    }
    private Path basePath(Models.CaseEntry e) { return e.isMutation() ? e.caseDir().resolve("../..") : e.caseDir(); }
    private Path safe(Path path) throws IOException { Path p = safeUnchecked(path); if (!p.startsWith(evalRoot)) throw new IOException("Path escapes eval root: " + path); return p; }
    private Path safeUnchecked(Path path) { return path.toAbsolutePath().normalize(); }
}
