# Constraint-Driven Architecture

> Complexity only when the constraints earn it.

[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE) [![CI](https://github.com/Liparakis/ConstraintDrivenArchitecture/actions/workflows/ci.yml/badge.svg)](https://github.com/Liparakis/ConstraintDrivenArchitecture/actions/workflows/ci.yml) [![Java 25](https://img.shields.io/badge/evaluation%20harness-Java%2025-orange.svg)](evals/harness/build.gradle.kts) [![Codex skill](https://img.shields.io/badge/Codex-skill-6f42c1.svg)](agents/openai.yaml)

`constraint-driven-architecture` is an instruction-only Codex skill for greenfield design, major evolution, and
repository-level architecture review. It starts with capabilities, invariants, failure consequences, constraints, and
evidence, then selects the smallest defensible architecture.

## What problem it solves

Architecture advice often fails in two directions: impressive enterprise complexity without evidence, or simplistic
advice that refuses necessary distribution and infrastructure. This skill attempts to select complexity proportionally,
including when the answer is one deployable or when verified throughput and durability constraints justify a distributed
design.

## Same skill, different constraints

The following are illustrative evaluation cases until a complete scored package is published:

| Case                     | Constraints                                      | Candidate direction                             |
|--------------------------|--------------------------------------------------|-------------------------------------------------|
| Local batch converter    | One machine, resumable jobs, bounded concurrency | Single CLI process with local durable state     |
| Event ingestion platform | 250k events/s sustained, 1M/s bursts, replay     | Replicated partitioned log with consumer groups |

The cases are deliberately different: a local workload can reject services and brokers, while verified durable
throughput can justify partitions, replay, backpressure, and poison-event isolation. See the case briefs
and [published evidence](evals/published/README.md) for the current evidence status.

## How it differs

Software-architecture skills already exist. This project focuses on a narrower hypothesis:

> Architectural complexity should be earned by explicit constraints, and changes to the skill should be justified by
> evaluation evidence rather than prompt intuition alone.

Its particular combination is:

- separate `GREENFIELD`, `EVOLUTION`, and `REVIEW` modes;
- explicit evidence classifications;
- a smallest-credible-baseline rule;
- an adversarial pass that removes unjustified complexity;
- proportional output rather than mandatory architecture theatre;
- immutable public evaluation inputs;
- isolated candidate generation;
- blinded evaluator packages; and
- failure-pattern tracking before modifying the skill.

This is a scoped distinction, not a claim that other projects lack any of these techniques.

## 30-second usage example

```text
$constraint-driven-architecture Design a resumable batch file converter for one machine. Ask only questions that could change the architecture; otherwise state assumptions and validation steps.
```

Typical direction: one local executable, bounded workers, temporary output, atomic publication, and durable job state.
Services, a broker, and a central database require evidence.

## Installation

The canonical GitHub URL is `https://github.com/Liparakis/ConstraintDrivenArchitecture.git`.
The installed skill directory remains canonical.
See [the repository note](docs/REPOSITORY_RENAME.md).

### Windows PowerShell

```powershell
git clone https://github.com/Liparakis/ConstraintDrivenArchitecture.git "$env:USERPROFILE\.agents\skills\constraint-driven-architecture"
git -C "$env:USERPROFILE\.agents\skills\constraint-driven-architecture" pull --ff-only
Test-Path "$env:USERPROFILE\.agents\skills\constraint-driven-architecture\SKILL.md"
Remove-Item -Recurse -Force "$env:USERPROFILE\.agents\skills\constraint-driven-architecture"
```

### macOS/Linux

```bash
git clone https://github.com/Liparakis/ConstraintDrivenArchitecture.git ~/.agents/skills/constraint-driven-architecture
git -C ~/.agents/skills/constraint-driven-architecture pull --ff-only
test -f ~/.agents/skills/constraint-driven-architecture/SKILL.md
rm -rf ~/.agents/skills/constraint-driven-architecture
```

Start a new Codex session after installation. Invoke it explicitly with `$constraint-driven-architecture`.

## How it reasons

The workflow derives capabilities, hard and soft constraints, credible patterns, candidate technologies, operational and
delivery consequences, invalidation evidence, and the simplest sufficient choice. It preserves established repository
and team ecosystems when reasonable, and does not use a maintainer preference as a hidden tie-breaker. Important claims
are classified as `VERIFIED`, `USER-STATED`, `DERIVED`, `ASSUMED`, `UNKNOWN`, or `REQUIRES-MEASUREMENT`.

## Evidence discipline and evaluation

The offline harness prepares immutable public inputs, records candidates unchanged, creates blinded evaluator packages,
validates score JSON, and reports comparisons. It never invokes a model.
Read [the methodology](evals/METHODOLOGY.md), [the harness guide](evals/README.md),
and [the dual-model contract](evals/DUAL_MODEL_TESTING.md).

No complete scored evaluation package is currently published. Prepared external runs exist, but no `evaluation.json` was
recorded, so this README does not present them as benchmark results.

## Repository map

`SKILL.md` is the production workflow. `references/` contains gates, evidence guidance, and the output menu.
`agents/openai.yaml` contains Codex metadata. `evals/` contains offline evaluation tooling and cases.
`AUTHOR_PREFERENCES.md` is an optional maintainer profile.

## Known limitations

Architecture evaluation remains partly subjective; the harness does not invoke models; published outputs may not
reproduce byte-for-byte; two-model comparison is not universal; case rubrics may encode incorrect assumptions; the skill
can still overengineer or underengineer; repository inspection depends on accessible evidence; version-sensitive claims
require authoritative verification; security-critical or formally verified systems require specialist review; and the
skill does not replace measurement, benchmarks, threat models, or operational ownership. The maintainer's Java
preferences are optional and separated from the core.

## Contributing, security, and licence

See [CONTRIBUTING.md](CONTRIBUTING.md), [SECURITY.md](SECURITY.md), and [LICENSE](LICENSE). The project is Apache-2.0
licensed and does not currently require a CLA or DCO.

## Optional maintainer preferences

The Java 25 and Gradle Kotlin DSL preferences are documented separately
in [AUTHOR_PREFERENCES.md](AUTHOR_PREFERENCES.md). They are never automatically loaded or enforced by `SKILL.md`.
