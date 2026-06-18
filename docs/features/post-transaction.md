# Feature: Post Transaction

**Status:** Live
**Last updated:** 2026-06-19

---

## Purpose
Allows users to share a Pokémon card transaction — a buy, sell, or trade — with their followers. Each post includes a photo of the card, the card name and number, the transaction type, and the price. Posted transactions appear in the Dashboard feed of all subscribed friends.

---

## User Flows

### Flow 1: Post a Transaction
1. User taps the post button on Dashboard
2. **Step 1 — Camera:** User sees a camera viewfinder. The app creates a temporary JPEG file for the photo.
3. User takes a photo of the Pokémon card
4. User taps to confirm the photo
5. **Step 2 — Details:** User fills in: Pokémon name, Pokémon number, transaction type (buy/sell/trade), price
6. User taps "Post"
7. Loading state is shown while the transaction is saved
8. On success, user is navigated back to Dashboard (success event emitted)
9. The transaction appears in the Dashboard feed

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| PostTransactionStep1Screen | Camera viewfinder, capture button | Dashboard "Post" button |
| PostTransactionStep2Screen | Form: name, number, type, price + post button | After photo taken in Step 1 |

---

## States

### Step 1 (Camera)
| State | When | What the user sees |
|---|---|---|
| Camera active | Screen open | Live camera viewfinder |

### Step 2 (Details Form)
| State | When | What the user sees |
|---|---|---|
| Filling | Form open | Input fields: name, number, type (selector), price + Post button |
| Loading | Post submitted | Loading indicator, form disabled |

---

## Business Rules
- Step 1 creates a temporary JPEG file path on the device before the camera launches
- The image URI is passed from Step 1 to Step 2 via navigation route (ID only, not the image data)
- Transaction type is one of: Buy, Sell, Trade
- Pokémon number is an integer (Pokédex number)
- The post is attributed to the current user with the hardcoded name "Super Collectionneur" (auth not yet implemented)
- On successful post, a `success` event is emitted and the screen navigates back to Dashboard; the Dashboard refreshes via `ON_RESUME` lifecycle event

---

## Error States
- **Camera file creation fails (Step 1):** Error snackbar; user can retry
- **URI resolution fails (Step 1):** Error snackbar; user can retry
- **Post fails (Step 2):** Form returns to Filling state + error snackbar; user can retry

---

## Edge Cases
- User goes back from Step 2 to Step 1: temporary JPEG file remains on device (not yet cleaned up)
- Very long Pokémon name: form field truncates but posts full value
- Price of 0: allowed (gift/trade with no monetary value)
- Pokémon number 0 or negative: not validated in current implementation

---

## Out of Scope
- Gallery photo selection (camera only for now)
- Editing or deleting a posted transaction
- Tagging other users in a transaction
- Multiple photos per transaction
- Card valuation or price suggestions
- Pokédex autocomplete for name/number fields
