# Git Workflow

- Work only on the assigned `feature/*` branch; never push directly to `main`.
- Sync by `git fetch origin` then `git merge origin/main`; do not rebase shared
  work.
- Never force-push or rewrite shared history.
- Use `type(scope): summary` commit messages.
- A pull request states changes, reason, tests, evidence, limitations, and any
  shared-contract impact.
- Required checks and leader review must pass before merge.
- Do not mix unrelated foundation and feature work.

Before committing:

```text
git diff --check
gradlew lintDebug
gradlew testDebugUnitTest
gradlew assembleDebug
```
