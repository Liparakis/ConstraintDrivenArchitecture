# CI/CD and deployment

This repository is not a hosted application. The production skill is instruction-only; the deployable part is the offline `cda-evals` harness used by maintainers.

## Continuous integration

`.github/workflows/ci.yml` runs on pushes and pull requests. It sets up Java 25, runs the Gradle clean test and distribution build, validates the case catalog and skill, checks for personal paths and obvious committed secrets, and uploads the harness distribution as a workflow artifact.

## Release deployment

`.github/workflows/release.yml` runs when a tag matching `v*` is pushed. It rebuilds and tests the harness, creates `.tar.gz` and `.zip` distributions with portable SHA-256 files, and publishes them to a GitHub Release using the repository's `GITHUB_TOKEN`.

Create a release only after review:

```bash
git tag -a v0.1.0 -m "Release v0.1.0"
git push origin v0.1.0
```

The workflow does not deploy a server, invoke models, modify `SKILL.md`, or publish private evaluation data. A future hosted deployment would require an explicitly chosen target, credentials, ownership, rollback policy, and a different workflow.
