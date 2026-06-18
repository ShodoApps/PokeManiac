# Regression — My Friends

**Feature status:** Live
**Spec:** `docs/features/my-friends.md`

Run this checklist any time you change My Friends, friend detail, or the subscription/unsubscribe flow.

---

## Friend list

- [ ] Loading spinner shown on open
- [ ] Subscribed friends displayed as a list (avatar + name per row)
- [ ] No subscriptions → empty state with "Find Friends" CTA
- [ ] Empty state CTA navigates to Search Friend

## Friend detail

- [ ] Tapping a friend → navigates to Friend Detail
- [ ] Friend Detail: loading spinner shown while data fetches
- [ ] Friend Detail loaded: avatar, name, and transaction history visible
- [ ] Friend with no transactions → empty transaction section (no error, no crash)
- [ ] Long friend name → shown fully in detail header

## Unsubscribe from list

- [ ] Tapping Unsubscribe on a list row → friend removed from list immediately (reactive)
- [ ] Unsubscribe fails → error snackbar, list state is NOT reverted (Room is source of truth)

## Unsubscribe from detail

- [ ] Tapping Unsubscribe in Friend Detail → navigates back to Friend List on success
- [ ] Unsubscribed friend no longer appears in the list
- [ ] Unsubscribe fails → error snackbar, user stays on Detail screen

## Reactive back navigation

- [ ] If a friend is unsubscribed from the list while you are viewing their Detail screen → Detail screen automatically navigates back (Room query emits null)

## Error states

- [ ] Friend list fails to load → stays on loading + error snackbar
- [ ] Friend detail fails to load → stays on loading + error snackbar

## Platform checks

- [ ] Android dark mode — list rows and detail screen readable
- [ ] iOS dark mode — list rows and detail screen readable
- [ ] Small screen — rows and Unsubscribe button not clipped

## Accessibility

- [ ] TalkBack / VoiceOver: friend names announced in list and in detail header
- [ ] Unsubscribe button has a content description / accessibility label
