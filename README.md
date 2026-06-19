# Claude Code Hackathon

## The Point

This is a hack. You get a team, a scenario, and Claude Code. The scenarios are enterprise-flavored briefs: a monolith nobody understands, a migration nobody agrees on, seven systems that can't agree on what a customer is. Real problems, compressed.

There's no prescribed path. Each scenario sketches a handful of challenges worth working toward. How you get there, what stack you pick, what you skip, what you invent on top is up to you. We care about ambition and judgment, not box-checking.

---

## The Setup

Pick one scenario. Work with your team. Get as far as you can.

Each scenario sketches a handful of challenges. You probably won't do them all, and that's the point. **Depth beats breadth.** Pick the ones that interest you, work in parallel where you can, and let Claude help you coordinate.

---

## How Your Team Works

The scenarios span the SDLC, so there's meaningful work for PM, architect, dev, test, and platform. You won't have one of each, and that's fine. **Play every role, regardless of your day job.** Claude Code doesn't care what your title is, and a lot of what makes the hack interesting is watching the tool perform in parts of the work you don't normally touch.

Divide the challenges up early. Share a running `CLAUDE.md` so everyone teaches the tool the same conventions. Commit often. The commit history is part of the submission and part of how the judges read the journey.

---

## The Rules

1. **Tech stack is yours to choose.** One exception: Scenario 5 requires the **Claude Agent SDK**. Use Claude to help you learn it, or to migrate if you're coming from another framework.
2. **You may need to build starter code, data, or documents.** If the scenario says "a 12-year-old monolith exists," you generate it. That's part of the job. Some scenarios offer optional starter repos. Use them or don't.
3. **Play every role.** Your team needs a PM, architect, developer, tester, data engineer, and infra engineer whether you staffed for it or not.
4. **Commit history is evidence.** We want to see the journey, not just the destination.
5. **`CLAUDE.md` is your friend.** Teach it your conventions early.
6. **Document your work.** Your repo must include a `README.md` (template below) explaining what you built and what you'd do next.
7. **Build a presentation.** Use Claude Code to generate an HTML presentation you *could* deliver if you win the judging. It lives in your repo whether you present or not.
8. **Claude will judge.** At the end, Claude evaluates submissions. A handful of teams present live.

---

## The Scenarios

| \# | Scenario | One-liner |
| :---- | :---- | :---- |
| 1 | **[Code Modernization](01-code-modernization.md)** | A monolith nobody understands. The board wants it "modernized." |
| 2 | **[Cloud Migration](02-cloud-migration.md)** | On-prem to cloud. The CFO and CTO disagree on how. |
| 3 | **[Data Engineering](03-data-engineering.md)** | Seven systems. Zero agreement on what a "customer" is. |
| 4 | **[Data Analytics](04-data-analytics.md)** | 40 dashboards. One metric. Four different answers. |
| 5 | **[Agentic Solution](05-agentic-solution.md)** (Claude Agent SDK) | 200 requests a day, triaged by hand. Build the agent. |

---

## Techniques to Reach For

These are the patterns the Claude Code Architecture certification tests on. No scenario requires them, and no challenge dictates which to use. They're here because a lot of teams also want the hack to double as cert practice. Pick two or three you want to get reps on, and reach for them inside whichever challenges you pursue.

**Agentic Architecture**

- Coordinator plus specialist subagents via the Task tool, with context passed *explicitly* in each call (Task subagents don't inherit coordinator context).
- Stop conditions that are real signals, not "parse the text" or "iteration cap."
- `fork_session` to try two paths on the same input and compare.

**Tool Design & MCP**

- Tool descriptions that say what the tool *does* and what it *does not*. Input formats, edge cases, example queries.
- Structured error responses (`isError: true` with a reason code and guidance) so the agent can recover gracefully.
- Keep each specialist's tool count small. Reliability tends to drop once an agent has more than a handful.
- An MCP server over whatever system you built, so a fresh Claude session picks the right tool on the first try.

**Claude Code Config**

- Three-level `CLAUDE.md`: user (personal preferences), project (shared, in VCS), directory (per-module specifics).
- Custom slash commands *and* skills, used distinctly. A command runs a playbook; a skill captures reusable guidance.
- Plan Mode for anything reversible-dangerous; direct execution for the safe paths. Defend the default.
- Non-interactive Claude Code in CI, with scoped tools and no write access to production paths.

**Prompt Engineering**

- Explicit criteria in place of vague modifiers. "Material," "significant," and "recent" are usually a signal that the definition needs sharper thresholds.
- Few-shot examples with a negative case and a boundary case. Two sharp examples outperform eight fuzzy ones.
- `tool_use` with a JSON Schema for anything that must parse. Don't prompt-for-JSON.
- Validation-retry loop: structured validator checks the output, errors are fed back, Claude retries up to N times. Log retry count and error type.

**Context Management**

- Hooks for deterministic guardrails (`PreToolUse` to block, `PostToolUse` to redact). Prompts for probabilistic preferences. An ADR on why each is which is worth writing; the distinction shows up repeatedly on the exam.
- Escalation rules that are category plus confidence plus impact, not "when the agent isn't sure."
- Stratified sampling and field-level confidence when humans review.

---

## The Judging

Claude does the first pass. Top teams present live.

**What definitely gets read:**

1. Your `README.md`
2. Your `presentation.html`
3. Your `CLAUDE.md`

These are your pitch. Don't leave them to the end. If Claude only sees those three files, it should still understand what you built, why it matters, how far you got, and how you taught the tool to work your way. We may go deeper into the repo, we may not. Assume those three carry the weight.

**What we're looking for** (final categories will be a surprise!, but think along these lines):

- **Most production-ready.** Could hand it to an ops team Monday.
- **Best architecture thinking.** ADRs, diagrams, decisions someone will thank you for later.
- **Best testing.** Not coverage. Adversarial thinking, edge cases, evals.
- **Best product work.** Stories that are actually stories. Docs that persuade.
- **Most inventive Claude Code use.** Subagents, hooks, skills, something we didn't expect.
- **Wildcards:** best CI/CD, best legacy archaeology, best "what if this goes wrong" thinking, furthest through the challenges with quality intact, team that questioned a scenario requirement and was *right*.

---

## Submission

You need three files:

1. **`README.md`** tells the story. Use the template below.
2. **`CLAUDE.md`** so we can see how you taught Claude Code to work your way.
3. **`presentation.html`**, your HTML deck built with Claude Code, ready to present if called.

**Preferred:** put the three files in a folder named for your table and team (for example `Table1_SonnetSlayers/`) and upload the folder to the link provided at your session.

**Alternative:** if a folder upload isn't supported, zip the three files into an archive with the same naming convention (for example `Table1_SonnetSlayers.zip`) and upload that instead.

Either way, **one submission per team**.

**NO CLIENT OR INTERNAL DATA.** Anything in the submission must be safe to share.

---

## README Template

Copy this into your repo's `README.md` and fill it in as you go, not at the end.

```
# Team <name>

## Participants
- Name (role(s) played today)
- Name (role(s) played today)
- Name (role(s) played today)

## Scenario
Scenario <#>: <title>

## What We Built
A couple of paragraphs. What exists in this repo that didn't exist when you
started. What runs, what's scaffolding, what's faked.

## Challenges Attempted
| # | Challenge | Status | Notes |
|---|---|---|---|
| 1 | The <name> | done / partial / skipped | |
| 2 | | | |

## Key Decisions
Biggest calls you made and why. Link into `/decisions` for the full ADRs.

## How to Run It
Exact commands. Assume the reader has Docker and nothing else.

## If We Had More Time
What you'd tackle next, in priority order. Be honest about what's held
together with tape.

## How We Used Claude Code
What worked. What surprised you. Where it saved the most time.
```

---

## Our Work — Scenario 1: Code Modernization

### Participants
- (fill in names and roles)

### What We Built

A **Strangler Fig decomposition** of the Spring Music monolith into a clean `album-catalog-service`, with full characterization testing to guarantee behavioral equivalence.

**What exists in this repo:**

| Directory | What it is |
|-----------|-----------|
| `spring-music/` | The legacy monolith (unchanged) |
| `album-catalog-service/` | Extracted album service — Postgres/JPA only, clean REST |
| `docs/adr/` | ADR-001: Decomposition Plan |
| `tests/` | 87 characterization + contract test cases (Jest + Playwright) |
| `user-stories.md` | User stories with acceptance criteria and stakeholder disagreements |

### Challenges Attempted

| # | Challenge | Status | Notes |
|---|-----------|--------|-------|
| 1 | The Stories (PM) | Done | `user-stories.md` — 5 user stories, 4 open disagreements |
| 2 | The Patient (Architect) | Done | Using BYO monolith (spring-music) |
| 3 | The Map (Architect) | Done | `docs/adr/ADR-001-decomposition-plan.md` |
| 4 | The Pin (Tester) | In progress | `tests/` — 87 test cases, automated API + UI suite |
| 5 | The Cut (Dev) | Not started | `album-catalog-service/` has contract only |
| 6 | The Fence (Dev/Tester) | Not started | ACL rules defined in CLAUDE.md |

### Key Decisions

1. **Strangler Fig over big-bang rewrite** — incremental extraction with gateway routing
2. **Postgres-only for new service** — drops multi-backend complexity (Redis/Mongo/H2 profiles)
3. **Characterization tests before any code change** — behavior-pinning, bugs included
4. **Anti-corruption boundary** — `albumId` and `CfEnv` must never leak into new service
5. **Three-level CLAUDE.md** — project, monolith, and service each get their own context

See `docs/adr/ADR-001-decomposition-plan.md` for full rationale.

### How to Run It

```bash
# Run the monolith
cd spring-music
./gradlew clean assemble
java -jar build/libs/spring-music-1.0.jar
# App at http://localhost:8080

# Run characterization tests (requires monolith running)
cd tests
npm install
npx playwright install   # one-time browser setup
npx jest                 # API tests
npx playwright test      # UI tests
```

### If We Had More Time

1. Implement `album-catalog-service` (Phase 1 — The Cut)
2. Add API gateway routing with rollback capability
3. Automate ACL boundary checks in CI (grep for banned field names)
4. Extract frontend SPA to static hosting
5. Add contract tests that run both services side-by-side

### How We Used Claude Code

- **Tester role:** Claude explored the full monolith codebase and produced a complete behavior catalog (87 test cases) mapped to user stories
- **Three-level CLAUDE.md:** Taught Claude the project boundaries so it respects extraction rules
- **ADR generation:** Claude produced the decomposition plan with risk ranking and "what we chose NOT to do"
- **Test automation:** Generated Jest + Playwright test suite from the specification

