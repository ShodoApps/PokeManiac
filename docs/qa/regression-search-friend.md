# Regression — Search Friend

**Feature status:** Live
**Spec:** `docs/features/search-friend.md`

Run this checklist any time you change Search Friend, subscription logic, or the friend data layer.

---

## Search behaviour

- [ ] Screen opens with empty state ("Search for a friend" prompt — no results shown)
- [ ] Typing a name → loading indicator → results list
- [ ] Blank or whitespace-only query → stays on empty state, no search triggered
- [ ] Clearing the search field → empty state shown again
- [ ] Query with no results → "No results for [name]" message
- [ ] Rapid typing → only the last query's results appear (previous in-flight searches cancelled)
- [ ] Search failure (network/server) → empty result state + error snackbar

## Subscribe

- [ ] Non-subscribed result shows "Subscribe" button
- [ ] Tapping Subscribe → button shows loading/Updating state
- [ ] Subscribe succeeds → button changes to "Unsubscribe"
- [ ] Subscribe fails → button reverts to "Subscribe" + error snackbar

## Unsubscribe

- [ ] Subscribed result shows "Unsubscribe" button
- [ ] Tapping Unsubscribe → button shows loading/Updating state
- [ ] Unsubscribe succeeds → button changes to "Subscribe"
- [ ] Unsubscribe fails → button reverts to "Unsubscribe" + error snackbar

## Edge cases

- [ ] Long username in result → truncated with ellipsis
- [ ] Searching your own name → appears in results (not filtered out — known behaviour)
- [ ] Subscribe/Unsubscribe button remains tappable (≥ 48dp Android / ≥ 44pt iOS)

## Platform checks

- [ ] Android dark mode — results list and buttons readable
- [ ] iOS dark mode — results list and buttons readable
- [ ] Small screen — result rows not clipped, button accessible

## Accessibility

- [ ] TalkBack / VoiceOver: result cards announce user name + current subscription state
- [ ] Subscribe/Unsubscribe button has a content description / accessibility label
