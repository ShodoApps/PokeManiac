# Regression — Post Transaction

**Feature status:** Live
**Spec:** `docs/features/post-transaction.md`

Run this checklist any time you change Post Transaction, the camera flow, or the transaction data layer.

---

## Step 1 — Camera

- [ ] Tapping Post on Dashboard opens Step 1 with a live camera viewfinder
- [ ] Camera viewfinder is active (not frozen, not blank)
- [ ] Taking a photo → navigates to Step 2 with the photo visible

## Error states — Step 1

- [ ] Camera file creation fails → error snackbar shown, user can retry
- [ ] URI resolution fails → error snackbar shown, user can retry

## Step 2 — Details form

- [ ] Form shows: Pokémon name field, Pokémon number field, transaction type selector (Buy/Sell/Trade), price field
- [ ] All three transaction types selectable (Buy, Sell, Trade)
- [ ] Price of 0 accepted (gift / trade with no monetary value)
- [ ] Tapping Post → loading state shown, form disabled
- [ ] Post succeeds → navigates back to Dashboard
- [ ] Dashboard feed refreshes after returning (ON_RESUME)

## Error states — Step 2

- [ ] Post fails → form returns to Filling state + error snackbar
- [ ] User can retry after failure

## Navigation

- [ ] Back from Step 2 to Step 1 → no crash, camera restarts
- [ ] Back from Step 1 → returns to Dashboard

## Edge cases

- [ ] Very long Pokémon name → field allows entry, form does not crash
- [ ] Pokémon number 0 → accepted (no validation in current implementation)

## Platform checks

- [ ] Android — camera permission granted before first use; denied → appropriate message
- [ ] iOS — camera permission requested on first use; denied → appropriate message
- [ ] Dark mode — form fields and type selector readable in both steps
- [ ] Small screen — form scrollable, Post button accessible

## Accessibility

- [ ] TalkBack / VoiceOver: form fields labelled (name, number, type, price)
- [ ] Post button has a content description / accessibility label
- [ ] Transaction type selector options announced correctly
