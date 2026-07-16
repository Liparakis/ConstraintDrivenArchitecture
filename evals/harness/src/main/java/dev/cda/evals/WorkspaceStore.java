package dev.cda.evals;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

final class WorkspaceStore {
    final Path root;
    WorkspaceStore(Path root) { this.root = root.toAbsolutePath().normalize(); }
    Path snapshots() { return root.resolve("snapshots"); }
    Path run(String id) { return root.resolve("runs").resolve(id); }
    Path requireRun(String id) throws IOException { Path p = run(id); if (!Files.isDirectory(p)) throw new IOException("Unknown run: " + id); return p; }
    Models.RunManifest manifest(String id) throws IOException { return JsonFiles.read(requireRun(id).resolve("manifest.json"), Models.RunManifest.class); }
    String status(String id) throws IOException { JsonObject o = JsonFiles.object(requireRun(id).resolve("status.json")); return o.get("status").getAsString(); }
    void status(String id, String status, Map<String, ?> fields) throws IOException {
        Map<String, Object> data = new LinkedHashMap<>(); data.put("status", status); data.putAll(fields); JsonFiles.write(requireRun(id).resolve("status.json"), data);
    }
    static Path defaultRoot(String explicit) {
        if (explicit != null) return Path.of(explicit);
        String env = System.getenv("CDA_EVALS_HOME"); if (env != null && !env.isBlank()) return Path.of(env);
        String local = System.getenv("LOCALAPPDATA"); if (local != null && !local.isBlank()) return Path.of(local, "ConstraintDrivenArchitectureEvals");
        return Path.of(System.getProperty("user.home"), ".constraint-driven-architecture-evals");
    }
}
