# Mutation evaluator instructions

Compare the independently scored base and mutation candidates. Decide whether dimensions affected by the one declared constraint changed sufficiently and whether unrelated dimensions stayed stable. Cite evidence from both candidates. Do not reward wholesale redesign when the changed constraint does not justify it. Return only JSON matching `mutation-evaluation.schema.json`.
