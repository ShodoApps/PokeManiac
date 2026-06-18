---
name: write-feature-spec
description: Interview the developer about a feature and produce a functional spec in docs/features/. Works solo, in grooming sessions, or during design workshops.
---

# Write Feature Spec

You are conducting a product interview to capture a functional spec. Your goal: produce a well-structured markdown document in `docs/features/` that any developer or QA engineer can read to understand what the feature does, without reading the code.

## Context — This Skill Works in Three Settings

- **Solo:** A developer describes a feature they're about to build — you interview them and document it
- **Grooming:** The skill runs during a team grooming session — you ask questions that surface gaps the team may not have considered, capture the answers from the discussion
- **Workshop:** You act as live scribe — the team discusses, you record and ask clarifying questions, the spec is ready when the session ends

## Before You Start

Read `docs/features/_TEMPLATE.md` so you understand the sections to fill.

Announce: "I'm using the write-feature-spec skill. Let's document this feature. I'll ask you questions one at a time."

## Interview — One Question at a Time

Ask each question, wait for the answer, then move to the next. If the answer is incomplete, ask a clarifying follow-up before moving on.

1. **Purpose** — What problem does this feature solve for the user? Who is the user?
2. **Main flows** — Walk me through the main thing a user does in this feature, step by step. Are there other flows?
3. **Screens** — What screens are involved? What does each screen show?
4. **Business rules** — What constraints or logic govern this feature? What determines the behaviour?
5. **States** — What states can the user see? (Loading, empty, error, data — any others specific to this feature?)
6. **Error states** — What happens for each error scenario? (No network, server error, empty response, permission denied, validation error?)
7. **Edge cases** — What about long text? Small screens? Slow or no network? New users with no data?
8. **Out of scope** — What does this feature deliberately NOT do? (This prevents scope creep.)
9. **Implementation status** — Is this new, existing, partial, or a stub? If partial, what's not implemented yet?

In a grooming/workshop setting: after asking your question, pause and let the team discuss. Record the answer that emerges from the discussion, not just the first response.

## Writing the Spec

Once all sections are answered:

1. Re-read `docs/features/_TEMPLATE.md`
2. Write the spec, filling every template section with what you learned
3. Set **Status** from the implementation status answer: `Live` | `Partial` | `Stub`
4. Save to `docs/features/<feature-name>.md` — use kebab-case (e.g., `my-profile.md`, `search-friend.md`)
5. Present the path and ask for review: "Spec saved to `docs/features/<name>.md`. Please review it — let me know if anything needs adjusting."

## Rules

- One question at a time — never present a questionnaire
- Never invent business rules — if something is unclear, ask
- "Out of scope" is mandatory — always fill this section even if the answer is "nothing specific yet"
- The spec describes what the feature does for users, not how it is built — no implementation details unless they affect user-visible behaviour
