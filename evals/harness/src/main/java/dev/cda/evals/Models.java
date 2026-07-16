package dev.cda.evals;

import java.util.List;
import java.util.Map;

final class Models {
    enum Mode { GREENFIELD, EVOLUTION, REVIEW }
    enum Status { PREPARED, INGESTED, EVALUATION_PREPARED, SCORED, INCOMPLETE, INVALID }
    enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
    enum Confidence { LOW, MEDIUM, HIGH }
    enum Source { MODEL_JUDGMENT, SKILL_INSTRUCTION, BENCHMARK_DEFECT, UNKNOWN_CONSTRAINT, UNDETERMINED }

    record CaseDef(int schemaVersion, String id, String familyId, String title, String version, Mode mode,
                   String brief, String prompt, String expectations, String fixture) {}
    record MutationDef(int schemaVersion, String id, String baseCaseId, String familyId, String title,
                       String version, String change, String expectations, List<String> affectedDimensions,
                       List<String> stableDimensions) {}
    record Snapshot(int schemaVersion, String label, String createdAt, Map<String, String> files,
                    String combinedHash, List<String> excludedPaths, String notes) {}
    record RunManifest(int schemaVersion, String runId, String caseId, String caseVersion, String familyId,
                       Mode mode, String modelLabel, String reasoningLabel, String skillVersion,
                       String skillHash, String briefHash, String promptHash, String fixtureHash,
                       String mutationBaseCaseId, String createdAt) {}
    record Score(String category, int score, String justification, List<String> evidence, String uncertainty) {}
    record Finding(String tag, Severity severity, Confidence confidence, String summary, List<String> evidence) {}
    record Evaluation(int schemaVersion, String candidateId, List<Score> scores, int totalScore,
                      List<Finding> findings, List<String> strengths, List<String> architectureChangingUnknowns,
                      Source suspectedSource, String smallestGeneralSkillChange, boolean rerunRequired) {}
    record HumanReview(int schemaVersion, String reviewer, String disposition, String rationale, Integer adjustedTotal) {}
    record CaseEntry(CaseDef base, MutationDef mutation, java.nio.file.Path caseDir) {
        String id() { return mutation == null ? base.id() : mutation.id(); }
        String familyId() { return mutation == null ? base.familyId() : mutation.familyId(); }
        String version() { return mutation == null ? base.version() : mutation.version(); }
        Mode mode() { return base.mode(); }
        String title() { return mutation == null ? base.title() : mutation.title(); }
        boolean isMutation() { return mutation != null; }
    }
}
