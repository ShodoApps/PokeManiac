# Regression — My Profile

**Feature status:** Live
**Spec:** `docs/features/my-profile.md`

Run this checklist any time you change My Profile or the user transaction data layer.

---

## Initial load

- [ ] Loading spinner shown on open
- [ ] Profile loaded: avatar, username, and transaction history visible
- [ ] Transactions displayed in reverse chronological order (newest first)
- [ ] Each transaction shows: card image, card name, transaction type, price, date

## Edge cases

- [ ] User with no avatar → placeholder image shown (no crash)
- [ ] User with no transactions → empty transaction list, header (avatar + name) still visible
- [ ] Very long transaction list → list scrolls smoothly (LazyColumn / lazy list)

## Error states

- [ ] Profile load fails → error snackbar shown, screen stays on Loading state (no partial data shown)

## Platform checks

- [ ] Android dark mode — profile header and transaction list readable
- [ ] iOS dark mode — profile header and transaction list readable
- [ ] Small screen — avatar, name, and transaction rows not clipped

## Accessibility

- [ ] TalkBack / VoiceOver: username and transaction items announced
- [ ] Avatar has a content description / accessibility label
