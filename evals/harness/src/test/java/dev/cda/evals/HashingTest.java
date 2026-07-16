package dev.cda.evals;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class HashingTest {
    @Test void combinedHashIsStableAndOrderIndependent() {
        Map<String,String> a = Map.of("b", "00".repeat(32), "a", "11".repeat(32));
        assertEquals(Hashing.combined(a), Hashing.combined(Map.of("a", a.get("a"), "b", a.get("b"))));
    }
    @Test void treeExcludesNothingByItselfButSnapshotSelectionCanExcludeEvalArtifacts(@org.junit.jupiter.api.io.TempDir Path temp) throws Exception {
        Files.createDirectories(temp.resolve("evals")); Files.writeString(temp.resolve("SKILL.md"), "skill"); Files.writeString(temp.resolve("evals/secret"), "secret");
        Map<String,String> tree = Hashing.tree(temp); assertTrue(tree.containsKey("evals/secret"));
        Map<String,String> selected = tree.entrySet().stream().filter(e -> !e.getKey().startsWith("evals/")).collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertFalse(selected.containsKey("evals/secret")); assertNotEquals(Hashing.combined(tree), Hashing.combined(selected));
    }
}
