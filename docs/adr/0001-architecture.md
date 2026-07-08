# ADR-0001: MemberOrgOps-LLM ⊣ Membership Governance Governor architecture

## Status

Accepted. `cloud-itonami-isic-9499` promoted from `:blueprint` to
`:implemented` in the `kotoba-lang/industry` registry.

## Context

`cloud-itonami-isic-9499` publishes an OSS business blueprint for
activities of other membership organizations not elsewhere
classified: civic/social clubs, consumer organizations, environmental
advocacy groups. Like every prior actor in this fleet, the blueprint
alone is not an implementation: this ADR records the governed-actor
architecture that promotes it to real, tested code, following the
same langgraph StateGraph + independent Governor + Phase 0→3 rollout
pattern established by `cloud-itonami-isic-6511` (life insurance) and
applied across seventy-four prior siblings, most recently
`cloud-itonami-isic-9492` (political organizations).

## Decision

### Decision 1: single-actuation shape, and this build's place in the fleet-wide governor-name-collision survey

This blueprint's own README/business-model.md/operator-guide.md
consistently name only ONE real-world act: "publishing a public
position on the organization's behalf." Matching every prior single-
actuation sibling's shape, `high-stakes` is the one-member set
`#{:actuation/publish-position}`, a POSITIVE actuation. This is the
LAST genuinely clean `:blueprint`-tier candidate identified by
`sportsclub`/9312's own governor-name-collision survey (re-confirmed
by `partyops`/9492's own ADR): ISIC 9499's own `:membership-
governance-governor` has NO collision with any already-implemented
sibling (distinct from `sportsclub`/9312's `:membership-governor`,
`association`/9412's, `congregation`/9491's, `union`/9420's, and
`partyops`/9492's own governor names). This build is structurally the
closest sibling to `partyops`/9492's own `:actuation/publish-
position` -- same "publish a position" actuation verb, genuinely
different domain (tax-exempt-status risk for civic/consumer/advocacy
nonprofits, not campaign-finance disclaimers for political parties).

### Decision 2: entity and op shape

The primary entity is a `position`, matching the business-model.md's
own Offer language ("program/advocacy-position proposal"). Four ops:
`:member/intake` (directory upsert, no capital risk), `:position/
verify` (per-jurisdiction tax-exempt-status/political-activity-
restriction evidence checklist, never auto), `:taxstatus/screen`
(tax-exempt-status-risk screening, unconditional-evaluation
discipline, never auto), and `:actuation/publish-position` (POSITIVE,
high-stakes -- publishing a real public position). "Member-benefit
administration" (also named in the Offer) is deliberately NOT part of
this R0's governed op surface -- see README's Business-process
coverage table.

### Decision 3: `tax-exempt-status-risk-unresolved-violations` -- the 60th unconditional-evaluation screening grounding, a genuinely new concept

Before writing this check, every prior sibling's governor/registry/
facts namespaces were grepped for "tax-exempt", "501(c)", "lobbying"
and "charitable-status" -- zero hits, INCLUDING against `partyops.
facts` itself (the structurally closest sibling), confirming this is
a genuinely new unconditional-evaluation concept distinct from
`partyops`/9492's own campaign-finance-disclaimer concern. It reuses
the unconditional-evaluation DISCIPLINE (`casualty.governor/
sanctions-violations`'s original fix) for the 60th distinct
application overall, continuing the count established across this
fleet's builds (most recently `partyops.governor/campaign-finance-
disclaimer-missing-violations` at 59th). Grounded in real nonprofit
tax law: US Internal Revenue Code §501(c)(3) (absolute prohibition on
political-campaign intervention) / §501(c)(4) (substantial-part
lobbying test), UK Charity Commission guidance CC9, Japan's public-
interest-corporation political-activity restrictions, Germany's
Abgabenordnung §52 Gemeinnützigkeit. Gates `:taxstatus/screen` and
the actuation.

### Decision 4: `position-review-overdue?` -- an honest reuse of `eldercare`/8730's own periodic-review-overdue temporal shape, the 14th MAXIMUM-ceiling instance

`eldercare.registry/care-plan-review-overdue?` established the
family's ORIGINAL temporal MAXIMUM-ceiling instance (`days-since-
last-X` must not exceed its own `max-review-interval-days`).
`memberorg.registry/position-review-overdue?` reuses that exact
shape (same field-comparison pattern) for an advocacy-position/
program-statement review context rather than an assisted-living
care-plan review context -- the 14th instance of this fleet's
MAXIMUM-ceiling family overall (`recreation.registry/occupancy-
exceeds-capacity?` was the 13th). An honest reuse, not claimed as new
-- grounded in real nonprofit-governance practice: civic/advocacy
organizations commonly require periodic board review of standing
positions. Gates only the actuation (a pure ground-truth recompute).

### Decision 5: dedicated double-actuation-guard boolean

`:published?` is a dedicated boolean on the `position` record, never
a single `:status` value -- the same discipline every prior sibling
governor's guards establish (most directly, `partyops.governor`'s own
`:published?` guard for the structurally closest sibling), informed
by `cloud-itonami-isic-6492`'s status-lifecycle bug
(ADR-2607071320).

### Decision 6: Store protocol, MemStore + DatomicStore parity

`memberorg.store/Store` is implemented by both `MemStore` (atom-
backed, default for dev/tests/demo) and `DatomicStore` (`langchain.
db`-backed), proven to satisfy the same contract in `test/
memberorg/store_contract_test.clj` -- the same seam every sibling
actor uses so swapping the SSoT backend is a configuration change,
not a rewrite. The protocol's per-entity accessor is named `position`
directly -- not a Clojure special form, so no `-of` suffix workaround
was needed.

### Decision 7: Phase 0→3 rollout

Phase 3's `:auto` set has exactly one member, `:member/intake` (no
capital risk). `:position/verify` and `:taxstatus/screen` are never
auto-eligible at any phase (matching every sibling's screening/
verification-op posture), and `:actuation/publish-position` is
permanently excluded from every phase's `:auto` set -- a structural
fact, not a rollout milestone, enforced by BOTH `memberorg.phase` and
`memberorg.governor`'s `high-stakes` set independently.

### Decision 8: no bespoke domain capability lib

This blueprint's own `:itonami.blueprint/required-technologies`
names no domain-specific capability beyond the generic robotics/
identity/forms/dmn/bpmn/audit-ledger stack -- there was no
capability-lib decision to make at all.

### Decision 9: mock + LLM advisor pair

`memberorg.memberorgopsllm` provides `mock-advisor` (deterministic,
default everywhere -- the actor graph and governor contract run
offline) and `llm-advisor` (backed by `langchain.model/ChatModel`,
with a defensive EDN-proposal parser so a malformed LLM response
degrades to a safe low-confidence noop rather than ever auto-
publishing a position).

### Decision 10: no `blueprint.edn` field-sync fixes needed

Matching `photo`/7420's, `personalservice`/9609's, `edsupport`/8550's,
`headoffice`/7010's, `residential`/8790's, `cultural`/8542's,
`reserve`/6411's, `proserv`/7490's, `sportsevent`/9319's,
`recreation`/9329's, `sportsclub`/9312's and `partyops`/9492's own
experience, this repo's `blueprint.edn` already had the correct
`isic-` prefixed `:id` and correctly populated `:required-
technologies`/`:optional-technologies` matching the `kotoba-lang/
industry` registry's own entry for `"9499"` exactly -- only the
`:maturity` field itself needed adding.

## Alternatives considered

- **Framing `tax-exempt-status-risk-unresolved?` as a variant of
  `partyops`/9492's campaign-finance-disclaimer-missing concept.**
  Rejected: these are genuinely distinct real-world concerns (tax-
  exempt-status risk vs. election-communication disclosure
  requirements), even though both actors share the "publish a
  position" actuation shape; grep-verified `partyops.facts` itself
  has zero mention of tax-exempt status.
- **Framing `position-review-overdue?` as a new concept.** Rejected:
  it is structurally identical to `eldercare.registry/care-plan-
  review-overdue?`'s own shape; honest reuse characterization
  matches this fleet's precedent-verification discipline.
- **A dual-actuation shape.** Rejected: the blueprint's own text
  consistently names only ONE real-world act.

## Consequences

- Seventy-sixth actor promoted in this fleet's registry (75
  implemented before this build).
- Establishes a genuinely NEW unconditional-evaluation-screening
  concept (tax-exempt-status-risk-unresolved), grep-verified absent
  from every prior sibling (including `partyops`/9492) before the
  claim was finalized.
- Documents an honest reuse of `eldercare`/8730's own periodic-
  review-overdue temporal shape (position-review-overdue, the 14th
  MAXIMUM-ceiling instance), not claimed as new.
- `MemStore` ‖ `DatomicStore` parity is proven by `test/memberorg/
  store_contract_test.clj`.
- `blueprint.edn` required no field-sync fixes this time (already
  correct) -- only the `:maturity` flip itself.
- **This build exhausts the fleet-wide governor-name-collision
  survey**: as of this build, ALL remaining `:blueprint`-tier
  candidates (7220, 8522, 8549, 9411, 9512, 9522, 9523, 9524, 9529)
  are confirmed blocked by an exact governor-name collision with an
  already-implemented sibling. Future vertical selection from the
  current registry state will need a different strategy: either a
  fresh critical look at whether a legitimately DIFFERENT governor
  name could still be justified for one of the blocked candidates (a
  more invasive judgment call than this fleet has made so far), or
  waiting for newly-registered `:blueprint`-tier entries.

## References

- `orgs/cloud-itonami/cloud-itonami-isic-9499/README.md`
- `orgs/cloud-itonami/cloud-itonami-isic-9499/docs/business-model.md`
- `orgs/cloud-itonami/cloud-itonami-isic-8730/src/eldercare/registry.cljc` (`care-plan-review-overdue?` origin)
- `orgs/cloud-itonami/cloud-itonami-isic-9492/src/partyops/governor.cljc` (structurally closest sibling)
- `orgs/kotoba-lang/industry/resources/kotoba/industry/registry.edn` (entry `"9499"`)
