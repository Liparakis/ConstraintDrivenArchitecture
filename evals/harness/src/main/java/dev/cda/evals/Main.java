package dev.cda.evals;

import java.nio.file.*;
import java.util.*;

public final class Main {
    private Main() {}
    public static void main(String[] args) { int code = execute(args); if (code != 0) System.exit(code); }
    static int execute(String[] raw) {
        try {
            Arguments a = Arguments.parse(raw); if (a.has("help") || a.command.equals("help") || a.command.equals("--help")) { help(); return 0; }
            Path evalRoot = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize().getParent();
            if (evalRoot == null || !Files.isDirectory(evalRoot.resolve("cases"))) evalRoot = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize().resolve("..");
            Path skillRoot = Path.of(a.optional("skill-root", evalRoot.getParent().toString())).toAbsolutePath().normalize();
            WorkspaceStore store = new WorkspaceStore(WorkspaceStore.defaultRoot(a.optional("work-dir", null)));
            RunCommands runs = new RunCommands(evalRoot, skillRoot, store); EvaluationCommands evaluations = new EvaluationCommands(evalRoot, store); Reports reports = new Reports(store);
            switch (a.command) {
                case "validate" -> runs.validate();
                case "snapshot-skill" -> runs.snapshot(a.required("label"), a.optional("notes", ""));
                case "list-cases" -> runs.listCases(a.values.get("mode"));
                case "prepare-run" -> runs.prepare(a.required("case"), a.required("snapshot"), a.required("model"), a.required("reasoning"));
                case "ingest-result" -> runs.ingest(a.required("run"), a.values.get("file"));
                case "mark-incomplete" -> runs.incomplete(a.required("run"), a.required("reason"));
                case "prepare-evaluation" -> { if (a.values.containsKey("run")) evaluations.prepare(a.required("run")); else evaluations.prepareMutation(a.required("base-run"), a.required("mutation-run")); }
                case "record-evaluation" -> { if (a.values.containsKey("run")) { evaluations.record(a.required("run"), Path.of(a.required("file")), a.required("evaluator-label")); if (a.values.containsKey("human-review")) evaluations.human(a.required("run"), Path.of(a.required("human-review")), a.required("reviewer")); } else evaluations.recordMutation(a.required("base-run"), a.required("mutation-run"), Path.of(a.required("file")), a.required("evaluator-label")); }
                case "compare" -> reports.compare(a.required("kind"), Arrays.asList(a.required("runs").split(",")), a.values.containsKey("output") ? Path.of(a.values.get("output")) : null);
                case "report" -> reports.report(a.values.containsKey("runs") ? Arrays.asList(a.values.get("runs").split(",")) : List.of(), a.has("all"), a.values.containsKey("output") ? Path.of(a.values.get("output")) : null);
                default -> throw new IllegalArgumentException("Unknown command: " + a.command + ". Use --help.");
            }
            return 0;
        } catch (Exception e) { System.err.println("ERROR: " + e.getMessage()); return 2; }
    }
    private static void help() {
        System.out.println("cda-evals commands:\n" +
                "  validate --skill-root PATH [--work-dir PATH]\n" +
                "  snapshot-skill --label LABEL [--notes TEXT]\n" +
                "  list-cases [--mode MODE]\n" +
                "  prepare-run --case ID --snapshot LABEL --model LABEL --reasoning LABEL\n" +
                "  ingest-result --run ID [--file PATH]\n" +
                "  mark-incomplete --run ID --reason TEXT\n" +
                "  prepare-evaluation --run ID | --base-run ID --mutation-run ID\n" +
                "  record-evaluation --run ID --file PATH --evaluator-label LABEL\n" +
                "  compare --kind model|skill|mutation|repeat --runs ID,ID[,ID]\n" +
                "  report --all | --runs ID,ID[,ID]\n" +
                "Options: --work-dir overrides %LOCALAPPDATA%\\ConstraintDrivenArchitectureEvals.");
    }
}
