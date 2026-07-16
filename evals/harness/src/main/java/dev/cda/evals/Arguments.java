package dev.cda.evals;

import java.util.*;

final class Arguments {
    final String command;
    final Map<String, String> values;
    final Set<String> flags;

    private Arguments(String command, Map<String, String> values, Set<String> flags) {
        this.command = command; this.values = values; this.flags = flags;
    }

    static Arguments parse(String[] raw) {
        if (raw.length == 0) return new Arguments("help", Map.of(), Set.of());
        String command = raw[0];
        Map<String, String> values = new LinkedHashMap<>();
        Set<String> flags = new LinkedHashSet<>();
        for (int i = 1; i < raw.length; i++) {
            String token = raw[i];
            if (!token.startsWith("--")) throw new IllegalArgumentException("Expected option, got: " + token);
            String key = token.substring(2);
            if (key.isBlank()) throw new IllegalArgumentException("Empty option");
            if (i + 1 < raw.length && !raw[i + 1].startsWith("--")) {
                if (values.put(key, raw[++i]) != null || flags.contains(key)) throw new IllegalArgumentException("Duplicate option: --" + key);
            } else if (!flags.add(key) || values.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate option: --" + key);
            }
        }
        return new Arguments(command, Map.copyOf(values), Set.copyOf(flags));
    }

    String required(String name) { return values.get(name) == null ? fail("Missing required option --" + name) : values.get(name); }
    String optional(String name, String fallback) { return values.getOrDefault(name, fallback); }
    boolean has(String name) { return flags.contains(name) || values.containsKey(name); }
    private static <T> T fail(String message) { throw new IllegalArgumentException(message); }
}
