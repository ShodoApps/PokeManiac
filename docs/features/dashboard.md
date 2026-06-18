# Feature: Dashboard (News Feed)

**Status:** Live
**Last updated:** 2026-06-19

---

## Purpose
The home screen of the app. Shows a chronological feed of Pokémon card transactions posted by the user's friends — what they bought, sold, or exchanged, at what price, with a photo of the card. This is the social core of the app: users come here to see what the community has been trading.

---

## User Flows

### Flow 1: View News Feed
1. User arrives at Dashboard (after sign in)
2. Loading state is shown while data is fetched
3. Feed displays transaction cards in reverse chronological order — each card shows: friend's avatar, friend's name, Pokémon card image, card name, transaction type (buy/sell/trade), price, date
4. If no friends have posted transactions, an empty state is shown

### Flow 2: Pull to Refresh
1. User pulls down on the feed
2. Loading state is shown briefly (2 second mock delay in current implementation)
3. Feed refreshes with latest data

### Flow 3: Post a Transaction
1. User taps the "Post" action (entry point to Post Transaction feature)
2. Navigates to Post Transaction Step 1

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| DashboardScreen | News feed of friend transactions + post button | Sign in / app root |

---

## States

| State | When | What the user sees |
|---|---|---|
| Loading | Initial load or pull-to-refresh | Full-screen spinner |
| Data | Feed has items | Scrollable list of transaction cards |
| EmptyResult | No transactions in feed | Empty state screen |

---

## Business Rules
- Feed shows transactions from subscribed friends only — not global
- Transactions appear in reverse chronological order (newest first)
- Refreshing shows a loading state for ~2 seconds (current mock implementation)
- Each transaction card shows: poster name + avatar, card image, card name, activity type, price, date

---

## Error States
- **Network failure on load:** Empty state shown + error snackbar with message
- **Network failure on refresh:** Previous data remains visible + error snackbar

---

## Edge Cases
- Empty feed (new user with no subscribed friends): empty state with CTA to add friends
- Long friend names: truncated with ellipsis
- Missing card image: placeholder image shown
- Very long transaction list: lazy-loaded, infinite scroll (currently all loaded at once)

---

## Out of Scope
- Likes, comments, or reactions on transactions
- Filtering or sorting the feed
- Transaction detail screen (tapping a card in the feed)
- Global feed (transactions from all users, not just friends)
- Push notifications for new feed items
