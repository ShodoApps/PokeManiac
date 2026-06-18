# Feature: Billing

**Status:** Stub
**Last updated:** 2026-06-19

---

## Purpose
The entry point for the premium subscription paywall. Intended to let users subscribe to a paid tier that unlocks premium features (AI card recognition, advanced analytics, etc.). Currently a placeholder screen with no payment integration.

---

## User Flows

### Flow 1: View Billing Screen (current state)
1. User is navigated to the Billing screen (entry point TBD — likely from a "Go Premium" CTA elsewhere)
2. Loading state is shown briefly
3. Screen content is not yet implemented

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| BillingScreen | Stub — loading state only | Navigation (entry point not yet defined) |

---

## States

| State | When | What the user sees |
|---|---|---|
| Loading | Screen opens | Loading indicator (persistent — no data state implemented yet) |

---

## Business Rules
- No business rules implemented — this is a stub screen
- Planned: display subscription tiers, pricing, and purchase CTA

---

## Error States
- No error states implemented

---

## Edge Cases
- None implemented

---

## Out of Scope (everything, until the feature is implemented)
- Payment processing (App Store / Google Play in-app purchases)
- Subscription tier selection
- Receipt validation
- Subscription management (cancel, restore)
- Paywall gating of premium features
- Trial periods or promotional codes
