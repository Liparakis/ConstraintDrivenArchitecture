# Tuning policy

- Freeze the skill for a benchmark batch.
- Never modify the skill between Luna and Sol runs of the same case.
- Use identical generation inputs for model comparisons.
- Do not tune based on one questionable technology choice.
- Consider a skill change only when the same failure category appears in at least two unrelated benchmark families, or when one severe false guarantee exposes a fundamental safety flaw.
- Make one surgical skill change at a time where possible.
- Rerun affected cases plus at least two unrelated regression cases.
- Reject a change that improves one case by hardcoding its answer while harming generality.
- Record every accepted or rejected skill change in `CHANGELOG.md`.

Reports distinguish isolated model judgment, repeated model weakness, probable skill weakness, benchmark defect, and architecture-changing unknown. These are evidence classifications, not proof of causality.
