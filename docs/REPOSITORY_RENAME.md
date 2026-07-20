# Repository rename recommendation

The configured `origin` is currently `https://github.com/Liparakis/constrain-driven-architecture.git`. The repository slug omits “t” from “constraint”, while the installed skill directory and skill name are canonical: `constraint-driven-architecture`.

Renaming the GitHub repository improves discoverability and keeps the repository URL, skill name, installation path, and documentation consistent. This rename has not been performed by this change.

## Manual GitHub steps

1. Open the repository on GitHub and choose **Settings** → **General**.
2. Change **Repository name** to `constraint-driven-architecture` and confirm the rename.
3. Update the local remote:

```powershell
git remote set-url origin https://github.com/Liparakis/constraint-driven-architecture.git
```

4. Recheck `README.md`, `evals/README.md`, `evals/DUAL_MODEL_TESTING.md`, launch materials, badges, and any external automation.

GitHub redirects are helpful during migration, but they should not replace fixing canonical links.
