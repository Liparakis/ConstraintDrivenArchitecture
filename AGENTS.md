# Project instructions

## Dual-Model Architecture Skill Testing

Apply this workflow only when the parent request is to test, evaluate, benchmark, or run the next evaluation for the constraint-driven-architecture skill or its evaluation harness. This includes `test next`, `test case 03`, `test mutation for case 02`, `compare Luna and Sol on case 05`, `run an architecture evaluation`, `run the next eval`, and `test a new scenario`. A bare `test` triggers it only when the active context is clearly this skill root or its `evals` directory. Do not apply it to ordinary unit tests, Gradle tests, script tests, application tests, build tests, or unrelated repository checks merely because they contain the word "test".

For a matching request, the parent Codex must select or prepare exactly one case, create its immutable public generation packages, then spawn `cda_luna_tester` and `cda_sol_tester` concurrently (in parallel) and wait for both before comparison or evaluation. Give both agents the exact same public inputs. Never expose expectations, rubric, taxonomy, evaluator prompts, prior outputs, scores, reports, model labels, or comparison metadata to either tester or to the other tester. Preserve each returned candidate separately, with private run metadata, and use the existing harness `prepare-run`, `ingest-result`, and `prepare-evaluation` commands. Verify both `input-manifest.sha256` files and the canonical input hashes before accepting the pair.

If the user requests a new scenario, create and validate it with the existing case schema before preparing either public package. Treat the first prepared input manifest as canonical and require the second manifest to match it byte-for-byte and by every listed input hash; stop before spawning if they differ.

Never modify the production skill while running an evaluation. If one candidate fails, preserve the successful candidate, mark the failed run incomplete, report the exact failure, and offer a retry for only that candidate. Do not automatically tune the skill or prompts from a result.
