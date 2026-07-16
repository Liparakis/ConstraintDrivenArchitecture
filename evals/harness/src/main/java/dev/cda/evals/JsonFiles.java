package dev.cda.evals;

import com.google.gson.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Set;

final class JsonFiles {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();

    static <T> T read(Path path, Class<T> type) throws IOException {
        try { return GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), type); }
        catch (JsonParseException e) { throw new IOException("Invalid JSON in " + path + ": " + e.getMessage(), e); }
    }
    static JsonObject object(Path path) throws IOException {
        try {
            JsonElement e = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            if (!e.isJsonObject()) throw new IOException("Expected JSON object: " + path);
            return e.getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) { throw new IOException("Invalid JSON in " + path + ": " + e.getMessage(), e); }
    }
    static void requireKeys(JsonObject object, Set<String> required, Set<String> allowed, Path path) throws IOException {
        for (String key : required) if (!object.has(key)) throw new IOException(path + ": missing field " + key);
        for (String key : object.keySet()) if (!allowed.contains(key)) throw new IOException(path + ": unknown field " + key);
    }
    static void write(Path path, Object value) throws IOException {
        Files.createDirectories(path.getParent());
        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(temp, GSON.toJson(value), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try { Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException e) { Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING); }
    }
    static String pretty(Object value) { return GSON.toJson(value); }
}
