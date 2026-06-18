# ADR-007: Platform & Form Factor Targets

**Status:** Accepted
**Date:** 2026-06-18

## Context

PokeManiac is a consumer social app. We needed to define which platforms, OS versions, and form factors to support before building any UI. Covering everything (tablets, foldables, landscape, older OS) has real cost: more layout variants, more test cases, more conditional code.

## Decision

**Platforms:**
- Android (primary)
- iOS (secondary, Phase G)

**Android minimum SDK:** To be formalised in a future ADR when the first release is cut. Current development targets a reasonably modern minimum (exact SDK TBD based on analytics).

**iOS minimum version:** To be formalised when the iOS app reaches release readiness.

**Form factor:**
- **Portrait only** — no landscape layout required for any screen
- **Phone only** — no tablet or iPad optimisation required
- No foldable support

**Screen sizes:**
- Test on both large (standard phone) and small (iPhone SE-equivalent, compact Android) screens
- Long texts and edge-case content must not break layout on small screens

## Consequences

**Easier:**
- No landscape media queries or orientation change handling
- No adaptive layouts or two-pane patterns
- Simplified QA matrix — portrait only, phone only

**Harder:**
- If tablet support is added later, layout changes could be significant
- Small-screen testing is still required — not all developers test on SE-size devices by default

**Common mistake to avoid:** Hardcoding pixel sizes or using fixed-height containers that clip on small screens. Use `fillMaxWidth`, `weight`, and `wrapContentHeight` in Compose. Test with a small device or emulator before marking a feature done.
