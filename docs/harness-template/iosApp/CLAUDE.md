# [PROJECT_NAME] — iOS App Guide

> See root `CLAUDE.md` for project-wide architecture.
> See `shared/CLAUDE.md` for ScreenModel/UiState patterns.
> See `docs/harness/patterns/ios-patterns.md` for full KotlinViewAdapter + SKIE code examples + permission flow.

---

## Key Rules

- Never manage `CoroutineScope` manually — `KotlinViewAdapter` creates and cancels it automatically on `deinit`
- `.task` handles view lifecycle — no manual `onDisappear` cancellation needed

---

## Reference Screens

Add your own reference screens here as you build them:

| Screen | Files | Pattern used |
|---|---|---|
| (add your first screen here) | | KotlinViewAdapter + StateFlow |

Full patterns: `docs/harness/patterns/ios-patterns.md`
