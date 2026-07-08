(ns memberorg.facts
  "Per-jurisdiction nonprofit-membership-organization tax-exempt-
  status/political-activity-restriction regulatory catalog -- the
  G2-style spec-basis table the Membership Governance Governor checks
  every position/verify proposal against ('did the advisor cite an
  OFFICIAL public source for this jurisdiction's tax-exempt-status/
  political-activity-restriction requirements, or did it invent one?').

  This blueprint's own named example activities (civic/social clubs,
  consumer organizations, environmental advocacy groups) are
  genuinely distinct from `partyops`/9492's political-PARTY domain --
  most organizations of THIS type are structured as tax-exempt/
  charitable nonprofits, where the real, load-bearing regulatory
  concern is whether publishing a position risks jeopardizing the
  organization's own tax-exempt/charitable status by crossing into
  prohibited political-campaign intervention or exceeding permissible
  lobbying activity (US IRC §501(c)(3)/(c)(4), UK Charity Commission
  CC9 guidance, Japan's public-interest-corporation political-activity
  restrictions, Germany's Abgabenordnung §52 Gemeinnützigkeit) -- NOT
  the campaign-finance-disclaimer/imprint concern `partyops.facts`
  already covers for political-PARTY communications.

  Coverage is reported HONESTLY (see `coverage`), the same discipline
  every sibling actor's `facts` namespace uses: a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.

  Seed values are drawn from each jurisdiction's official tax/
  charitable-status regulator (see `:provenance`); they are a
  STARTING catalog, not a from-scratch survey of all ~194
  jurisdictions.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  member-consensus-record/tax-status-review-record/governing-body-
  approval-record/publication-notice-record evidence set submitted in
  some form; `:legal-basis` / `:owner-authority` / `:provenance` are
  the G2 citation the governor requires before any :position/verify
  proposal can commit."
  {"JPN" {:name "Japan"
          :owner-authority "内閣府 公益認定等委員会 (Cabinet Office, Public Interest Corporation Commission)"
          :legal-basis "公益社団法人及び公益財団法人の認定等に関する法律 -- 政治活動の制限"
          :national-spec "公益法人の政治活動制限に関する認定基準"
          :provenance "https://www.koeki-info.go.jp/"
          :required-evidence ["会員合意記録 (member-consensus record)"
                              "税務上の地位審査記録 (tax-status-review record)"
                              "運営機関承認記録 (governing-body-approval record)"
                              "公表通知記録 (publication-notice record)"]}
   "USA" {:name "United States"
          :owner-authority "Internal Revenue Service (IRS)"
          :legal-basis "Internal Revenue Code §501(c)(3) (absolute prohibition on political-campaign intervention) / §501(c)(4) (substantial-part lobbying test)"
          :national-spec "IRS Rev. Rul. 2007-41 political-campaign-intervention factors"
          :provenance "https://www.irs.gov/charities-non-profits/charitable-organizations/the-restriction-of-political-campaign-intervention-by-section-501c3-tax-exempt-organizations"
          :required-evidence ["Member-consensus record"
                              "Tax-status-review record"
                              "Governing-body-approval record"
                              "Publication-notice record"]}
   "GBR" {:name "United Kingdom"
          :owner-authority "The Charity Commission for England and Wales"
          :legal-basis "Charities Act 2011 -- restriction on political purposes; Charity Commission guidance CC9 (Campaigning and political activity guidance for charities)"
          :national-spec "CC9 political-activity/campaigning boundary tests"
          :provenance "https://www.gov.uk/government/publications/speaking-out-guidance-on-campaigning-and-political-activity-by-charities-cc9"
          :required-evidence ["Member-consensus record"
                              "Tax-status-review record"
                              "Governing-body-approval record"
                              "Publication-notice record"]}
   "DEU" {:name "Germany"
          :owner-authority "Bundeszentralamt für Steuern (Federal Central Tax Office) / Finanzämter"
          :legal-basis "Abgabenordnung (AO) §52 -- Gemeinnützigkeit (charitable-status political-activity restrictions)"
          :national-spec "AO §52 Anforderungen an die politische Betätigung gemeinnütziger Körperschaften"
          :provenance "https://www.gesetze-im-internet.de/ao_1977/__52.html"
          :required-evidence ["Mitgliederkonsensprotokoll (member-consensus record)"
                              "Steuerstatusprüfungsprotokoll (tax-status-review record)"
                              "Vorstandsbeschluss (governing-body-approval record)"
                              "Veröffentlichungsmitteilung (publication-notice record)"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to publish a
  position on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-isic-9499 R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog, not a survey of all ~194 "
                 "jurisdictions -- extend `memberorg.facts/catalog`, "
                 "never fabricate a jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))
