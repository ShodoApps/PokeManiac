# ADR-010: Accessibility Baseline

**Status:** Accepted
**Date:** 2026-06-18

## Context

PokeManiac targets adults aged 20–40 who are Pokémon card collectors. While the primary audience is not specifically users with disabilities, accessibility is a legal requirement in most markets and improves usability for all users (e.g. poor lighting, one-handed use, large text preferences).

We needed a baseline that is achievable without a dedicated accessibility specialist and that does not block shipping.

## Decision

We established a **baseline accessibility standard** covering the most impactful, lowest-effort requirements:

**All screens:**
- All interactive elements (buttons, list items, icons) have `contentDescription` or semantic role set — screen readers can identify them
- Touch targets are at least 48×48 dp (Android) / 44×44 pt (iOS) — matches platform guidelines
- Text contrast meets WCAG AA minimum (4.5:1 for normal text, 3:1 for large text) — enforced by design token choice in `PokeManiacColor.kt`
- Dynamic text size is supported — no fixed font sizes that clip at accessibility text scale

**Android specific:**
- TalkBack: all interactive elements are focusable and have meaningful labels
- `Modifier.semantics { }` used where the visual label is insufficient (e.g. icon-only buttons)

**iOS specific:**
- VoiceOver: all interactive elements have `accessibilityLabel` set
- Navigation with swipe gestures works end-to-end for primary flows

**Not in scope (conscious decision):**
- Full WCAG AA compliance on all screens
- Keyboard-only navigation (mobile-first app)
- Advanced switch access

## Consequences

**Easier:**
- TalkBack / VoiceOver basics work for primary flows
- Legal risk reduced in major markets
- Improves usability in low-vision / one-handed scenarios for all users

**Harder:**
- Must test on a real device with TalkBack/VoiceOver enabled before shipping — simulator/emulator accessibility testing is unreliable
- Icon-only buttons require explicit `contentDescription` — easy to forget, caught in code review

**Common mistake to avoid:** Calling `contentDescription = null` on an interactive element to suppress the "unlabelled button" TalkBack announcement. This makes the element invisible to screen readers. If a button has no visible text, add a descriptive `contentDescription`.
