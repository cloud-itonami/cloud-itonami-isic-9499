(ns memberorg.render-html
  "Build-time HTML renderer for `docs/samples/operator-console.html`.

  Closes flagship checklist item 2 for `cloud-itonami-isic-9499`: this
  repo previously had NO demo page and no generator at all. This
  namespace drives the REAL actor stack (`memberorg.operation` ->
  `memberorg.governor` -> `memberorg.store`) through a scenario
  adapted from this repo's own `memberorg.sim` demo driver (`clojure
  -M:dev:run`, confirmed BEFORE writing this file to produce a
  sensible ledger against the real seeded position ids
  `position-1`..`position-4`), and extended by one case so that ALL
  FIVE of this governor's HARD rules actually fire -- `memberorg.sim`
  exercises four and leaves `:evidence-incomplete` undemonstrated.

  Everything on the page is derived from a live run or from this
  repo's own source data:

    - the position table               <- `store/all-positions`
    - the HARD-hold table              <- `:governor-hold` ledger facts
    - the op/phase gate table          <- `memberorg.phase/phases` +
                                          `memberorg.governor/high-stakes`
                                          (read at render time, not retyped)
    - the jurisdiction table           <- `memberorg.facts/catalog`
    - the coverage note                <- `memberorg.facts/coverage`
    - the approver-attribution table   <- the run's `:approval-granted`
                                          audit facts joined against what
                                          the SSoT registers actually kept

  No mock data and no hand-written HTML rows (ADR-2607122300 §1).

  Deterministic: no timestamps in the page content, byte-identical
  across reruns against the same seed (verify by diffing two
  consecutive runs into scratch dirs).

  Usage: `clojure -M:dev:render-html [out-file]`
  (default `docs/samples/operator-console.html`)."
  (:require [jp-go-dds.skin]
            [clojure.string :as str]
            [memberorg.facts :as facts]
            [memberorg.governor :as governor]
            [memberorg.phase :as phase]
            [memberorg.store :as store]
            [memberorg.operation :as op]
            [langgraph.graph :as g]))

(def ^:private operator
  "The same operator context `memberorg.sim` uses -- a phase-3
  governing-body officer."
  {:actor-id "op-1" :actor-role :governing-body-officer :phase 3})

(defn- exec! [actor tid request]
  (g/run* actor {:request request :context operator} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}}
          {:thread-id tid :resume? true}))

(defn- audit-of
  "The `:audit` channel of a completed `g/run*` -- the run's own
  decision facts, including the `:approval-granted` facts that the
  store ledger never sees (only `:commit`/`:hold` nodes append to the
  ledger, so approver identity lives ONLY here)."
  [run]
  (get-in run [:state :audit] []))

(defn run-demo!
  "Runs a fresh seeded store through a scenario that reaches every
  disposition this actor can produce, and every HARD rule its governor
  declares.

  position-1 clears a full lifecycle -- member intake (auto-commits at
  phase 3: `:member/intake` is the only op in phase 3's `:auto` set), a
  JPN jurisdiction verification (escalates -- not auto-eligible --
  approved), a tax-exempt-status screening that finds no risk
  (approved), and a position publication (ALWAYS escalates --
  `:actuation/publish-position` is absent from every phase's `:auto`
  set AND is in `governor/high-stakes`, two independent layers --
  approved, publishing JPN-POS-000000).

  Then five HARD holds, one per governor rule, none of which ever
  reaches a human:

    :no-spec-basis                     position-2 -- jurisdiction ATL is
                                       deliberately absent from
                                       `memberorg.facts/catalog`, so the
                                       advisor cites nothing rather than
                                       inventing requirements.
    :evidence-incomplete               position-2 -- publication proposed
                                       while no verification is on file
                                       (its own verify HARD-held above,
                                       so nothing was ever committed).
    :position-review-overdue           position-3 -- seeded
                                       days-since-last-review 120 EXCEEDS
                                       its own max-review-interval-days 90,
                                       recomputed independently by
                                       `registry/position-review-overdue?`.
    :tax-exempt-status-risk-unresolved position-4 -- seeded
                                       tax-exempt-status-risk-unresolved?
                                       true; the screening op HARD-holds on
                                       its own finding.
    :already-published                 position-1 -- a second publication
                                       of an already-published position.

  Returns {:db store :approvals [..]} -- `:approvals` are the real
  `:approval-granted` audit facts, kept because the ledger drops them."
  []
  (let [db (store/seed-db)
        actor (op/build db)
        approvals (atom [])
        approve-and-record!
        (fn [tid]
          (let [r (approve! actor tid)]
            (swap! approvals into
                   (filter #(= :approval-granted (:t %)) (audit-of r)))
            r))]
    ;; ---- position-1: a complete, clean lifecycle ----
    (exec! actor "t1-intake" {:op :member/intake :subject "position-1"
                              :patch {:id "position-1"
                                      :position-name "clean-water-advocacy-platform"}})

    (exec! actor "t1-verify" {:op :position/verify :subject "position-1"})
    (approve-and-record! "t1-verify")

    (exec! actor "t1-taxstatus" {:op :taxstatus/screen :subject "position-1"})
    (approve-and-record! "t1-taxstatus")

    (exec! actor "t1-publish" {:op :actuation/publish-position :subject "position-1"})
    (approve-and-record! "t1-publish")

    ;; ---- HARD hold 1: no official spec-basis for jurisdiction ATL ----
    (exec! actor "t2-verify" {:op :position/verify :subject "position-2" :no-spec? true})

    ;; ---- HARD hold 2: publication with no verification on file ----
    (exec! actor "t2-publish" {:op :actuation/publish-position :subject "position-2"})

    ;; ---- HARD hold 3: position review overdue (120 > 90) ----
    (exec! actor "t3-verify" {:op :position/verify :subject "position-3"})
    (approve-and-record! "t3-verify")
    (exec! actor "t3-publish" {:op :actuation/publish-position :subject "position-3"})

    ;; ---- HARD hold 4: unresolved tax-exempt-status risk ----
    (exec! actor "t4-taxstatus" {:op :taxstatus/screen :subject "position-4"})

    ;; ---- HARD hold 5: double publication ----
    (exec! actor "t1-republish" {:op :actuation/publish-position :subject "position-1"})

    {:db db :approvals @approvals}))

;; ----------------------------- rendering helpers -----------------------------

(defn- esc [v]
  (-> (str v)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn- kw-name [v] (if (keyword? v) (name v) (str v)))

(defn- code [v] (str "<code>" (esc v) "</code>"))

(defn- cell [class v] (str "<span class=\"" class "\">" v "</span>"))

(defn- row [& cells]
  (str "        <tr>" (str/join (map #(str "<td>" % "</td>") cells)) "</tr>"))

(defn- table [headers rows]
  (str "    <table>\n"
       "      <thead><tr>"
       (str/join (map #(str "<th>" (esc %) "</th>") headers))
       "</tr></thead>\n"
       "      <tbody>\n"
       (str/join "\n" rows) "\n"
       "      </tbody>\n"
       "    </table>\n"))

(defn- section [title lead body]
  (str "  <section class=\"card\">\n"
       "    <h2>" (esc title) "</h2>\n"
       "    <p class=\"muted\">" lead "</p>\n"
       body
       "  </section>\n"))

;; ----------------------------- derived views -----------------------------

(defn- holds
  "Every HARD hold this run produced, straight off the ledger."
  [ledger]
  (filter #(= :governor-hold (:t %)) ledger))

(defn- last-fact-for [ledger position-id]
  (last (filter #(= (:subject %) position-id) ledger)))

(defn- status-cell [ledger position-id]
  (let [f (last-fact-for ledger position-id)]
    (case (:t f)
      nil            (cell "muted" "no activity")
      :committed     (cell "ok" "committed")
      :governor-hold (cell "critical"
                           (str "HARD hold &middot; "
                                (esc (kw-name (-> f :violations first :rule)))))
      (cell "muted" (esc (kw-name (:t f)))))))

(defn- review-cell [{:keys [days-since-last-review max-review-interval-days]}]
  (let [overdue? (and (number? days-since-last-review)
                      (number? max-review-interval-days)
                      (> days-since-last-review max-review-interval-days))]
    (cell (if overdue? "critical" "ok")
          (str "<span class=\"num\">" (esc days-since-last-review) "</span> / "
               "<span class=\"num\">" (esc max-review-interval-days) "</span> d"
               (when overdue? " &middot; overdue")))))

(defn- position-row [ledger {:keys [id position-name jurisdiction published?
                                    publication-number
                                    tax-exempt-status-risk-unresolved?] :as p}]
  (row (code id)
       (esc position-name)
       (esc jurisdiction)
       (review-cell p)
       (if tax-exempt-status-risk-unresolved?
         (cell "critical" "unresolved")
         (cell "ok" "resolved"))
       (if published?
         (cell "ok" (str "published &middot; " (code publication-number)))
         (cell "muted" "not published"))
       (status-cell ledger id)))

(defn- hold-row [{:keys [op subject violations confidence]}]
  (let [{:keys [rule detail]} (first violations)]
    (row (cell "critical" (esc (kw-name rule)))
         (code op)
         (code subject)
         (esc detail)
         (str "<span class=\"num\">" (esc confidence) "</span>"))))

(defn- ledger-row [{:keys [t op subject basis summary]}]
  (row (case t
         :committed (cell "ok" "committed")
         :governor-hold (cell "critical" "governor-hold")
         (cell "muted" (esc (kw-name t))))
       (code op)
       (code subject)
       (esc (some->> basis (map kw-name) (str/join ", ")))
       (esc summary)))

(defn- gate-row
  "One row per write op, derived from `memberorg.phase/phases` and
  `memberorg.governor/high-stakes` -- read from source at render time
  so the page cannot drift from the actual gate."
  [op]
  (let [auto-phases (->> phase/phases
                         (filter (fn [[_ {:keys [auto]}]] (contains? auto op)))
                         (map key) sort vec)
        high-stakes? (contains? governor/high-stakes op)
        write-phases (->> phase/phases
                          (filter (fn [[_ {:keys [writes]}]] (contains? writes op)))
                          (map key) sort vec)]
    (row (code op)
         (esc (str/join ", " (map str write-phases)))
         (if (seq auto-phases)
           (cell "ok" (str "phase " (str/join ", " (map str auto-phases))))
           (cell "warn" "never"))
         (if high-stakes?
           (cell "critical" "ALWAYS human approval &middot; real-world act")
           (cell "muted" "-")))))

(defn- rule-row
  "One row per HARD governor rule. The rule name and the op it guards
  are read from the run; the `demonstrated by` column is derived from
  the holds this scenario actually produced, so a rule that stopped
  firing would show up here as an empty cell rather than a stale claim."
  [held-by [rule description]]
  (let [subjects (get held-by rule)]
    (row (code rule)
         (esc description)
         (if (seq subjects)
           (cell "critical" (str/join ", " (map #(code %) (sort subjects))))
           (cell "muted" "not exercised in this run")))))

(def ^:private hard-rules
  "The governor's five HARD rules, in the priority order
  `memberorg.governor/check` composes them. Descriptions summarise
  each check's own docstring -- documentation of fixed behaviour, not
  runtime telemetry. Whether each one FIRED is derived from the run."
  [[:no-spec-basis
    "公式 spec-basis の引用が無い提案は法域要件として扱えない -- never invent a jurisdiction's tax-exempt-status / political-activity requirements."]
   [:evidence-incomplete
    "公表前に法域の必要書類(会員合意/税務上の地位審査/運営機関承認/公表通知)が充足していること。Advisor の自己申告 confidence は根拠にしない。"]
   [:tax-exempt-status-risk-unresolved
    "税務上の地位への影響が未解決なら公表しない。提案自身の検出でも、記録済みの事実でも同じく HARD hold。"]
   [:position-review-overdue
    "position 自身の days-since-last-review が自身の max-review-interval-days を超過していないかを独立に再計算する。"]
   [:already-published
    "同一 position の二重公表を拒否する。専用の :published? 事実で判定し、:status 値には依存しない。"]])

(defn- approver-rows
  "For every human approval this run granted, show what the SSoT
  actually retained. The retention verdict is DERIVED -- each register
  is read back through the Store protocol and inspected for an
  approver key -- so if the store is later changed to keep approver
  attribution, this table self-corrects with no edit here."
  [db approvals]
  (let [approver-key? (fn [k] (str/includes? (str/lower-case (kw-name k)) "approv"))
        retained
        (fn [{:keys [op subject]}]
          (case op
            :position/verify
            (some->> (store/verify-of db subject) (filter (comp approver-key? key)) first val)

            :taxstatus/screen
            (some->> (store/taxstatus-screen-of db subject) (filter (comp approver-key? key)) first val)

            :actuation/publish-position
            (some->> (store/publication-history db)
                     (filter #(= subject (get % "position_id")))
                     first
                     (filter (comp approver-key? key)) first val)

            :member/intake
            (some->> (store/position db subject) (filter (comp approver-key? key)) first val)

            nil))]
    (mapv (fn [{:keys [op subject by] :as a}]
            (let [kept (retained a)]
              (row (code op)
                   (code subject)
                   (esc by)
                   (if kept
                     (cell "ok" (str "retained &middot; " (code kept)))
                     (cell "warn" "audit only &mdash; not retained in record")))))
          approvals)))

(defn- jurisdiction-row [[iso3 {:keys [name owner-authority legal-basis provenance required-evidence]}]]
  (row (code iso3)
       (esc name)
       (esc owner-authority)
       (esc legal-basis)
       (str "<span class=\"num\">" (count required-evidence) "</span>")
       (str "<a href=\"" (esc provenance) "\">" (esc provenance) "</a>")))

(defn- publication-row [r]
  (row (code (get r "record_id"))
       (code (get r "position_id"))
       (esc (get r "jurisdiction"))
       (esc (get r "kind"))
       (if (get r "immutable") (cell "ok" "immutable") (cell "warn" "mutable"))))

;; ----------------------------- document -----------------------------

(defn render
  "Renders the operator console from a store `db` that has already run
  `run-demo!` (or any other real scenario) plus that run's approvals."
  [{:keys [db approvals]}]
  (let [ledger (vec (store/ledger db))
        positions (store/all-positions db)
        hard-holds (holds ledger)
        held-by (reduce (fn [m {:keys [subject violations]}]
                          (update m (-> violations first :rule) (fnil conj #{}) subject))
                        {} hard-holds)
        cov (facts/coverage)
        publications (store/publication-history db)]
    (str
     "<!DOCTYPE html>\n"
     "<html lang=\"ja\">\n<head><meta charset=\"utf-8\">"
     "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, viewport-fit=cover\">"
     "<meta name=\"color-scheme\" content=\"light\">"
     "<title>cloud-itonami-isic-9499 &middot; other membership organizations &mdash; Operator Console</title>"
     "<style>" (jp-go-dds.skin/dds+skin) "</style></head>\n<body>\n"
     "<header class=\"bar\">\n"
     "  <h1>Activities of other membership organizations n.e.c. (ISIC 9499) &mdash; Operator Console</h1>\n"
     "  <p class=\"subtitle\">read-only sample &middot; governor-gated &middot; position publication is ALWAYS human-approved</p>\n"
     "</header>\n"
     "<main>\n"

     (section
      "Positions"
      (str "Live snapshot of the position directory, generated at build time from "
           (code "memberorg.store") " by " (code "memberorg.render-html")
           " (" (code "clojure -M:dev:render-html") "). "
           "Review recency is shown as days-since-last-review / max-review-interval-days, "
           "the two fields " (code "memberorg.registry/position-review-overdue?")
           " independently recomputes.")
      (table ["Position" "Name" "Jurisdiction" "Review recency" "Tax-exempt status risk"
              "Publication" "Last op"]
             (map (partial position-row ledger) positions)))

     (section
      (str "HARD holds this run (" (count hard-holds) ")")
      (str "Every hold below is un-overridable: a human approver never sees these proposals at all. "
           "Rows are read directly off the " (code ":governor-hold")
           " facts in the append-only ledger.")
      (table ["Rule" "Op" "Position" "Detail" "Confidence"]
             (map hold-row hard-holds)))

     (section
      "Governor HARD rules"
      (str "All five checks in " (code "memberorg.governor/check")
           " are HARD: a human approver CANNOT override them. "
           "The right-hand column is derived from the holds this scenario actually produced.")
      (table ["Rule" "What it enforces" "Demonstrated by"]
             (map (partial rule-row held-by) hard-rules)))

     (section
      "Op gate (rollout phase &times; governor stakes)"
      (str "Derived at render time from " (code "memberorg.phase/phases") " and "
           (code "memberorg.governor/high-stakes") ". "
           (code ":actuation/publish-position") " is deliberately absent from every phase's "
           (code ":auto") " set AND is high-stakes in the governor &mdash; two independent layers "
           "agree that publishing a real public position is always a human call.")
      (table ["Op" "Writable in phases" "Auto-commit" "Governor stakes"]
             (map gate-row (sort-by str phase/write-ops))))

     (section
      (str "Human approvals this run (" (count approvals) ")")
      (str "Who approved what, and whether the SSoT actually kept that attribution. "
           "The verdict is derived by reading each register back through the Store protocol "
           "and looking for an approver key &mdash; so this table self-corrects if the store "
           "is changed. Where it reads &ldquo;audit only&rdquo;, the approver is present in the "
           "run's " (code ":approval-granted") " audit fact but the committed record does not "
           "carry it: " (code "store/commit-record!") "'s " (code ":position/mark-published")
           " branch rebuilds the record from " (code "memberorg.registry")
           " and discards the proposal payload.")
      (table ["Op" "Position" "Approved by" "Retained in SSoT?"]
             (approver-rows db approvals)))

     (section
      (str "Position-publication records (" (count publications) ")")
      (str "The append-only book-of-record drafts produced by "
           (code "memberorg.registry/register-position-publication") ". "
           "Every certificate this actor produces is UNSIGNED &mdash; signature is the "
           "organization operator's own act, not this actor's.")
      (table ["Record" "Position" "Jurisdiction" "Kind" "Immutability"]
             (map publication-row publications)))

     (section
      "Jurisdiction spec-basis catalog"
      (str "The G2-style citation table " (code "memberorg.facts")
           " checks every verification proposal against. "
           (esc (:note cov))
           " A jurisdiction absent from this table has NO spec-basis, full stop &mdash; "
           "which is exactly why " (code "position-2") " (ATL) HARD-holds above.")
      (table ["ISO3" "Jurisdiction" "Owner authority" "Legal basis" "Required evidence" "Provenance"]
             (map jurisdiction-row (sort-by key facts/catalog))))

     (section
      (str "Audit ledger (" (count ledger) " facts)")
      (str "The append-only decision log &mdash; every commit and every hold this scenario "
           "produced, in order. This is the evidence the organization needs if a publication "
           "is later disputed or its tax-exempt status challenged.")
      (table ["Fact" "Op" "Position" "Basis" "Summary"]
             (map ledger-row ledger)))

     "</main>\n"
     "<footer class=\"footer\">\n"
     "  <p>Generated by <code>memberorg.render-html</code> from a live "
     "<code>memberorg.operation</code> actor run &mdash; no mock data, no hand-written rows. "
     "Regenerate with <code>clojure -M:dev:render-html</code>.</p>\n"
     "</footer>\n"
     "</body></html>\n")))

(defn -main [& args]
  (let [out (or (first args) "docs/samples/operator-console.html")
        {:keys [db approvals] :as result} (run-demo!)
        ledger (vec (store/ledger db))
        hard-holds (holds ledger)]
    ;; Build-time invariant: a console that shows no HARD hold would be
    ;; evidence-free -- the whole point of this page is that the governor
    ;; refuses things a human cannot override. Fail the build rather than
    ;; publish a page that silently lost its holds.
    (when (zero? (count hard-holds))
      (throw (ex-info "render-html: the scenario produced 0 :governor-hold records -- refusing to write a console with no HARD holds"
                      {:ledger-facts (count ledger)
                       :ops (mapv :op ledger)})))
    (let [rules-fired (into (sorted-set) (map #(-> % :violations first :rule) hard-holds))
          declared (into (sorted-set) (map first hard-rules))]
      (when-not (= rules-fired declared)
        (throw (ex-info "render-html: the scenario did not exercise every declared HARD rule"
                        {:fired rules-fired :declared declared
                         :missing (remove rules-fired declared)})))
      (let [dir (.getParentFile (java.io.File. ^String out))]
        (when dir (.mkdirs dir)))
      (spit out (render result) :encoding "UTF-8")
      (println "wrote" out
               (str "(" (count ledger) " ledger facts, "
                    (count hard-holds) " HARD holds covering "
                    (count rules-fired) " distinct rules, "
                    (count approvals) " human approvals, "
                    (count (store/publication-history db)) " publication records)")))))
