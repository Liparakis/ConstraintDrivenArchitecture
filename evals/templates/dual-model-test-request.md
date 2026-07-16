# Dual-model test request

Use one of these parent Codex requests:

- `test next`
- `test case <id>`
- `test mutation for case <id>`
- `compare Luna and Sol on case <id>`
- `test a new scenario: <brief public description>`

The parent selects the case, prepares two identical public packages, spawns both configured testers concurrently, waits for both, and reports the two private run IDs plus the next blinded-evaluation step.
