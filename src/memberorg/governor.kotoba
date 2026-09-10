(ns memberorg.governor
  "Membership Governance Governor -- the independent compliance layer
  that earns the MemberOrgOps-LLM the right to commit. The LLM has no
  notion of jurisdictional tax-exempt-status/political-activity-
  restriction law, whether a position's own recorded last review
  actually stays within its own recorded maximum review interval, or
  when an act stops being a draft and becomes a real-world public
  position publication, so this MUST be a separate system able to
  *reject* a proposal and fall back to HOLD -- the other-membership-
  organization analog of `cloud-itonami-isic-6512`'s `casualty.
  governor`.

  Five checks, in priority order, ALL HARD violations: a human approver
  CANNOT override them (you don't get to approve your way past a
  fabricated jurisdiction spec-basis, incomplete evidence, an
  unresolved tax-exempt-status risk, an overdue position review, or a
  double publication of the same position). The confidence/actuation
  gate is SOFT: it asks a human to look (low confidence / actuation),
  and the human may approve -- but see `memberorg.phase`: for `:stake
  :actuation/publish-position` (a real public act) NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

  This vertical's own named example activities (civic/social clubs,
  consumer organizations, environmental advocacy groups) are
  genuinely distinct from `partyops`/9492's political-PARTY domain --
  most organizations of THIS type are tax-exempt/charitable
  nonprofits, where the real, load-bearing concern is whether
  publishing a position risks the organization's own tax-exempt
  status, not a campaign-finance-disclaimer requirement.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source (`memberorg.
                                       facts`), or invent one?
    2. Evidence incomplete         -- for `:actuation/publish-
                                       position`, has the jurisdiction
                                       actually been assessed with a
                                       full position-publication
                                       evidence checklist (member-
                                       consensus/tax-status-review/
                                       governing-body-approval/
                                       publication-notice) on file?
    3. Tax-exempt-status risk
       unresolved                    -- reported by THIS proposal
                                       itself (a `:taxstatus/screen`
                                       that just found an unresolved
                                       risk), or already on file for
                                       the position (`:taxstatus/
                                       screen`/`:actuation/publish-
                                       position`). Evaluated
                                       UNCONDITIONALLY (not scoped to a
                                       specific op), the SAME discipline
                                       `casualty.governor/sanctions-
                                       violations`'s original fix
                                       establishes -- GENUINELY NEW
                                       (grep-verified absent -- zero
                                       hits for 'tax-exempt'/'501(c)'/
                                       'lobbying'/'charitable-status'
                                       across every prior sibling,
                                       including `partyops.facts`
                                       itself), the 60th distinct
                                       application of this discipline
                                       overall (most recently
                                       `partyops.governor/campaign-
                                       finance-disclaimer-missing-
                                       violations` at 59th). Grounded
                                       in real nonprofit tax law: US
                                       IRC §501(c)(3)/(c)(4), UK
                                       Charity Commission CC9, Japan's
                                       public-interest-corporation
                                       political-activity
                                       restrictions, Germany's AO §52
                                       Gemeinnützigkeit.
    4. Position review overdue     -- for `:actuation/publish-
                                       position`, INDEPENDENTLY
                                       recompute whether the
                                       position's own days-since-last-
                                       review exceeds its own max-
                                       review-interval-days
                                       (`memberorg.registry/position-
                                       review-overdue?`) -- needs no
                                       proposal inspection or stored-
                                       verdict lookup at all. The
                                       FOURTEENTH instance of this
                                       fleet's MAXIMUM-ceiling check
                                       family (`recreation.registry/
                                       occupancy-exceeds-capacity?`
                                       was the 13th), an honest reuse
                                       of `eldercare.registry/care-
                                       plan-review-overdue?`'s own
                                       periodic-review-overdue
                                       temporal shape, not claimed as
                                       new.
    5. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:actuation/
                                       publish-position` (a REAL public
                                       act) -> escalate.

  One more guard, double-publication prevention, is enforced but NOT
  listed as a numbered HARD check above because it needs no upstream
  comparison at all -- `already-published-violations` refuses to
  publish the SAME position twice, off a dedicated `:published?` fact
  (never a `:status` value) -- the SAME 'check a dedicated boolean,
  not status' discipline every prior governor's guards establish,
  informed by `cloud-itonami-isic-6492`'s status-lifecycle bug
  (ADR-2607071320)."
  (:require [memberorg.facts :as facts]
            [memberorg.registry :as registry]
            [memberorg.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Publishing a real public position on the organization's behalf is
  the ONE real-world actuation event this actor performs -- a single-
  member set, matching every other single-actuation sibling's shape."
  #{:actuation/publish-position})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:position/verify` (or `:actuation/publish-position`) proposal
  with no spec-basis citation is a HARD violation -- never invent a
  jurisdiction's tax-exempt-status/political-activity-restriction
  requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:position/verify :actuation/publish-position} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:actuation/publish-position`, the jurisdiction's required
  member-consensus/tax-status-review/governing-body-approval/
  publication-notice evidence must actually be satisfied -- do not
  trust the advisor's self-reported confidence alone."
  [{:keys [op subject]} st]
  (when (= op :actuation/publish-position)
    (let [p (store/position st subject)
          verification (store/verify-of st subject)]
      (when-not (and verification
                     (facts/required-evidence-satisfied?
                      (:jurisdiction p) (:checklist verification)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(会員合意記録/税務上の地位審査記録/運営機関承認記録/公表通知記録等)が充足していない状態での公表提案"}]))))

(defn- tax-exempt-status-risk-unresolved-violations
  "An unresolved tax-exempt-status risk -- reported by THIS proposal
  (e.g. a `:taxstatus/screen` that itself just found one unresolved),
  or already on file in the store for the position (`:taxstatus/
  screen`/`:actuation/publish-position`) -- is a HARD, un-overridable
  hold. Evaluated UNCONDITIONALLY (not scoped to a specific op) so the
  screening op itself can HARD-hold on its own finding."
  [{:keys [op subject]} proposal st]
  (let [hit-in-proposal? (true? (get-in proposal [:value :tax-status-risk-unresolved?]))
        position-id (when (contains? #{:taxstatus/screen :actuation/publish-position} op) subject)
        hit-on-file? (and position-id (:tax-exempt-status-risk-unresolved? (store/position st position-id)))]
    (when (or hit-in-proposal? hit-on-file?)
      [{:rule :tax-exempt-status-risk-unresolved
        :detail "税務上の地位への影響が未解決の状態での公表提案は進められない"}])))

(defn- position-review-overdue-violations
  "For `:actuation/publish-position`, INDEPENDENTLY recompute whether
  the position's own days-since-last-review exceeds its own max-
  review-interval-days via `memberorg.registry/position-review-
  overdue?` -- needs no proposal inspection or stored-verdict lookup
  at all, an honest reuse of `eldercare.registry`'s own periodic-
  review-overdue temporal shape."
  [{:keys [op subject]} st]
  (when (= op :actuation/publish-position)
    (let [p (store/position st subject)]
      (when (registry/position-review-overdue? p)
        [{:rule :position-review-overdue
          :detail (str subject " の前回審査経過日数(" (:days-since-last-review p)
                      ")が最大審査間隔(" (:max-review-interval-days p) ")を超過している")}]))))

(defn- already-published-violations
  "For `:actuation/publish-position`, refuses to publish the SAME
  position twice, off a dedicated `:published?` fact -- see ns
  docstring for why this sidesteps the status-lifecycle risk `cloud-
  itonami-isic-6492`'s ADR-0001 documents."
  [{:keys [op subject]} st]
  (when (= op :actuation/publish-position)
    (when (store/position-already-published? st subject)
      [{:rule :already-published
        :detail (str subject " は既に公表済み")}])))

(defn check
  "Censors a MemberOrgOps-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (tax-exempt-status-risk-unresolved-violations request proposal st)
                           (position-review-overdue-violations request st)
                           (already-published-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
