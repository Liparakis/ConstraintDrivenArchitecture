# Contributing

This project is an instruction-only Codex architecture skill plus offline, manual-model evaluation tooling. Contributions should improve constraint proportionality, evidence discipline, safety, or inspectable evaluation without turning the project into a hosted service or generic benchmark platform.

Useful contributions include adversarial briefs; ambiguous cases with multiple defensible answers; embedded, local-first, regulated, data-intensive, security-sensitive, legacy-evolution, and cost-constrained systems; and repeated-failure reports grounded in published outputs.

To propose a case, add a schema-valid catalog entry with separate public brief/prompt and private expectations, explain the difficult decision, and document the constraints and legitimate variability. To propose a mutation, change one meaningful constraint and state the affected dimensions. Do not put hidden expectations, prior outputs, scores, or evaluator material in generation inputs.

Before changing `SKILL.md`, show evidence from repeated failure patterns or a severe safety issue, explain why the problem is a skill rule rather than an isolated model judgment or benchmark defect, and run affected plus unrelated regression cases. Preserve proportionality and do not tune a rule to one model output.

Run validation from `evals/harness` with `./gradlew clean test installDist` (or `gradlew.bat` on Windows), then validate the repository and inspect hashes. Do not execute candidate text, commit secrets or private benchmark data, or add model-specific prompt hacks. Keep documentation, command examples, and provenance accurate.

Use normal focused commits and pull requests. Explain the evidence, tests, and any known limitation. Contributions are accepted under Apache-2.0 as described in `LICENSE`; no CLA or DCO is currently required.
