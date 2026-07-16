package dev.cda.evals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class RunLifecycleTest {
    @Test void ingestRejectsChangedGenerationInput(@TempDir Path temp) throws Exception {
        Path evalRoot = Path.of("..").toAbsolutePath().normalize(); Path skillRoot = evalRoot.getParent(); WorkspaceStore store = new WorkspaceStore(temp.resolve("work")); RunCommands commands = new RunCommands(evalRoot, skillRoot, store);
        commands.snapshot("test", ""); String id = commands.prepare("01-appointment-booking", "test", "Luna High", "high"); Path generation = store.run(id).resolve("generation"); Files.writeString(generation.resolve("brief.md"), "tampered"); Files.writeString(generation.resolve("candidate.md"), "candidate");
        IOException error = assertThrows(IOException.class, () -> commands.ingest(id, null)); assertTrue(error.getMessage().contains("changed")); assertEquals(Models.Status.INVALID.name(), store.status(id));
    }
}
