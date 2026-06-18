# Feature: Search Friend

**Status:** Live
**Last updated:** 2026-06-19

---

## Purpose
Allows users to find other collectors by name and subscribe to follow their transactions. This is the primary way to build a social graph in PokeManiac — users who follow each other see each other's card trades in the Dashboard feed.

---

## User Flows

### Flow 1: Search for a User
1. User opens Search Friend screen
2. Search field is empty — empty search state is shown (prompt to start typing)
3. User types a name
4. Loading state is shown while search is in progress
5. Results are displayed as a list of user cards showing: avatar, name, subscription status
6. If no results match, "No results for [name]" is shown

### Flow 2: Subscribe to a Friend
1. User sees a search result with "Subscribe" button (NotSubscribed state)
2. User taps Subscribe
3. Button shows "Updating…" state while the request is in flight
4. On success: button changes to "Unsubscribe" (Subscribed state)
5. The subscribed user's transactions will now appear in the Dashboard feed

### Flow 3: Unsubscribe from a Friend
1. User sees a search result with "Unsubscribe" button (Subscribed state)
2. User taps Unsubscribe
3. Button shows "Updating…" state while the request is in flight
4. On success: button changes to "Subscribe" (NotSubscribed state)
5. The user's transactions no longer appear in the Dashboard feed

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| SearchFriendScreen | Search field + results list | Dashboard navigation |

---

## States

| State | When | What the user sees |
|---|---|---|
| EmptySearch | Screen first opens / search field cleared | Empty state: "Search for a friend" prompt |
| Loading | Search query submitted | Spinner in the results area |
| Data | Results found | List of user cards with subscribe/unsubscribe buttons |
| EmptyResult | Query returned no results | "No results for [query]" message |

**Per-result subscription states:**
| State | When | Button label |
|---|---|---|
| NotSubscribed | User is not following this person | "Subscribe" |
| UpdatingSubscribe | Subscribe/unsubscribe in progress | Loading indicator |
| Subscribed | User is following this person | "Unsubscribe" |

---

## Business Rules
- Search triggers on every keystroke (debounced / cancels previous search)
- Empty or blank query always shows EmptySearch — never triggers a search
- Subscription state is optimistically shown in the UI immediately, confirmed after API response
- If subscription update fails, the button reverts to its previous state

---

## Error States
- **Search fails (network/server):** EmptyResult shown for the query + error snackbar
- **Subscribe fails:** Button reverts to NotSubscribed + error snackbar
- **Unsubscribe fails:** Button reverts to Subscribed + error snackbar

---

## Edge Cases
- Very long usernames: truncated with ellipsis
- Searching for yourself: currently not filtered out — appears in results if name matches
- Rapid typing cancels in-flight searches (previous job is cancelled on new input)
- Small screens: subscription button must remain tappable at minimum 48dp touch target

---

## Out of Scope
- Blocking or muting users
- Mutual subscription detection ("follows you back")
- Browse suggestions (friends of friends, recommended users)
- Profile preview before subscribing
