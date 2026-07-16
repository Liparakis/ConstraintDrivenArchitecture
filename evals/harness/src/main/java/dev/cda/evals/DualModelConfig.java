package dev.cda.evals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class DualModelConfig {
    private DualModelConfig() {}

    static void validate(Path repoRoot) throws IOException {
        Path codex = repoRoot.resolve(".codex");
        validateAgent(codex.resolve("agents/cda_luna_tester.toml"), "cda_luna_tester", "gpt-5.6-luna");
        validateAgent(codex.resolve("agents/cda_sol_tester.toml"), "cda_sol_tester", "gpt-5.6-sol");
        String config = read(codex.resolve("config.toml"));
        require(config, "[agents]", codex.resolve("config.toml"));
        require(config, "max_threads = 3", codex.resolve("config.toml"));
        require(config, "max_depth = 1", codex.resolve("config.toml"));

        Path instructions = repoRoot.resolve("AGENTS.md");
        String agents = read(instructions);
        for (String needle : List.of("## Dual-Model Architecture Skill Testing", "test, evaluate, benchmark", "run an architecture evaluation", "run the next eval", "test next", "cda_luna_tester", "cda_sol_tester", "concurrently", "wait", "ordinary unit tests", "Gradle tests", "Never modify the production skill", "Do not automatically tune")) require(agents, needle, instructions);

        Path guide = repoRoot.resolve("evals/DUAL_MODEL_TESTING.md");
        String docs = read(guide);
        for (String needle : List.of("test next", "test case 03", "test mutation for case 02", "run an architecture evaluation", "run the next eval", "input-manifest.sha256", "prepare-run", "concurrently", "wait for both", "blinded", "new scenario", "auto-tune")) require(docs, needle, guide);
        require(read(repoRoot.resolve("evals/README.md")), "DUAL_MODEL_TESTING.md", repoRoot.resolve("evals/README.md"));
    }

    private static void validateAgent(Path file, String name, String model) throws IOException {
        String text = read(file);
        for (String needle : List.of("name = \"" + name + "\"", "description =", "model = \"" + model + "\"", "model_reasoning_effort = \"high\"", "sandbox_mode = \"read-only\"", "developer_instructions = \"\"\"", "public generation workspace", "operating mode", "fixture repository", "expectations", "rubric", "prior outputs", "known weaknesses", "other model", "outside that workspace", "Do not edit", "candidate architecture", "evidence, assumptions, unknowns", "simplest defensible", "unsupported requirements", "rejected alternatives", "fitness functions", "Never mention your model name")) require(text, needle, file);
    }

    private static String read(Path path) throws IOException {
        if (!Files.isRegularFile(path)) throw new IOException("Missing dual-model workflow file: " + path);
        return Files.readString(path);
    }

    private static void require(String text, String needle, Path file) throws IOException {
        if (!text.contains(needle)) throw new IOException("Missing '" + needle + "' in " + file);
    }
}
