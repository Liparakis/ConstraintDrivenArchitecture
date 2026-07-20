# Security policy

This is currently a pre-release project. Report suspected vulnerabilities privately through GitHub's private vulnerability reporting for this repository when enabled. Do not open a public issue containing secrets, exploit details, private benchmark data, or personal data. No personal security email is designated.

Relevant issues include command injection in harness arguments, path traversal, arbitrary file overwrite, unsafe archive handling, secret leakage, execution of model output as code, private evaluation contamination, provenance or hash bypass, unsafe report rendering, and dependency vulnerabilities. The skill can also produce unsafe architecture advice; treat generated advice as untrusted and require specialist review for high-risk systems.

The harness must never execute candidate text. Contributors must not commit secrets or private evaluation material. Include the affected version, reproduction steps, impact, and a safe contact path. Maintainers will acknowledge a valid private report when practicable, investigate, and coordinate a fix or mitigation before public disclosure.
