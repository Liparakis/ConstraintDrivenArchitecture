# Dual-model architecture-skill testing

This is a test-only workflow for comparing two isolated architecture generations. It never changes `SKILL.md`, `agents/openai.yaml`, or `references/*`, and it does not invoke an LLM itself; the parent Codex orchestrates the two custom agents.

## When it triggers

Use it for a request to test, evaluate, benchmark, or run the next evaluation of this skill or its harness: `test next`, `test case 03`, `test mutation for case 02`, `compare Luna and Sol on case 05`, `run an architecture evaluation`, `run the next eval`, or `test a new scenario`. A bare `test` is relevant only when the active context is this skill root or its `evals` directory. Ordinary unit, Gradle, script, build, and application tests are excluded.

## Case selection

An explicit case or mutation wins. For `test a new scenario`, create and validate a case using the existing case schema before preparing either agent package. Otherwise inspect external `runs/*/manifest.json` and `status.json`, use stable catalog order, and prefer a base case before its mutation. Select the first case not completed for both exact model labels under the current skill snapshot. If every case is complete, report suite completion and ask for an explicit regression or new scenario; do not silently rerun or tune.

## Agents and isolation

The project-local agents are `.codex/agents/cda_luna_tester.toml` (`gpt-5.6-luna`, high reasoning) and `.codex/agents/cda_sol_tester.toml` (`gpt-5.6-sol`, high reasoning). Both are read-only and receive the same immutable public package: brief, generation prompt, optional fixture, public metadata, and the installed skill snapshot. Spawn both concurrently (in parallel), wait for both, and do not send either agent private expectations, rubric, taxonomy, evaluator prompt, prior output, score, report, model label, or comparison metadata. The tester response must not mention model identity.

## Existing harness workflow

Run from `evals/harness`; the work directory is external by default (`%LOCALAPPDATA%\ConstraintDrivenArchitectureEvals`). The parent prepares the same case twice, with only the private model metadata differing:

```powershell
Set-Location C:\Users\Liparakis\Desktop\constrain-driven-architecture\evals\harness
.\gradlew.bat clean test installDist
$cli = ".\build\install\cda-evals\bin\cda-evals.bat"
$work = "$env:LOCALAPPDATA\ConstraintDrivenArchitectureEvals"
& $cli validate --skill-root "..\.." --work-dir $work
& $cli snapshot-skill --skill-root "..\.." --label v0.1 --work-dir $work
& $cli prepare-run --case 03-batch-file-converter-cli --snapshot v0.1 --model gpt-5.6-luna --reasoning high --work-dir $work
& $cli prepare-run --case 03-batch-file-converter-cli --snapshot v0.1 --model gpt-5.6-sol --reasoning high --work-dir $work
```

Before spawning, treat the first prepared `generation/input-manifest.json` as canonical and require the second to match it byte-for-byte. Compare both manifests and their `input-manifest.sha256` values; the canonical file hash and every listed input hash must match. The private `runs/<run-id>/manifest.json` retains model IDs. Save each unchanged response to its own `generation/candidate.md`, then run `ingest-result` for each run. Only after both are ingested, create separate blinded packages with `prepare-evaluation`; those packages use neutral candidate IDs and contain no model identity. Use `record-evaluation`, `compare`, and `report` only in a later evaluation step.

If one agent fails, preserve the successful run, mark the failed run incomplete (`mark-incomplete --run ID --reason "..."`), report the exact error, and retry only that candidate with a new `prepare-run`. Never copy private evaluation material into a generation directory and never auto-tune from scores.

The retry is a single-candidate retry, for example:

```powershell
& $cli prepare-run --case 03-batch-file-converter-cli --snapshot v0.1 --model gpt-5.6-sol --reasoning high --work-dir $work
```

## Reusable request template

See [`templates/dual-model-test-request.md`](templates/dual-model-test-request.md) for a compact parent request format.

## Token usage

Record each agent's reported usage in the private run record or test notes. Do not place token counts, model labels, or comparison metadata in the public generation package or blinded evaluator package.
