# Feature: Welcome

**Status:** Partial
**Last updated:** 2026-06-19

---

## Purpose
The entry point of the app. Shown to users who are not yet signed in. It introduces the app and provides two paths: sign up for a new account or sign in to an existing one.

---

## User Flows

### Flow 1: Sign In
1. User opens the app for the first time (or is signed out)
2. Welcome screen is displayed with the app name/branding, a Sign In button, and a Sign Up button
3. User taps Sign In
4. User is navigated to the Dashboard (authentication is not yet implemented — this is a direct bypass)

### Flow 2: Sign Up (stub)
1. User taps Sign Up
2. A message is shown: "Nothing implemented yet"
3. User remains on the Welcome screen

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| WelcomeScreen | App branding, Sign In button, Sign Up button | App launch (unauthenticated) |

---

## States

| State | When | What the user sees |
|---|---|---|
| Idle | Screen opens | Branding + two buttons |

---

## Business Rules
- Sign In bypasses authentication for now — it navigates directly to Dashboard
- Sign Up is a stub — shows an informational message and stays on Welcome

---

## Error States
- No network error states (no network calls in current implementation)
- Sign Up stub: shows a snackbar/alert with "Nothing implemented yet"

---

## Edge Cases
- No back navigation from Welcome (it is the root screen)
- If a user is already signed in (future state), Welcome should be skipped — not yet implemented

---

## Out of Scope
- Real authentication (username/password, OAuth, biometric)
- Session persistence / auto-login
- Onboarding flow after sign up
- Forgot password flow
