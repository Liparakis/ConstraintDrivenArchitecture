package dev.cda.evals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DualModelWorkflowTest {
    @Test void projectWorkflowIsConfiguredAndPublicInputsHashIdentically(@TempDir Path temp) throws Exception {
        Path evalRoot = Path.of("..").toAbsolutePath().normalize();
        Path skillRoot = evalRoot.getParent();
        DualModelConfig.validate(skillRoot);
        WorkspaceStore store = new WorkspaceStore(temp.resolve("work"));
        RunCommands commands = new RunCommands(evalRoot, skillRoot, store);
        commands.snapshot("test", "");
        String luna = commands.prepare("01-appointment-booking", "test", "gpt-5.6-luna", "high");
        String sol = commands.prepare("01-appointment-booking", "test", "gpt-5.6-sol", "high");
        commands.incomplete(sol, "agent failure");
        assertEquals(Models.Status.INCOMPLETE.name(), store.status(sol));
        Path lunaGeneration = store.run(luna).resolve("generation");
        Path solGeneration = store.run(sol).resolve("generation");
        assertEquals(Files.readString(lunaGeneration.resolve("input-manifest.json")), Files.readString(solGeneration.resolve("input-manifest.json")));
        assertEquals(Files.readString(lunaGeneration.resolve("input-manifest.sha256")), Files.readString(solGeneration.resolve("input-manifest.sha256")));
        assertEquals(Hashing.file(lunaGeneration.resolve("input-manifest.json")), Files.readString(lunaGeneration.resolve("input-manifest.sha256")).trim());
    }
}
