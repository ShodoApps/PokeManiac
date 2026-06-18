# Regression — Dashboard

**Feature status:** Live
**Spec:** `docs/features/dashboard.md`

Run this checklist any time you change the Dashboard, Post Transaction, or the news feed data layer.

---

## Initial load

- [ ] Loading spinner shown on first open
- [ ] Feed appears with friend transactions in reverse chronological order (newest first)
- [ ] Each card shows: friend avatar, friend name, card image, card name, transaction type (Buy/Sell/Trade), price, date
- [ ] Missing card image → placeholder shown (no crash, no broken image)
- [ ] Long friend name → truncated with ellipsis

## Empty state

- [ ] New user with no subscribed friends → empty state with CTA to add friends
- [ ] Empty state does not show a list

## Pull to refresh

- [ ] Pull down triggers a loading state (~2 second delay)
- [ ] Feed updates after refresh completes
- [ ] After posting a transaction, returning to Dashboard refreshes the feed (ON_RESUME)

## Error states

- [ ] Network failure on initial load → empty state shown + error snackbar
- [ ] Network failure on refresh → previous feed data stays visible + error snackbar
- [ ] Snackbar disappears automatically (not permanent)

## Navigation

- [ ] Post button navigates to Post Transaction Step 1
- [ ] Navigating back from Post Transaction returns to Dashboard

## Platform checks

- [ ] Android — feed scrolls smoothly, no recomposition jank on large lists
- [ ] iOS — feed scrolls smoothly
- [ ] Dark mode — all card elements readable
- [ ] Small screen — card layout not clipped or overflowing

## Accessibility

- [ ] TalkBack / VoiceOver: transaction cards are announced with meaningful content
- [ ] Post button has a content description / accessibility label
