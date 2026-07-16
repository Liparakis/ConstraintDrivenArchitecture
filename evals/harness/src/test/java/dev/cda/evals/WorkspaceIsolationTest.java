package dev.cda.evals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class WorkspaceIsolationTest {
    @Test void preparedGenerationWorkspaceContainsNoPrivateEvaluatorMaterial(@TempDir Path temp) throws Exception {
        Path evalRoot = Path.of("..").toAbsolutePath().normalize(); Path skillRoot = evalRoot.getParent(); WorkspaceStore store = new WorkspaceStore(temp.resolve("work"));
        RunCommands commands = new RunCommands(evalRoot, skillRoot, store); commands.snapshot("test", ""); String id = commands.prepare("01-appointment-booking", "test", "Luna High", "high");
        Path generation = store.run(id).resolve("generation"); String all = Files.walk(generation).filter(Files::isRegularFile).map(p -> { try { return Files.readString(p); } catch (Exception e) { throw new RuntimeException(e); } }).reduce("", String::concat);
        assertFalse(all.contains("requiredReasoning")); assertFalse(all.contains("OVERENGINEERING")); assertFalse(all.contains("CONSTRAINT_GROUNDING")); assertFalse(all.contains("gpt-5.6-luna")); assertFalse(all.contains("gpt-5.6-sol")); assertFalse(Files.exists(generation.resolve("expectations.json"))); assertFalse(Files.exists(generation.resolve("rubric.md"))); assertFalse(Files.exists(generation.resolve("failure-taxonomy.md"))); assertFalse(Files.exists(generation.resolve("evaluator-prompt.md")));
    }
}
