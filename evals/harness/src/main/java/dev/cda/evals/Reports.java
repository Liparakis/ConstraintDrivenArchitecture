package dev.cda.evals;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

final class Reports {
    private final WorkspaceStore store;
    Reports(WorkspaceStore store) { this.store = store; }

    void compare(String kind, List<String> ids, Path output) throws IOException {
        if (ids.size() < 2) throw new IOException("compare needs at least two run IDs");
        List<Models.RunManifest> manifests = new ArrayList<>(); for (String id : ids) manifests.add(store.manifest(id));
        for (int i = 1; i < manifests.size(); i++) ensureCompatible(kind, manifests.get(0), manifests.get(i));
        List<RunScore> scores = manifests.stream().map(m -> loadScore(m.runId())).toList(); StringBuilder md = new StringBuilder("# Comparison\n\nKind: ").append(kind).append("\n\n");
        md.append("| Run | Model | Skill | Score |\n|---|---|---|---:|\n"); for (RunScore s : scores) md.append("| ").append(s.manifest.runId()).append(" | ").append(s.manifest.modelLabel()).append(" | ").append(s.manifest.skillVersion()).append(" | ").append(s.evaluation.totalScore()).append(" |\n");
        List<String> warnings = regressionWarnings(scores); if (!warnings.isEmpty()) { md.append("\n## Regression warnings\n\n"); warnings.forEach(w -> md.append("- ").append(w).append('\n')); }
        writeOutput(output, md.toString(), Map.of("schemaVersion", 1, "generatedAt", Instant.now().toString(), "kind", kind, "runs", ids, "warnings", warnings));
    }

    void report(List<String> ids, boolean all, Path output) throws IOException {
        if (all) ids = discoverScored(); if (ids.isEmpty()) throw new IOException("No scored runs found");
        List<RunScore> scores = ids.stream().map(this::loadScore).toList(); Map<String, Set<String>> tags = new TreeMap<>();
        for (RunScore s : scores) for (Models.Finding f : s.evaluation.findings()) tags.computeIfAbsent(f.tag(), k -> new TreeSet<>()).add(s.manifest.familyId());
        List<Map<String,Object>> recurring = new ArrayList<>(), tuning = new ArrayList<>(); for (var e : tags.entrySet()) if (e.getValue().size() >= 2) { recurring.add(Map.of("tag", e.getKey(), "families", e.getValue())); tuning.add(Map.of("tag", e.getKey(), "families", e.getValue(), "reason", "same category in at least two unrelated families")); }
        StringBuilder md = new StringBuilder("# Evaluation report\n\n"); md.append("Generated: ").append(Instant.now()).append("\n\n## Runs\n\n| Run | Case | Model | Score |\n|---|---|---|---:|\n"); for (RunScore s : scores) md.append("| ").append(s.manifest.runId()).append(" | ").append(s.manifest.caseId()).append(" | ").append(s.manifest.modelLabel()).append(" | ").append(s.evaluation.totalScore()).append(" |\n"); md.append("\n## Recurring failures\n\n"); if (recurring.isEmpty()) md.append("None.\n"); else for (var x : recurring) md.append("- ").append(x.get("tag")).append(" across ").append(x.get("families")).append('\n'); md.append("\n## Tuning candidates\n\n"); if (tuning.isEmpty()) md.append("None supported by the policy threshold.\n"); else for (var x : tuning) md.append("- ").append(x.get("tag")).append(" across ").append(x.get("families")).append('\n');
        writeOutput(output, md.toString(), Map.of("schemaVersion", 1, "generatedAt", Instant.now().toString(), "runs", ids, "warnings", List.of(), "recurringFailures", recurring, "tuningCandidates", tuning));
    }

    private List<String> discoverScored() throws IOException { List<String> ids = new ArrayList<>(); Path runs = store.root.resolve("runs"); if (!Files.isDirectory(runs)) return ids; try (var stream = Files.list(runs)) {
        for (Path p : stream.filter(Files::isDirectory).sorted().toList()) { Path status = p.resolve("status.json"); if (Files.isRegularFile(status) && Models.Status.SCORED.name().equals(JsonFiles.object(status).get("status").getAsString())) ids.add(p.getFileName().toString()); }
    } return ids; }
    private RunScore loadScore(String id) { try { Models.RunManifest m = store.manifest(id); Models.Evaluation e = JsonFiles.read(store.requireRun(id).resolve("evaluation/initial/evaluation.json"), Models.Evaluation.class); return new RunScore(m, e); } catch (IOException e) { throw new IllegalArgumentException(e.getMessage(), e); } }
    private void ensureCompatible(String kind, Models.RunManifest a, Models.RunManifest b) throws IOException {
        boolean sameInputs = a.caseId().equals(b.caseId()) && a.briefHash().equals(b.briefHash()) && a.promptHash().equals(b.promptHash()) && Objects.equals(a.fixtureHash(), b.fixtureHash());
        if ((kind.equals("model") || kind.equals("repeat")) && (!sameInputs || !a.skillHash().equals(b.skillHash()))) throw new IOException("Model/repeat comparison requires identical case and skill inputs");
        if (kind.equals("skill") && (!sameInputs || !a.modelLabel().equals(b.modelLabel()) || a.skillHash().equals(b.skillHash()))) throw new IOException("Skill comparison requires same case/model and different skill hash");
        if (kind.equals("mutation")) {
            if (!b.caseId().equals(a.mutationBaseCaseId()) || !a.skillHash().equals(b.skillHash()) || !a.modelLabel().equals(b.modelLabel())) throw new IOException("Mutation comparison requires base/mutation runs with the same skill and model");
            Path pair = store.root.resolve("mutation-comparisons").resolve(a.runId() + "--" + b.runId());
            if (!Files.isDirectory(pair)) throw new IOException("Prepare and record the mutation evaluation before comparing");
        }
    }
    private List<String> regressionWarnings(List<RunScore> scores) { if (scores.size() < 2) return List.of(); int baseline = scores.get(0).evaluation.totalScore(); List<String> out = new ArrayList<>(); for (int i = 1; i < scores.size(); i++) { int delta = scores.get(i).evaluation.totalScore() - baseline; if (delta <= -2) out.add(scores.get(i).manifest.runId() + " drops " + (-delta) + " total points"); } return out; }
    private void writeOutput(Path requested, String markdown, Map<String,Object> json) throws IOException {
        Path base = requested == null ? store.root.resolve("reports").resolve("report-" + Instant.now().toString().replaceAll("[:.]", "-")) : requested;
        Path md = base.toString().endsWith(".json") ? base.resolveSibling(base.getFileName().toString().replace(".json", ".md")) : (base.toString().endsWith(".md") ? base : base.resolveSibling(base.getFileName() + ".md"));
        Path machine = base.toString().endsWith(".json") ? base : base.resolveSibling(base.getFileName().toString().replaceAll("\\.md$", "") + ".json");
        Files.createDirectories(md.getParent()); Files.writeString(md, markdown); JsonFiles.write(machine, json); System.out.println("Report written: " + md + " and " + machine);
    }
    private record RunScore(Models.RunManifest manifest, Models.Evaluation evaluation) {}
}
