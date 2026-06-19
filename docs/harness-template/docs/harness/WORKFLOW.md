# AI-Driven Development Workflow

Three workflows, one harness. Pick the one that fits your context.

---

## 1. Solo Development Loop

```
brainstorm → spec → plan → implement → test gate → review → ship → retrospective
     ↑                                                                     |
     └─────────────────── loop if new pattern emerged ────────────────────┘
```

| Step | Skill / Tool | Output | Done when… |
|---|---|---|---|
| Brainstorm | `/brainstorming` | Approved design in conversation | User explicitly approves the design |
| Spec | `/brainstorming` (continues) | `docs/superpowers/specs/YYYY-MM-DD-topic-design.md` | User reviews and approves the spec file |
| Plan | `/writing-plans` | `docs/superpowers/plans/YYYY-MM-DD-topic.md` | Plan written and ready |
| Implement | Subagents or direct | Code changes | All plan tasks checked off |
| Test gate | `./gradlew testDebugUnitTest` + `./gradlew lint` | Green build | Tests pass, lint clean, no hook warnings |
| Review | `/code-review` or manual | Feedback addressed | No blocking issues |
| Ship | User commits + pushes | Commit on remote | **User pushes — Claude never commits** |
| Retrospective | Manual | New/updated ADR if needed | "Did this introduce a new pattern?" → if yes, write or update ADR |

### Rules

- **Never start implementing before the spec is approved.**
- **Never skip brainstorm** for a non-trivial feature (anything touching more than 2 files).
- **The test gate is not optional.** A feature is not done until `testDebugUnitTest` + `lint` pass.
- **Claude never commits or pushes.** You own the git history. (Enforced by hook.)
- **Retrospective = one question:** "Did this introduce a new architectural pattern?" If yes → write or update an ADR in `docs/harness/adr/`.

---

## 2. Grooming Session Workflow

Use this when the team is refining a ticket and wants a written spec as the output.

```
ticket description → /write-feature-spec → team Q&A → spec review → approved spec
```

| Step | Who | What happens |
|---|---|---|
| Introduce the ticket | Developer / PO | Describe the feature to Claude informally — no template needed |
| Interview | Claude | Asks clarifying questions one at a time: purpose → flows → screens → rules → error states → edge cases → out of scope |
| Capture answers | Team | Answers come from the team discussion; Claude records them |
| Draft spec | Claude | Writes `docs/features/feature-name.md` using the template |
| Review | Team | Read the spec live; request corrections |
| Approve | PO / Lead | Spec is the grooming output — replaces hand-written notes |

**Invoke with:** `/write-feature-spec`

**Value:** The interview questions Claude asks often surface gaps the team didn't discuss — missing error states, undefined edge cases, unclear "out of scope" boundaries. These are caught in the meeting, not during implementation.

---

## 3. Workshop / Design Session Workflow

Use this when the team is making an architectural or product decision.

```
problem statement → /brainstorming → collaborative Q&A → design approval → spec + ADR
```

| Step | Who | What happens |
|---|---|---|
| State the problem | Lead / Architect | Describe the decision to make to Claude |
| Explore | Claude | Asks questions, proposes 2–3 approaches with trade-offs |
| Discuss | Team | Team discusses each option; Claude records the reasoning |
| Decide | Lead | Team picks an approach; Claude documents why |
| Spec | Claude | Writes design spec to `docs/superpowers/specs/` |
| ADR | Claude or developer | If an architectural decision was made, write ADR to `docs/harness/adr/` |

**Invoke with:** `/brainstorming`

**Value:** Every significant design decision is documented with its reasoning. A developer joining later — or Claude in a new session — reads the ADR and understands why things are the way they are.

---

## 4. Documenting a Feature (Lightweight Path)

For features you already understand — no grooming session needed, you just want the spec written down.

```
informal description → /write-feature-spec → spec in docs/features/
```

**When to use `/write-feature-spec` vs `/brainstorming`:**

| Use `/write-feature-spec` when… | Use `/brainstorming` when… |
|---|---|
| You know what you want, need it documented | You're exploring options or making architectural decisions |
| Grooming session output | Design workshop output |
| Feature is well understood | Feature has technical unknowns or cross-cutting concerns |
| Output: `docs/features/` spec | Output: `docs/superpowers/specs/` spec + implementation plan |

---

## 5. QA Handoff

After the review step, before shipping:

1. Ask Claude: "Generate the QA handoff for this feature."
2. Claude reads `docs/harness/checklists/qa-handoff-template.md` and fills it with the actual changes.
3. Saves the result to `docs/qa/YYYY-MM-DD-feature-name.md` — the template is never modified.
4. QA team reads the file — they know what changed, what to test, and what to watch for.
