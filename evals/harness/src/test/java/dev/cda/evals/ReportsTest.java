package dev.cda.evals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportsTest {
    @Test void reportCanBeGeneratedFromScoredRun(@TempDir Path temp) throws Exception {
        Path evalRoot = Path.of("..").toAbsolutePath().normalize(); Path skillRoot = evalRoot.getParent(); WorkspaceStore store = new WorkspaceStore(temp.resolve("work")); RunCommands runs = new RunCommands(evalRoot, skillRoot, store); EvaluationCommands eval = new EvaluationCommands(evalRoot, store);
        runs.snapshot("test", ""); String id = runs.prepare("01-appointment-booking", "test", "Luna High", "high"); Files.writeString(store.run(id).resolve("generation/candidate.md"), "candidate"); runs.ingest(id, null); eval.prepare(id); Path packageDir = store.run(id).resolve("evaluation/initial"); String cid = JsonFiles.object(packageDir.resolve("package-manifest.json")).get("candidateId").getAsString(); List<Models.Score> scores = EvaluationCommands.CATEGORIES.stream().map(c -> new Models.Score(c, 2, "strong", List.of("candidate"), "")).toList(); JsonFiles.write(temp.resolve("good.json"), new Models.Evaluation(1, cid, scores, 20, List.of(), List.of("good"), List.of(), Models.Source.UNDETERMINED, null, false)); eval.record(id, temp.resolve("good.json"), "Sol High");
        new Reports(store).report(List.of(id), false, temp.resolve("report.md")); assertTrue(Files.exists(temp.resolve("report.md"))); assertTrue(Files.exists(temp.resolve("report.json"))); assertTrue(Files.readString(temp.resolve("report.md")).contains(id));
    }

    @Test void recurringFailuresAndRegressionWarningsAreReported(@TempDir Path temp) throws Exception {
        Path evalRoot = Path.of("..").toAbsolutePath().normalize(); Path skillRoot = evalRoot.getParent(); WorkspaceStore store = new WorkspaceStore(temp.resolve("work")); RunCommands runs = new RunCommands(evalRoot, skillRoot, store); EvaluationCommands eval = new EvaluationCommands(evalRoot, store);
        runs.snapshot("test", ""); String familyA = scored(runs, eval, store, "01-appointment-booking", 20, true, "Luna High"); String familyB = scored(runs, eval, store, "02-offline-document-indexer", 18, true, "Luna High");
        new Reports(store).report(List.of(), true, temp.resolve("all.md")); String all = Files.readString(temp.resolve("all.md")); assertTrue(all.contains("OVERENGINEERING")); assertTrue(all.contains("Tuning candidates"));
        String first = scored(runs, eval, store, "03-batch-file-converter-cli", 20, false, "Luna High"); String second = scored(runs, eval, store, "03-batch-file-converter-cli", 17, false, "Sol High"); new Reports(store).compare("model", List.of(first, second), temp.resolve("compare.md")); assertTrue(Files.readString(temp.resolve("compare.md")).contains("Regression warnings"));
    }

    private static String scored(RunCommands runs, EvaluationCommands eval, WorkspaceStore store, String caseId, int total, boolean finding, String model) throws Exception {
        String id = runs.prepare(caseId, "test", model, "high"); Files.writeString(store.run(id).resolve("generation/candidate.md"), "candidate"); runs.ingest(id, null); eval.prepare(id); Path pkg = store.run(id).resolve("evaluation/initial"); String cid = JsonFiles.object(pkg.resolve("package-manifest.json")).get("candidateId").getAsString();
        int base = total / 10, remainder = total - (base * 10); List<Models.Score> scores = new ArrayList<>(); for (int i = 0; i < EvaluationCommands.CATEGORIES.size(); i++) scores.add(new Models.Score(EvaluationCommands.CATEGORIES.get(i), base + (i < remainder ? 1 : 0), "x", List.of("candidate"), "")); List<Models.Finding> findings = finding ? List.of(new Models.Finding("OVERENGINEERING", Models.Severity.MEDIUM, Models.Confidence.HIGH, "too much", List.of("candidate"))) : List.of(); Path response = store.root.resolve(id + ".json"); JsonFiles.write(response, new Models.Evaluation(1, cid, scores, total, findings, List.of(), List.of(), Models.Source.UNDETERMINED, null, false)); eval.record(id, response, "manual"); return id;
    }
}
