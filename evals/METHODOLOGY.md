# Evaluation methodology

The harness evaluates architecture responses against case-specific constraints and a published rubric. It does not invoke models; generation is manual and the harness records inputs, outputs, hashes, evaluator artefacts, and comparisons.

It evaluates constraint grounding, proportionality, invariants, failure and recovery semantics, security boundaries, technology selection, alternatives, implementation usefulness, fitness functions, and uncertainty handling. It does not evaluate universal architectural correctness, production availability, real-world capacity, model quality in general, or whether a candidate will work without implementation and measurement.

Runs use immutable skill snapshots and identical public generation inputs. Private expectations, rubric material, evaluator prompts, prior outputs, and model labels stay outside generation workspaces. Candidate outputs are ingested unchanged. Evaluation packages use neutral candidate identifiers. Blinding reduces some forms of bias; it does not make architecture evaluation objective.

Architecture has legitimate plurality. A higher score does not prove that one architecture is universally correct. It means the candidate better satisfied the published case rubric under this evaluation procedure.

Evaluator judgment is partly subjective and may correlate with the generating model or evaluator. Two models are not a universal benchmark, and a small case suite risks benchmark overfitting. Rubrics may encode incorrect assumptions; disagreements and unknowns should remain visible.

Failure tags are tracked by case family. A skill change normally requires the same failure category in at least two unrelated benchmark families, or one severe false guarantee exposing a fundamental safety flaw. Mutation testing checks whether a known constraint change changes the answer. Never tune the skill to one output.

New cases must pass the existing case schema, separate public and private material, state the decision being tested, and include a rubric-relevant expectation set without hiding expectations in the public prompt.

To publish a package, complete immutable runs, verify snapshot and input hashes, preserve candidates unchanged, neutralize model identity, and include provenance. Publication cannot make nondeterministic future model runs byte-for-byte reproducible.
