# cloud-itonami-isic-9499

Open Business Blueprint for **ISIC Rev.5 9499**: Activities of other
membership organizations n.e.c..

This repository publishes an other-membership-organization-governance
actor -- member enrollment intake, per-jurisdiction tax-exempt-status/
political-activity-restriction regulatory assessment, tax-exempt-
status-risk screening and public-position publication -- as an OSS
business that any qualified operator can fork, deploy, run, improve
and sell, so a community or independent provider never surrenders
member data and ledgers to a closed SaaS.

Built on this workspace's
[`langgraph`](https://github.com/kotoba-lang/langgraph)
StateGraph runtime (portable `.cljc`, supervised superstep loop,
interrupts, Datomic/in-mem checkpoints) -- the same actor pattern as
every prior actor in this fleet
([`cloud-itonami-isic-6511`](https://github.com/cloud-itonami/cloud-itonami-isic-6511),
[`6512`](https://github.com/cloud-itonami/cloud-itonami-isic-6512),
[`6621`](https://github.com/cloud-itonami/cloud-itonami-isic-6621),
[`6622`](https://github.com/cloud-itonami/cloud-itonami-isic-6622),
[`6629`](https://github.com/cloud-itonami/cloud-itonami-isic-6629),
[`6520`](https://github.com/cloud-itonami/cloud-itonami-isic-6520),
[`6530`](https://github.com/cloud-itonami/cloud-itonami-isic-6530),
[`6820`](https://github.com/cloud-itonami/cloud-itonami-isic-6820),
[`6612`](https://github.com/cloud-itonami/cloud-itonami-isic-6612),
[`6492`](https://github.com/cloud-itonami/cloud-itonami-isic-6492),
[`6920`](https://github.com/cloud-itonami/cloud-itonami-isic-6920),
[`6611`](https://github.com/cloud-itonami/cloud-itonami-isic-6611),
[`7120`](https://github.com/cloud-itonami/cloud-itonami-isic-7120),
[`8620`](https://github.com/cloud-itonami/cloud-itonami-isic-8620),
[`8530`](https://github.com/cloud-itonami/cloud-itonami-isic-8530),
[`9200`](https://github.com/cloud-itonami/cloud-itonami-isic-9200),
[`7500`](https://github.com/cloud-itonami/cloud-itonami-isic-7500),
[`9603`](https://github.com/cloud-itonami/cloud-itonami-isic-9603),
[`9521`](https://github.com/cloud-itonami/cloud-itonami-isic-9521),
[`9321`](https://github.com/cloud-itonami/cloud-itonami-isic-9321),
[`8730`](https://github.com/cloud-itonami/cloud-itonami-isic-8730),
[`9102`](https://github.com/cloud-itonami/cloud-itonami-isic-9102),
[`9103`](https://github.com/cloud-itonami/cloud-itonami-isic-9103),
[`9602`](https://github.com/cloud-itonami/cloud-itonami-isic-9602),
[`9000`](https://github.com/cloud-itonami/cloud-itonami-isic-9000),
[`8890`](https://github.com/cloud-itonami/cloud-itonami-isic-8890),
[`8610`](https://github.com/cloud-itonami/cloud-itonami-isic-8610),
[`9311`](https://github.com/cloud-itonami/cloud-itonami-isic-9311),
[`8510`](https://github.com/cloud-itonami/cloud-itonami-isic-8510),
[`9412`](https://github.com/cloud-itonami/cloud-itonami-isic-9412),
[`6491`](https://github.com/cloud-itonami/cloud-itonami-isic-6491),
[`8720`](https://github.com/cloud-itonami/cloud-itonami-isic-8720),
[`8521`](https://github.com/cloud-itonami/cloud-itonami-isic-8521),
[`6619`](https://github.com/cloud-itonami/cloud-itonami-isic-6619),
[`3600`](https://github.com/cloud-itonami/cloud-itonami-isic-3600),
[`6190`](https://github.com/cloud-itonami/cloud-itonami-isic-6190),
[`3030`](https://github.com/cloud-itonami/cloud-itonami-isic-3030),
[`3830`](https://github.com/cloud-itonami/cloud-itonami-isic-3830),
[`7020`](https://github.com/cloud-itonami/cloud-itonami-isic-7020),
[`9420`](https://github.com/cloud-itonami/cloud-itonami-isic-9420),
[`9491`](https://github.com/cloud-itonami/cloud-itonami-isic-9491),
[`2610`](https://github.com/cloud-itonami/cloud-itonami-isic-2610),
[`3512`](https://github.com/cloud-itonami/cloud-itonami-isic-3512),
[`8810`](https://github.com/cloud-itonami/cloud-itonami-isic-8810),
[`8691`](https://github.com/cloud-itonami/cloud-itonami-isic-8691),
[`8569`](https://github.com/cloud-itonami/cloud-itonami-isic-8569),
[`6419`](https://github.com/cloud-itonami/cloud-itonami-isic-6419),
[`7310`](https://github.com/cloud-itonami/cloud-itonami-isic-7310),
[`7320`](https://github.com/cloud-itonami/cloud-itonami-isic-7320),
[`7210`](https://github.com/cloud-itonami/cloud-itonami-isic-7210),
[`7410`](https://github.com/cloud-itonami/cloud-itonami-isic-7410),
[`8710`](https://github.com/cloud-itonami/cloud-itonami-isic-8710),
[`8541`](https://github.com/cloud-itonami/cloud-itonami-isic-8541),
[`8690`](https://github.com/cloud-itonami/cloud-itonami-isic-8690),
[`9601`](https://github.com/cloud-itonami/cloud-itonami-isic-9601),
[`6420`](https://github.com/cloud-itonami/cloud-itonami-isic-6420),
[`7420`](https://github.com/cloud-itonami/cloud-itonami-isic-7420),
[`9609`](https://github.com/cloud-itonami/cloud-itonami-isic-9609),
[`8550`](https://github.com/cloud-itonami/cloud-itonami-isic-8550),
[`7010`](https://github.com/cloud-itonami/cloud-itonami-isic-7010),
[`8790`](https://github.com/cloud-itonami/cloud-itonami-isic-8790),
[`8542`](https://github.com/cloud-itonami/cloud-itonami-isic-8542),
[`6411`](https://github.com/cloud-itonami/cloud-itonami-isic-6411),
[`7490`](https://github.com/cloud-itonami/cloud-itonami-isic-7490),
[`9319`](https://github.com/cloud-itonami/cloud-itonami-isic-9319),
[`9329`](https://github.com/cloud-itonami/cloud-itonami-isic-9329),
[`9312`](https://github.com/cloud-itonami/cloud-itonami-isic-9312),
[`9492`](https://github.com/cloud-itonami/cloud-itonami-isic-9492)) --
here it is **MemberOrgOps-LLM ⊣ Membership Governance Governor**.

> **Why an actor layer at all?** An LLM is great at drafting a member
> intake summary, normalizing records, and checking whether a
> position's own recorded last review actually stays within its own
> recorded maximum review interval -- but it has **no notion of which
> jurisdiction's tax-exempt-status/political-activity-restriction law
> is official, no authority to publish a real public position, and no
> way to know on its own whether a mandatory tax-exempt-status risk
> has actually been resolved**. Letting it publish a position directly
> invites fabricated regulatory citations, a stale advocacy position
> being republished unreviewed, and a genuine risk to the
> organization's own tax-exempt status being quietly overlooked -- and
> liability, for whoever runs it. This project seals the
> MemberOrgOps-LLM into a single node and wraps it with an independent
> **Membership Governance Governor**, a human **approval workflow**,
> and an immutable **audit ledger**.

## Scope: what this actor does and does not do

This actor covers member enrollment intake through tax-exempt-status/
political-activity-restriction regulatory assessment, tax-status-risk
screening and public-position publication. It does **not**, by
itself, hold any registration or tax-exempt determination required to
operate a civic/social club, consumer organization or advocacy group
in a given jurisdiction, and it does not claim to. It also does not
adjudicate whether a position actually crosses a legal line itself --
`memberorg.registry/position-review-overdue?` is a pure ground-truth
recompute against the position's own recorded fields, not a tax-law
determination. Whoever deploys and operates a live instance (a
qualified nonprofit operator) supplies any jurisdiction-specific
registration, the real tax-compliance-filing process and the real
membership-management-system integrations, and bears that
jurisdiction's liability -- the software supplies the governed,
spec-cited, audited execution scaffold so that operator does not have
to build the compliance layer from scratch.

### Actuation

**Publishing a real public position on the organization's behalf is
never autonomous, at any phase, by construction.** Two independent
layers enforce this (`memberorg.governor`'s `:actuation/publish-
position` high-stakes gate and `memberorg.phase`'s phase table, which
never puts `:actuation/publish-position` in any phase's `:auto` set)
-- see `memberorg.phase`'s docstring and
`test/memberorg/phase_test.clj`'s
`publish-position-never-auto-at-any-phase`. The actor may draft,
check and recommend; a human governing-body officer is always the one
who actually publishes a position. Matching `leasing`'s/
`underwriting`'s/`testlab`'s/`clinic`'s/`veterinary`'s/`funeral`'s/
`parksafety`'s/`salon`'s/`entertainment`'s/`facility`'s/
`consulting`'s/`advertising`'s/`polling`'s/`research`'s/`design`'s/
`sports`'s/`alliedhealth`'s/`photo`'s/`personalservice`'s/
`edsupport`'s/`cultural`'s/`proserv`'s/`sportsevent`'s/`recreation`'s/
`sportsclub`'s/`partyops`'s single-actuation shape, grounded directly
in this blueprint's own README text ("No automated proposal, by
itself, can complete the following without governor approval and
audit evidence: publishing a public position on the organization's
behalf") -- a POSITIVE actuation (committing a real publication
record), matching this fleet's majority actuation shape (`3600`/
`6190` are the fleet's two NEGATIVE-actuation exceptions), and
structurally the closest sibling to `partyops`/9492's own
`:actuation/publish-position` (same "publish a position" shape,
genuinely different domain -- tax-exempt-status risk for civic/
consumer/advocacy nonprofits rather than campaign-finance disclaimers
for political parties).

## The core contract

```
member intake + jurisdiction facts (memberorg.facts, spec-cited)
        |
        v
   ┌───────────────────────┐   proposal      ┌───────────────────────┐
   │ MemberOrgOps-LLM      │ ─────────────▶ │ Membership                     │  (independent system)
   │ (sealed)              │  + citations    │ Governance Governor:         │
   └───────────────────────┘                 │ spec-basis · evidence-       │
          │                 commit ◀┼ incomplete · tax-exempt-          │
          │                         │ status-risk-unresolved                │
    record + ledger        escalate ┼ (unconditional, NEW) · position-        │
          │              (ALWAYS for│ review-overdue (MAXIMUM-                 │
          │               :actuation│ ceiling, honest reuse) ·                  │
          │               /publish- │ already-published                          │
          ▼               position) └───────────────────────┘
      human approval
```

**The MemberOrgOps-LLM never publishes a position the Membership
Governance Governor would reject, and never does so without a human
sign-off.** Hard violations (fabricated regulatory requirements;
unsupported evidence; an unresolved tax-exempt-status risk; an
overdue position review; a double publication) force **hold** and
*cannot* be approved past; a clean publication proposal still always
routes to a human.

## Run

```bash
clojure -M:dev:run     # walk one clean single-actuation lifecycle + four HARD-hold cases through the actor
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot
performs the physical domain work**. Here a document-courier robot
handles physical member-mailing fulfillment where used, under the
actor, gated by the independent **Membership Governance Governor**.
The governor never dispatches hardware itself; `:high`/`:safety-
critical` actions require human sign-off.

## Open business

This repository is not only source code. It is a public, forkable
business model:

| Layer | What is open |
|---|---|
| OSS core | Actor runtime, Membership Governance Governor, position-publication draft records, audit ledger |
| Business blueprint | Customer, offer, pricing, unit economics, sales motion |
| Operator playbook | How to fork, license, deploy and support the service in a jurisdiction |
| Trust controls | Governance, security reporting, actuation invariant, audit requirements |

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md) to start this as an
open business on itonami.cloud, and
[`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) for the
full architecture and decision record.

## Capability layer

This blueprint resolves its technology stack via
[`kotoba-lang/industry`](https://github.com/kotoba-lang/industry) (ISIC
`9499`). This vertical's member/operational records are practice-
specific rather than a shared cross-operator data contract, so
`memberorg.*` runs on the generic robotics/identity/forms/dmn/bpmn/
audit-ledger stack only -- no bespoke domain capability lib to
reference at all.

## Layout

| File | Role |
|---|---|
| `src/memberorg/store.cljc` | **Store** protocol -- `MemStore` ‖ `DatomicStore` (`langchain.db`) + append-only audit ledger + position-publication history. No dynamically-filed sub-record -- the actuation op acts directly on a pre-seeded position, and the double-actuation guard checks a dedicated `:published?` boolean rather than a `:status` value |
| `src/memberorg/registry.cljc` | Position-publication draft records, plus `position-review-overdue?` -- an HONEST reuse of `eldercare.registry/care-plan-review-overdue?`'s own periodic-review-overdue temporal shape (the 14th instance of this fleet's MAXIMUM-ceiling family overall), applied to advocacy-position review, not claimed as new |
| `src/memberorg/facts.cljc` | Per-jurisdiction tax-exempt-status/political-activity-restriction catalog with an official spec-basis citation per entry, honest coverage reporting |
| `src/memberorg/memberorgopsllm.cljc` | **MemberOrgOps-LLM** -- `mock-advisor` ‖ `llm-advisor`; intake/jurisdiction-verification/tax-status-screening/publication proposals |
| `src/memberorg/governor.cljc` | **Membership Governance Governor** -- 5 HARD checks (spec-basis · evidence-incomplete · tax-exempt-status-risk-unresolved, unconditional evaluation, GENUINELY NEW, the 60th grounding of this discipline · position-review-overdue, MAXIMUM-ceiling reuse, the 14th instance, not claimed as new · already-published guard) + 1 soft (confidence/actuation gate) |
| `src/memberorg/phase.cljc` | **Phase 0→3** -- read-only → assisted intake → assisted verify → supervised (position publication always human; member intake is the ONLY auto-eligible op, no direct capital risk) |
| `src/memberorg/operation.cljc` | **OperationActor** -- langgraph-clj StateGraph |
| `src/memberorg/sim.cljc` | demo driver |
| `test/memberorg/*_test.clj` | governor contract · phase invariants · store parity · registry conformance · facts coverage |

## Business-process coverage (honest)

This actor covers member enrollment intake through tax-exempt-status/
political-activity-restriction regulatory assessment, tax-status-risk
screening and public-position publication -- the core governed
lifecycle this blueprint's own `docs/business-model.md` names as its
Offer:

| Covered | Not covered (out of scope for this R0) |
|---|---|
| Member intake + per-jurisdiction evidence checklisting, HARD-gated on an official spec-basis citation (`:member/intake`/`:position/verify`) | Real membership-management-system integration, real tax-compliance-filing itself (see `memberorg.facts`'s docstring) |
| Tax-exempt-status-risk screening, evaluated unconditionally so the screening op itself can HARD-hold on its own finding (`:taxstatus/screen`) | Member-benefit administration -- deliberately outside this actor's R0 scope (see this blueprint's own Offer text) |
| Position publication, HARD-gated on full evidence, a resolved tax-exempt-status risk and a current position review, plus a double-publication guard (`:actuation/publish-position`) | |
| Immutable audit ledger for every intake/verification/screening/publication decision | |

Extending coverage is additive: add the next gate (e.g. a member-
benefit-eligibility check) as its own governed op with its own HARD
checks and tests, following the SAME "an independent governor
re-verifies against the actor's own records before any real-world
act" pattern this repo's flagship op already establishes.

## Jurisdiction coverage (honest)

`memberorg.facts/coverage` reports how many requested jurisdictions
actually have an official spec-basis in `memberorg.facts/catalog` --
currently 4 seeded (JPN, USA, GBR, DEU) out of ~194 jurisdictions
worldwide. This is a starting catalog to prove the governor contract
end-to-end, not a claim of global coverage. Adding a jurisdiction is
additive: one map entry in `memberorg.facts/catalog`, citing a real
official source -- never fabricate a jurisdiction's requirements to
make coverage look bigger.

## Maturity

`:implemented` -- `MemberOrgOps-LLM` + `Membership Governance
Governor` run as real, tested code (see `Run` above), promoted from
the originally-published `:blueprint`-tier scaffold, modeled closely
on the seventy-four prior actors' architecture. See
`docs/adr/0001-architecture.md` for the history and design.

## License

Code and implementation templates are AGPL-3.0-or-later.
