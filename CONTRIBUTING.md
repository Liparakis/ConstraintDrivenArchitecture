# Contributing

Contributions should improve constraint proportionality, evidence discipline, safety, or inspectable evaluation without
turning this instruction-only skill into a hosted service or generic benchmark platform.

- Cases must pass the schema, separate public inputs from private expectations, and describe the difficult decision and
  legitimate variability.
- Mutations change one meaningful constraint and state the affected dimensions.
- Changes to `SKILL.md` need repeated failure evidence or a severe safety issue; do not tune to one model output.
- Run `gradlew.bat clean test installDist` from `evals/harness`, then validate hashes and affected cases.
- Never commit secrets, private evaluation material, or model-specific prompt hacks; never execute candidate text.

Use focused pull requests that explain the evidence, checks, and any remaining limitation. Contributions are under
Apache-2.0; no CLA or DCO is required.
