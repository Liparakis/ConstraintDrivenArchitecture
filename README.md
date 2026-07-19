# Constraint-Driven Architecture

> Design the simplest architecture that satisfies the real constraints.

`constraint-driven-architecture` is a Codex skill for greenfield design, major system evolution, and repository-level architecture reviews.

It starts with product capabilities, invariants, failure consequences, and evidence - not a preferred stack or an impressive diagram. A modular monolith is the default baseline. Services, queues, extra databases, cloud infrastructure, and technology diversity must earn their coordination and operational cost.

## What it helps with

- Turn an ambiguous product idea into a buildable architecture.
- Evolve an existing system without defaulting to a rewrite.
- Review a repository's real architecture instead of trusting its diagrams.
- Separate verified facts from user-stated requirements, assumptions, unknowns, and measurements.
- Make security, reliability, performance, cost, team ownership, and delivery risk affect the design early.
- Compare credible alternatives and explain why rejected options do not fit.
- Produce an implementation sequence, migration or rollback path, and checkable architecture fitness functions.

## When to use it

Use it when the decision changes system boundaries, data ownership, deployment topology, trust boundaries, consistency, reliability, or scaling.

```text
$constraint-driven-architecture Design the initial architecture for a local-first collaborative notes product. Ask only questions that could change the architecture; otherwise proceed with explicit assumptions and validation steps.
```

It can also activate when a request clearly calls for architecture work. For a small bug fix, isolated refactor, routine library choice, or straightforward implementation, it should stay out of the way.

## How it reasons

The workflow is deliberately opinionated:

1. Frame capabilities, actors, invariants, critical journeys, failure consequences, and non-goals.
2. Define constraints using adoption scenarios rather than invented precision.
3. Identify ownership, trust zones, transaction boundaries, failure domains, and operational responsibility.
4. Build the smallest credible baseline first.
5. Compare only serious alternatives, including the cost of coordination and reversibility.
6. Apply bounded security, performance, reliability, operations, cost, and delivery reviews.
7. Attack the design: remove unjustified services, databases, hops, abstractions, and fashionable infrastructure.
8. Select the simplest candidate that demonstrably fits, then make it buildable.

Important claims are labeled `VERIFIED`, `USER-STATED`, `DERIVED`, `ASSUMED`, `UNKNOWN`, or `REQUIRES-MEASUREMENT`. The skill does not treat uncertainty as a reason to invent requirements; it states what would change the decision and how to validate it.

## What a good result contains

The depth is proportional to risk, but an architecture response should make these explicit:

- selected mode: `GREENFIELD`, `EVOLUTION`, or `REVIEW`
- chosen architecture and simplest viable baseline
- boundaries, ownership, trust model, and important contracts
- evidence, assumptions, unknowns, and measurements still needed
- security, failure, degraded-mode, recovery, and observability behavior
- rejected alternatives and the evidence against them
- invalidation conditions and decisions intentionally deferred
- implementation order, migration/rollback where relevant, and practical fitness functions

The reference material is a menu, not a demand to produce a giant document.

## Install for Codex

### Windows PowerShell

```powershell
git clone https://github.com/Liparakis/constrain-driven-architecture.git "$env:USERPROFILE\.agents\skills\constraint-driven-architecture"
```

### macOS or Linux

```bash
git clone https://github.com/Liparakis/constrain-driven-architecture.git ~/.agents/skills/constraint-driven-architecture
```

Start a new Codex session if the skill does not appear automatically.

## Repository map

```text
SKILL.md                              Core workflow and decision rules
references/architecture-gates.md      Security, boundary, performance, and reliability gates
references/evidence-and-decisions.md  Evidence ledger and decision-record guidance
references/architecture-output-template.md
                                      Proportional output structure
agents/openai.yaml                    Codex UI metadata
evals/                                Manual dual-model evaluation harness
```

## Evaluate changes to the skill

The `evals/` harness prepares immutable public inputs, ingests candidate architecture outputs, creates blinded evaluator packages, validates score JSON, and reports comparisons. It never invokes Codex or an LLM itself.

```powershell
Set-Location .\evals\harness
.\gradlew.bat clean test installDist
```

See [`evals/README.md`](evals/README.md) for the complete workflow and [`evals/DUAL_MODEL_TESTING.md`](evals/DUAL_MODEL_TESTING.md) for the isolation and blinding contract.

## Scope and limits

This is an instruction-only skill. It does not create a custom orchestrator, database, broker, runtime, UI, CLI, or helper tool. It also does not replace benchmarks, threat modeling, operational review, or authoritative documentation when a decision depends on version-sensitive or measured behavior.
