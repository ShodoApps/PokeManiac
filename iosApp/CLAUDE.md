# PokeManiac — iOS App Guide

> See root `CLAUDE.md` for project-wide architecture.
> See `shared/CLAUDE.md` for ScreenModel/UiState patterns.
> See `docs/harness/patterns/ios-patterns.md` for full KotlinViewAdapter + SKIE code examples + permission flow.

---

## Key Rules

- Never manage `CoroutineScope` manually — `KotlinViewAdapter` creates and cancels it automatically on `deinit`
- `.task` handles view lifecycle — no manual `onDisappear` cancellation needed

---

## Reference Screens

| Screen | Files | Pattern used |
|---|---|---|
| Welcome | `WelcomeScreen.swift` + `WelcomeView.swift` | KotlinViewAdapter + `uiEvent` SharedFlow |
| SearchFriend | `SearchFriendScreen.swift` + `SearchFriendView.swift` | KotlinViewAdapter + iOS composition helper + error SharedFlow |
| Dashboard | `DashboardScreen.swift` + `DashboardView.swift` | KotlinViewAdapter + StateFlow only |

Full patterns: `docs/harness/patterns/ios-patterns.md`
