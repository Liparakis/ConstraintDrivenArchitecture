package dev.cda.evals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EvaluationValidationTest {
    @Test void blindedPackageAndStrictScoreValidation(@TempDir Path temp) throws Exception {
        Path evalRoot = Path.of("..").toAbsolutePath().normalize(); Path skillRoot = evalRoot.getParent(); WorkspaceStore store = new WorkspaceStore(temp.resolve("work")); RunCommands runs = new RunCommands(evalRoot, skillRoot, store); EvaluationCommands eval = new EvaluationCommands(evalRoot, store);
        runs.snapshot("test", ""); String id = runs.prepare("01-appointment-booking", "test", "Luna High", "high"); Path candidate = store.run(id).resolve("generation/candidate.md"); Files.writeString(candidate, "# Candidate\nA compact architecture."); runs.ingest(id, null); eval.prepare(id);
        Path packageDir = store.run(id).resolve("evaluation/initial"); assertTrue(Files.exists(packageDir.resolve("expectations.json"))); assertTrue(Files.exists(packageDir.resolve("candidate.md"))); assertFalse(Files.readString(packageDir.resolve("evaluator-prompt.md")).contains("Luna High"));
        String blinded = Files.walk(packageDir).filter(Files::isRegularFile).map(p -> { try { return Files.readString(p); } catch (Exception e) { throw new RuntimeException(e); } }).reduce("", String::concat); assertFalse(blinded.contains("Luna High")); assertFalse(blinded.contains("gpt-5.6-luna")); assertFalse(blinded.contains("Sol High")); assertFalse(blinded.contains("gpt-5.6-sol"));
        List<Models.Score> scores = EvaluationCommands.CATEGORIES.stream().map(c -> new Models.Score(c, 1, "partial", List.of("candidate"), "none")).toList(); Models.Evaluation response = new Models.Evaluation(1, JsonFiles.object(packageDir.resolve("package-manifest.json")).get("candidateId").getAsString(), scores, 10, List.of(), List.of("concise"), List.of(), Models.Source.UNDETERMINED, null, false); Path responseFile = temp.resolve("evaluation.json"); JsonFiles.write(responseFile, response); eval.record(id, responseFile, "Sol High"); assertEquals(Models.Status.SCORED.name(), store.status(id));
    }
    @Test void scoreTotalMismatchIsRejected(@TempDir Path temp) throws Exception {
        Path file = temp.resolve("bad.json"); List<Models.Score> scores = EvaluationCommands.CATEGORIES.stream().map(c -> new Models.Score(c, 1, "x", List.of("x"), "")).toList(); JsonFiles.write(file, new Models.Evaluation(1, "candidate-x", scores, 9, List.of(), List.of(), List.of(), Models.Source.UNDETERMINED, null, false));
        Path evalRoot = Path.of("..").toAbsolutePath().normalize(); WorkspaceStore store = new WorkspaceStore(temp.resolve("work")); EvaluationCommands commands = new EvaluationCommands(evalRoot, store); assertThrows(java.io.IOException.class, () -> commands.record("missing", file, "evaluator"));
    }
}
