# Feature: My Profile

**Status:** Live
**Last updated:** 2026-06-19

---

## Purpose
Shows the current user's own profile and their complete transaction history — all the Pokémon card trades they have posted. This is the user's personal record of their collecting activity.

---

## User Flows

### Flow 1: View My Profile
1. User navigates to My Profile
2. Loading state is shown while profile data is fetched
3. Profile is displayed: user avatar, username, transaction history (all cards they've traded)

### Flow 2: Browse Transaction History
1. User scrolls through their transaction list on the Profile screen
2. Each transaction shows: card image, card name, transaction type (buy/sell/trade), price, date
3. Transactions are displayed in reverse chronological order

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| MyProfileScreen | User avatar + name + complete transaction history | Main navigation |

---

## States

| State | When | What the user sees |
|---|---|---|
| Loading | Initial load | Full-screen spinner |
| Data | Profile loaded | Profile header + transaction list |

---

## Business Rules
- Shows only the current user's own transactions — not friends' transactions
- Transactions are displayed in reverse chronological order
- The profile header always shows the user's avatar and username
- If the user has no transactions, the transaction list section is empty (no explicit "empty" state currently implemented)

---

## Error States
- **Profile load fails:** Error snackbar shown; screen remains in Loading state (no fallback data displayed)

---

## Edge Cases
- New user with no transactions: transaction list is empty, header still shows avatar and name
- Very long transaction list: lazy-loaded list with `LazyColumn` (Tier 2 recomposition optimisations applied)
- User with no avatar: placeholder image shown

---

## Out of Scope
- Editing profile (username, avatar, bio)
- Account settings or logout
- Privacy settings
- Deleting individual transactions
- Transaction analytics or statistics (total spent, most collected Pokémon)
