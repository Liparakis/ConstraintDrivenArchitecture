package dev.cda.evals;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

final class EvaluationCommands {
    static final List<String> CATEGORIES = List.of("CONSTRAINT_GROUNDING","PROPORTIONALITY","CORE_INVARIANT_IDENTIFICATION","FAILURE_AND_RECOVERY_SEMANTICS","SECURITY_AND_ENFORCEMENT_BOUNDARY_ACCURACY","CAPABILITY_FIRST_TECHNOLOGY_SELECTION","ALTERNATIVE_QUALITY_AND_REJECTION_REASONING","IMPLEMENTATION_USEFULNESS","ARCHITECTURE_FITNESS_FUNCTIONS","UNCERTAINTY_AND_MEASUREMENT_HANDLING");
    static final Set<String> TAGS = Set.of("OVERENGINEERING","UNDERENGINEERING","TOOL_FIRST_SELECTION","FALSE_ENFORCEMENT_CLAIM","MISSING_CORE_INVARIANT","UNSUPPORTED_CERTAINTY","FAILURE_SEMANTICS_GAP","SECURITY_BOUNDARY_GAP","DATA_OWNERSHIP_GAP","MIGRATION_GAP","OPERATIONAL_MISMATCH","MODE_MISCLASSIFICATION","REPOSITORY_EVIDENCE_GAP","DECORATIVE_DOCUMENTATION","UNJUSTIFIED_JAVA_SELECTION","WEB_STACK_LEAKAGE");
    private final Path evalRoot;
    private final CaseCatalog catalog;
    private final WorkspaceStore store;

    EvaluationCommands(Path evalRoot, WorkspaceStore store) { this.evalRoot = evalRoot; this.catalog = new CaseCatalog(evalRoot); this.store = store; }

    String prepare(String runId) throws IOException {
        Path run = store.requireRun(runId); if (!Models.Status.INGESTED.name().equals(store.status(runId))) throw new IOException("Run must be INGESTED before evaluation");
        Models.RunManifest manifest = store.manifest(runId); Models.CaseEntry entry = catalog.find(manifest.caseId());
        String candidateId = "candidate-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Path packageDir = run.resolve("evaluation/initial"); Files.createDirectories(packageDir);
        copy(run.resolve("result/candidate.md"), packageDir.resolve("candidate.md"));
        copy(catalog.expectationsPath(entry), packageDir.resolve("expectations.json"));
        copy(evalRoot.resolve("rubric.md"), packageDir.resolve("rubric.md")); copy(evalRoot.resolve("failure-taxonomy.md"), packageDir.resolve("failure-taxonomy.md")); copy(evalRoot.resolve("prompts/evaluator.md"), packageDir.resolve("evaluator-prompt.md")); copy(evalRoot.resolve("schemas/evaluation.schema.json"), packageDir.resolve("evaluation.schema.json"));
        Map<String,Object> packageManifest = new LinkedHashMap<>(); packageManifest.put("schemaVersion", 1); packageManifest.put("candidateId", candidateId); packageManifest.put("caseId", manifest.caseId()); packageManifest.put("caseVersion", manifest.caseVersion()); packageManifest.put("files", Hashing.tree(packageDir));
        JsonFiles.write(packageDir.resolve("package-manifest.json"), packageManifest); JsonFiles.write(run.resolve("status.json"), Map.of("status", Models.Status.EVALUATION_PREPARED.name(), "candidateId", candidateId, "preparedAt", Instant.now().toString()));
        System.out.println("Blinded evaluator package: " + packageDir + " (" + candidateId + ")"); return candidateId;
    }

    void record(String runId, Path evaluationFile, String evaluatorLabel) throws IOException {
        Path run = store.requireRun(runId); String status = store.status(runId); if (!Models.Status.EVALUATION_PREPARED.name().equals(status) && !Models.Status.SCORED.name().equals(status)) throw new IOException("Run must have an evaluation package first");
        EvaluationData data = readEvaluation(evaluationFile); JsonObject packageManifest = JsonFiles.object(run.resolve("evaluation/initial/package-manifest.json")); String expectedId = packageManifest.get("candidateId").getAsString(); if (!expectedId.equals(data.evaluation.candidateId())) throw new IOException("Candidate ID mismatch");
        Path target = run.resolve("evaluation/initial/evaluation.json"); if (!evaluationFile.toAbsolutePath().normalize().equals(target.toAbsolutePath().normalize())) copy(evaluationFile, target);
        JsonFiles.write(run.resolve("evaluation/initial/evaluator-metadata.json"), Map.of("evaluatorLabel", evaluatorLabel, "recordedAt", Instant.now().toString()));
        JsonFiles.write(run.resolve("status.json"), Map.of("status", Models.Status.SCORED.name(), "candidateId", data.evaluation.candidateId(), "totalScore", data.evaluation.totalScore(), "recordedAt", Instant.now().toString()));
        System.out.println("Evaluation recorded for " + runId + " total=" + data.evaluation.totalScore());
    }

    void human(String runId, Path reviewFile, String reviewer) throws IOException {
        HumanReviewData data = readHuman(reviewFile); Path run = store.requireRun(runId); copy(reviewFile, run.resolve("human-review.json")); JsonFiles.write(run.resolve("human-review-meta.json"), Map.of("reviewer", reviewer, "recordedAt", Instant.now().toString(), "disposition", data.review.disposition())); System.out.println("Human review recorded for " + runId);
    }

    String prepareMutation(String baseId, String mutationId) throws IOException {
        Models.RunManifest base = store.manifest(baseId), mutation = store.manifest(mutationId); if (mutation.mutationBaseCaseId() == null || !mutation.mutationBaseCaseId().equals(base.caseId())) throw new IOException("Runs are not a declared mutation pair");
        if (!base.skillHash().equals(mutation.skillHash()) || !base.modelLabel().equals(mutation.modelLabel())) throw new IOException("Mutation runs must use the same skill and model");
        if (!Models.Status.SCORED.name().equals(store.status(baseId)) || !Models.Status.SCORED.name().equals(store.status(mutationId))) throw new IOException("Both runs must be independently scored first");
        String pair = baseId + "--" + mutationId; Path dir = store.root.resolve("mutation-comparisons").resolve(pair); Files.createDirectories(dir);
        copy(store.requireRun(baseId).resolve("result/candidate.md"), dir.resolve("base-candidate.md")); copy(store.requireRun(mutationId).resolve("result/candidate.md"), dir.resolve("mutation-candidate.md")); copy(evalRoot.resolve("prompts/mutation-evaluator.md"), dir.resolve("mutation-evaluator-prompt.md")); copy(evalRoot.resolve("schemas/mutation-evaluation.schema.json"), dir.resolve("mutation-evaluation.schema.json")); JsonFiles.write(dir.resolve("pair.json"), Map.of("schemaVersion", 1, "baseRun", baseId, "mutationRun", mutationId, "baseCase", base.caseId(), "mutationCase", mutation.caseId())); System.out.println("Mutation evaluator package: " + dir); return pair;
    }

    void recordMutation(String baseId, String mutationId, Path file, String evaluatorLabel) throws IOException {
        Path dir = store.root.resolve("mutation-comparisons").resolve(baseId + "--" + mutationId); if (!Files.isDirectory(dir)) throw new IOException("Prepare mutation evaluation first"); JsonObject o = JsonFiles.object(file); JsonFiles.requireKeys(o, Set.of("schemaVersion","baseCandidateId","mutationCandidateId","affectedDimensions","stableDimensions","unknowns"), Set.of("schemaVersion","baseCandidateId","mutationCandidateId","affectedDimensions","stableDimensions","unknowns"), file); copy(file, dir.resolve("mutation-evaluation.json")); JsonFiles.write(dir.resolve("evaluator-metadata.json"), Map.of("evaluatorLabel", evaluatorLabel, "recordedAt", Instant.now().toString())); System.out.println("Mutation evaluation recorded: " + dir);
    }

    private EvaluationData readEvaluation(Path file) throws IOException {
        JsonObject o = JsonFiles.object(file); JsonFiles.requireKeys(o, Set.of("schemaVersion","candidateId","scores","totalScore","findings","strengths","architectureChangingUnknowns","suspectedSource","smallestGeneralSkillChange","rerunRequired"), Set.of("schemaVersion","candidateId","scores","totalScore","findings","strengths","architectureChangingUnknowns","suspectedSource","smallestGeneralSkillChange","rerunRequired"), file);
        Models.Evaluation e = JsonFiles.read(file, Models.Evaluation.class); if (e.scores() == null || e.scores().size() != 10) throw new IOException("Evaluation must contain exactly ten scores"); Set<String> seen = new HashSet<>(); int total = 0; for (Models.Score s : e.scores()) { if (!CATEGORIES.contains(s.category()) || !seen.add(s.category())) throw new IOException("Invalid or duplicate rubric category: " + s.category()); if (s.score() < 0 || s.score() > 2) throw new IOException("Score outside 0..2: " + s.category()); total += s.score(); } if (total != e.totalScore()) throw new IOException("Declared total does not equal score sum"); for (Models.Finding f : e.findings()) if (!TAGS.contains(f.tag())) throw new IOException("Unknown failure tag: " + f.tag()); return new EvaluationData(e);
    }
    private HumanReviewData readHuman(Path file) throws IOException { JsonObject o = JsonFiles.object(file); JsonFiles.requireKeys(o, Set.of("schemaVersion","reviewer","disposition","rationale"), Set.of("schemaVersion","reviewer","disposition","rationale","adjustedTotal"), file); return new HumanReviewData(JsonFiles.read(file, Models.HumanReview.class)); }
    private static void copy(Path from, Path to) throws IOException { Files.createDirectories(to.getParent()); if (!from.toAbsolutePath().normalize().equals(to.toAbsolutePath().normalize())) Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING); }
    private record EvaluationData(Models.Evaluation evaluation) {}
    private record HumanReviewData(Models.HumanReview review) {}
}
