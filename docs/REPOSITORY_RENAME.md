# Repository URL history

The local `origin` was `https://github.com/Liparakis/constrain-driven-architecture.git`, but GitHub now reports that the repository moved to `https://github.com/Liparakis/ConstraintDrivenArchitecture.git`.

Renaming the GitHub repository improves discoverability and keeps the repository URL, skill name, installation path, and documentation consistent. The move is evidenced by GitHub's push response and a successful `git ls-remote` against the new URL; verify the final repository name in the GitHub UI.

The canonical repository URL is now confirmed as `https://github.com/Liparakis/ConstraintDrivenArchitecture.git`; no rename action remains for this checkout.

## If the old slug appears elsewhere

1. Open the repository on GitHub and choose **Settings** → **General**.
1. Update the local remote:

```powershell
git remote set-url origin https://github.com/Liparakis/ConstraintDrivenArchitecture.git
```

2. Recheck `README.md`, `evals/README.md`, `evals/DUAL_MODEL_TESTING.md`, launch materials, badges, and any external automation.

GitHub redirects are helpful during migration, but they should not replace fixing canonical links.
