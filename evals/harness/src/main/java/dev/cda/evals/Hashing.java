package dev.cda.evals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

final class Hashing {
    static String file(Path path) throws IOException { return hex(digest(Files.readAllBytes(path))); }
    static String combined(Map<String, String> files) {
        MessageDigest d = sha();
        files.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
            d.update(e.getKey().getBytes(StandardCharsets.UTF_8)); d.update((byte) 0);
            d.update(hexBytes(e.getValue()));
        });
        return hex(d.digest());
    }
    static Map<String, String> tree(Path root) throws IOException {
        if (!Files.isDirectory(root)) throw new IOException("Missing directory: " + root);
        Map<String, String> result = new TreeMap<>();
        try (var stream = Files.walk(root)) {
            for (Path p : stream.sorted().toList()) {
                if (!Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) continue;
                String rel = root.relativize(p).toString().replace('\\', '/');
                result.put(rel, file(p));
            }
        }
        return result;
    }
    static String hex(byte[] bytes) { StringBuilder s = new StringBuilder(); for (byte b : bytes) s.append(String.format("%02x", b)); return s.toString(); }
    private static byte[] hexBytes(String value) { byte[] out = new byte[value.length() / 2]; for (int i = 0; i < out.length; i++) out[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16); return out; }
    private static MessageDigest sha() { try { return MessageDigest.getInstance("SHA-256"); } catch (Exception e) { throw new AssertionError(e); } }
    private static byte[] digest(byte[] bytes) { return sha().digest(bytes); }
}
