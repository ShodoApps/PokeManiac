# QA Handoff Template

> Copy this file to `docs/qa/YYYY-MM-DD-feature-name.md` and fill it in before shipping.
> Ask Claude: "Generate the QA handoff for this feature" — it will fill this template from the current changes.

---

# QA Handoff — [Feature Name] — [Date]

## What changed
1–3 sentence summary of what was implemented or fixed.

## Before / After

| Scenario | Before | After |
|---|---|---|
| [scenario] | [old behaviour] | [new behaviour] |

## What to test
- [ ] [Specific scenario 1]
- [ ] [Specific scenario 2]
- [ ] [Specific scenario 3]

## Regression areas
Screens or flows that could be affected by this change and should be re-tested.

## Don't forget
- [ ] Airplane mode / no network
- [ ] Small screen (SE-size iPhone, compact Android)
- [ ] Long texts and edge case content
- [ ] Error states (server error, timeout, empty response)
- [ ] Dark mode
- [ ] Back navigation and deep back stack
- [ ] Minimum OS version (Android API minimum / iOS minimum — see ADR-007 when written)
- [ ] Accessibility — TalkBack (Android) and VoiceOver (iOS) basics
- [ ] Slow network (throttle in developer settings)
- [ ] Portrait orientation only — no landscape testing required
