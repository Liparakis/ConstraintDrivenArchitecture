# Constraint-Driven Architecture evaluation harness

This harness is manual-model tooling. It prepares public generation workspaces, records immutable inputs, imports architecture outputs, creates blinded evaluator packages, validates score JSON, and reports comparisons. It never invokes Codex or an LLM.

## Dual-model test workflow

For the durable parent-orchestration contract, isolated Luna/Sol agents, case selection, and blinding rules, see [`DUAL_MODEL_TESTING.md`](DUAL_MODEL_TESTING.md). This workflow reuses the harness below; it does not modify the production skill.

## Windows workflow

```powershell
Set-Location .\evals\harness
.\gradlew.bat clean test installDist
$repo = (Resolve-Path ..\..).Path
$work = "$env:LOCALAPPDATA\ConstraintDrivenArchitectureEvals"
& ".\build\install\cda-evals\bin\cda-evals.bat" validate --skill-root $repo --work-dir $work
& ".\build\install\cda-evals\bin\cda-evals.bat" snapshot-skill --skill-root $repo --label v0.1 --work-dir $work
& ".\build\install\cda-evals\bin\cda-evals.bat" list-cases
& ".\build\install\cda-evals\bin\cda-evals.bat" prepare-run --case 01-appointment-booking --snapshot v0.1 --model "Luna High" --reasoning high --work-dir $work
```

Open the printed `generation` directory in Codex, run `$constraint-driven-architecture` with its prepared prompt, and save the answer as `candidate.md`. Then:

```powershell
& $cli ingest-result --run RUN_ID --work-dir $work
& $cli prepare-evaluation --run RUN_ID --work-dir $work
# Run the evaluator manually and save evaluation.json in the package directory.
& $cli record-evaluation --run RUN_ID --file PATH\evaluation.json --evaluator-label "Sol High" --work-dir $work
& $cli compare --kind model --runs LUNA_RUN_ID,SOL_RUN_ID --work-dir $work
& $cli report --all --work-dir $work
```

For a later skill version, create a new snapshot label. Never overwrite `v0.1` or change the skill between comparable runs.

## Do not contaminate the benchmark

Generation workspaces contain only the public brief, generation prompt, optional fixture, and a deterministic input manifest. Do not copy expectations, rubric, taxonomy, evaluator prompts, prior candidates, scores, or model-comparison data into them. Evaluator packages are separate and are created only after ingestion.

The default work directory is `%LOCALAPPDATA%\ConstraintDrivenArchitectureEvals`; use `--work-dir` or `CDA_EVALS_HOME` to override it. Reports are external by default.
