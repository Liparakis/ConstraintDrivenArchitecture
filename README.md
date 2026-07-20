# Constraint-Driven Architecture

> Complexity only when the constraints earn it.

[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE) [![CI](https://github.com/Liparakis/ConstraintDrivenArchitecture/actions/workflows/ci.yml/badge.svg)](https://github.com/Liparakis/ConstraintDrivenArchitecture/actions/workflows/ci.yml) [![Codex skill](https://img.shields.io/badge/Codex-skill-6f42c1.svg)](agents/openai.yaml)

`constraint-driven-architecture` is an instruction-only Codex skill for greenfield design, major evolution, and repository-level architecture review. It starts with capabilities, invariants, failure consequences, constraints, and evidence, then chooses the smallest defensible architecture.

## Why

Architecture advice often fails by adding enterprise machinery without evidence or by dismissing real distribution needs. This skill treats a modular monolith or similarly small deployable as the baseline; services, brokers, multiple stores, and operational complexity must earn their cost through explicit constraints.

It provides `GREENFIELD`, `EVOLUTION`, and `REVIEW` modes; an evidence ledger; proportionate architecture gates; and an adversarial pass to remove unjustified complexity. It is not a claim that other architecture tools lack these techniques.

## Use

```text
$constraint-driven-architecture Design a resumable batch file converter for one machine. Ask only questions that could change the architecture; otherwise state assumptions and validation steps.
```

For that brief, the likely answer is one local executable with bounded workers, temporary output, atomic publication, and durable job state—not services, a broker, or a central database.

## Install

Clone into your Codex skills directory, then start a new Codex session.

```powershell
git clone https://github.com/Liparakis/ConstraintDrivenArchitecture.git "$env:USERPROFILE\.agents\skills\constraint-driven-architecture"
Test-Path "$env:USERPROFILE\.agents\skills\constraint-driven-architecture\SKILL.md"
```

```bash
git clone https://github.com/Liparakis/ConstraintDrivenArchitecture.git ~/.agents/skills/constraint-driven-architecture
test -f ~/.agents/skills/constraint-driven-architecture/SKILL.md
```

To update an existing installation, run `git pull --ff-only` in that directory. Invoke the skill explicitly with `$constraint-driven-architecture`.

## Evaluation

The offline harness creates immutable public inputs, records candidates unchanged, creates blinded evaluator packages, validates score JSON, and reports comparisons. It never invokes a model. Read the [methodology](evals/METHODOLOGY.md), [harness guide](evals/README.md), and [dual-model contract](evals/DUAL_MODEL_TESTING.md).

No complete scored evaluation package is currently published; prepared runs are not presented as benchmark results.

## Releases

Tags run the [release workflow](.github/workflows/release.yml), which validates the repository and publishes checksummed harness archives. This project has no hosted application or deployment process.

## Repository map

- `SKILL.md` — production workflow.
- `references/` — gates, evidence guidance, and output menu.
- `evals/` — offline evaluation tooling and cases.
- `agents/openai.yaml` — Codex metadata.
- `AUTHOR_PREFERENCES.md` — optional, non-enforced maintainer preferences.

## Limits

The skill does not replace measurement, benchmarks, threat models, operational ownership, or specialist review for security-critical or formally verified systems. Evaluation is partly subjective and a two-model comparison is not a universal benchmark.

See [CONTRIBUTING.md](CONTRIBUTING.md), [SECURITY.md](SECURITY.md), and [LICENSE](LICENSE).
