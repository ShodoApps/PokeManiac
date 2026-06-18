# Regression — Welcome

**Feature status:** Partial (auth bypass — sign in goes directly to Dashboard)
**Spec:** `docs/features/welcome.md`

Run this checklist any time you change the Welcome screen, app startup flow, or navigation root.

---

## Flows

- [ ] App launch shows Welcome screen (branding + Sign In + Sign Up buttons)
- [ ] Sign In button → navigates directly to Dashboard (no auth prompt)
- [ ] Sign Up button → shows stub message "Nothing implemented yet", stays on Welcome
- [ ] No back navigation possible from Welcome (it is the root)

## Platform checks

- [ ] Android — buttons are tappable at 48dp minimum touch target
- [ ] iOS — buttons are tappable at 44pt minimum touch target
- [ ] Dark mode — branding and buttons readable
- [ ] Small screen (SE / compact Android) — buttons and text not clipped

## Accessibility

- [ ] TalkBack (Android): Sign In and Sign Up buttons announced correctly
- [ ] VoiceOver (iOS): Sign In and Sign Up buttons announced correctly
