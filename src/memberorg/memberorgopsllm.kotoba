(ns memberorg.memberorgopsllm
  "MemberOrgOps-LLM client -- the *contained intelligence node* for the
  other-membership-organization-governance actor.

  It normalizes member intake, drafts a per-jurisdiction tax-exempt-
  status/political-activity-restriction evidence checklist, screens
  positions for an unresolved tax-exempt-status risk, and drafts the
  position-publication finalization. CRITICAL: it is a smart-but-
  untrusted advisor. It returns a *proposal* (with a rationale + the
  fields it cited), never a committed record or a real public
  publication. Every output is censored downstream by `memberorg.
  governor` before anything touches the SSoT, and `:actuation/
  publish-position` proposals NEVER auto-commit at any phase -- see
  README `Actuation`.

  Like every sibling actor's advisor, this is a deterministic mock so
  the actor graph runs offline and the governor contract is exercised
  end-to-end. In production this calls a real LLM (kotoba-llm or
  equivalent) with the same proposal shape.

  Proposal shape (all kinds):
    {:summary    str            ; human-facing draft / finding
     :rationale  str            ; why -- SCANNED by the spec-basis gate
     :cites      [kw|str ..]    ; facts/sources the LLM used -- SCANNED too
     :effect     kw             ; how a commit would mutate the SSoT
     :stake      kw|nil         ; :actuation/publish-position | nil
     :confidence 0..1}"
  (:require #?(:clj  [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])
            [kotoba.lang.text :as str]
            [memberorg.facts :as facts]
            [memberorg.registry :as registry]
            [memberorg.store :as store]
            [langchain.model :as model]))

(defn- normalize-intake
  "Directory upsert -- the LLM only normalizes/validates the patch; it
  does not invent the position, tax-status-risk/review-recency status
  or jurisdiction. High confidence, low stakes."
  [_db {:keys [patch]}]
  {:summary    (str "会員記録更新: " (pr-str (keys patch)))
   :rationale  "入力 patch の正規化のみ。新規事実の生成なし。"
   :cites      (vec (keys patch))
   :effect     :position/upsert
   :value      patch
   :stake      nil
   :confidence 0.97})

(defn- verify-jurisdiction
  "Per-jurisdiction tax-exempt-status/political-activity-restriction
  evidence checklist draft. `:no-spec?` injects the failure mode we
  must defend against: proposing a checklist for a jurisdiction with
  NO official spec-basis in `memberorg.facts` -- the Membership
  Governance Governor must reject this (never invent a jurisdiction's
  requirements)."
  [db {:keys [subject no-spec?]}]
  (let [p (store/position db subject)
        iso3 (if no-spec? "ATL" (:jurisdiction p))
        sb (facts/spec-basis iso3)]
    (if (nil? sb)
      {:summary    (str iso3 " の公式spec-basisが見つかりません")
       :rationale  "memberorg.facts に未登録の法域。要件を推測で作らない。"
       :cites      []
       :effect     :verification/set
       :value      {:jurisdiction iso3 :checklist [] :spec-basis nil}
       :stake      nil
       :confidence 0.9}
      {:summary    (str iso3 " (" (:owner-authority sb) ") 向け必要書類 "
                        (count (:required-evidence sb)) " 件を提案")
       :rationale  (str "公式ソース: " (:provenance sb) " / 法的根拠: " (:legal-basis sb))
       :cites      [(:legal-basis sb) (:provenance sb)]
       :effect     :verification/set
       :value      {:jurisdiction iso3
                    :checklist (:required-evidence sb)
                    :spec-basis (:provenance sb)
                    :legal-basis (:legal-basis sb)}
       :stake      nil
       :confidence 0.9})))

(defn- screen-taxstatus
  "Tax-exempt-status-risk screening draft.
  `:tax-exempt-status-risk-unresolved?` on the position record injects
  the failure mode: the Membership Governance Governor must HOLD,
  un-overridably, on any unresolved risk."
  [db {:keys [subject]}]
  (let [p (store/position db subject)]
    (cond
      (nil? p)
      {:summary "対象の公表案件が見つかりません" :rationale "no position record"
       :cites [] :effect :taxstatus-screen/set :value {:position-id subject :verdict :unknown}
       :stake nil :confidence 0.0}

      (true? (:tax-exempt-status-risk-unresolved? p))
      {:summary    (str (:position-name p) ": 税務上の地位への影響リスクを検出")
       :rationale  "スクリーニングが税務上の地位への影響リスクを検出。人手確認とホールドが必須。"
       :cites      [:taxstatus-check]
       :effect     :taxstatus-screen/set
       :value      {:position-id subject :tax-status-risk-unresolved? true}
       :stake      nil
       :confidence 0.95}

      :else
      {:summary    (str (:position-name p) ": 税務上の地位への影響なしを確認")
       :rationale  "税務上の地位スクリーニング完了。"
       :cites      [:taxstatus-check]
       :effect     :taxstatus-screen/set
       :value      {:position-id subject :tax-status-risk-unresolved? false}
       :stake      nil
       :confidence 0.9})))

(defn- propose-position-publication
  "Draft the actual POSITION-PUBLICATION action -- publishing a real
  public position on the organization's behalf. ALWAYS `:stake
  :actuation/publish-position` -- this is a REAL-WORLD, reputation-
  and tax-status-affecting act, never a draft the actor may auto-run.
  See README `Actuation`: no phase ever adds this op to a phase's
  `:auto` set (`memberorg.phase`); the governor also always escalates
  on `:actuation/publish-position`. Two independent layers agree,
  deliberately."
  [db {:keys [subject]}]
  (let [p (store/position db subject)
        ready? (and p (not (:tax-exempt-status-risk-unresolved? p))
                   (not (registry/position-review-overdue? p)))]
    {:summary    (str subject " 向け公表提案"
                      (when p (str " (position=" (:position-name p) ")")))
     :rationale  (if p
                   (str "tax-exempt-status-risk-unresolved?=" (:tax-exempt-status-risk-unresolved? p)
                        " days-since-last-review=" (:days-since-last-review p)
                        " max-review-interval-days=" (:max-review-interval-days p))
                   "公表案件が見つかりません")
     :cites      (if p [subject] [])
     :effect     :position/mark-published
     :value      {:position-id subject}
     :stake      :actuation/publish-position
     :confidence (if ready? 0.9 0.3)}))

(defn infer
  "Route a request to the right proposal generator.
  request: {:op kw :subject id ...op-specific...}"
  [db {:keys [op] :as request}]
  (case op
    :member/intake                (normalize-intake db request)
    :position/verify                 (verify-jurisdiction db request)
    :taxstatus/screen                   (screen-taxstatus db request)
    :actuation/publish-position             (propose-position-publication db request)
    {:summary "未対応の操作" :rationale (str op) :cites []
     :effect :noop :stake nil :confidence 0.0}))

;; ----------------------------- Advisor protocol -----------------------------

(defprotocol Advisor
  (-advise [advisor store request] "store + request -> proposal map"))

(defn mock-advisor
  "The deterministic advisor (the `infer` logic above). Default everywhere."
  [] (reify Advisor (-advise [_ st req] (infer st req))))

(def ^:private system-prompt
  (str "あなたは会員制団体の公表エージェントの助言者です。"
       "与えられた事実のみに基づき、提案を1つだけEDNマップで返します。説明や前置きは"
       "一切書かず、EDNだけを出力します。\n"
       "キー: :summary(人向けドラフト) :rationale(根拠/必ず事実から) "
       ":cites(使った事実キーのベクタ) "
       ":effect(:position/upsert|:verification/set|:taxstatus-screen/set|"
       ":position/mark-published) "
       ":stake(:actuation/publish-position か nil) :confidence(0..1)。\n"
       "重要: 登録されていない法域の要件を絶対に創作してはいけません。"
       "spec-basisが無い場合は :cites を空にし confidence を上げないこと。"))

(defn- facts-for [st {:keys [op subject]}]
  (case op
    :position/verify              {:position (store/position st subject)}
    :taxstatus/screen               {:position (store/position st subject)}
    :actuation/publish-position       {:position (store/position st subject)}
    {:position (store/position st subject)}))

(defn- parse-proposal
  "Parse the model's EDN proposal defensively. Any parse/shape failure
  yields a safe low-confidence noop so the Membership Governance
  Governor escalates/holds -- an LLM hiccup can never auto-publish a
  position."
  [content]
  (let [p (try (edn/read-string (str/trim (str content)))
               (catch #?(:clj Exception :cljs :default) _ nil))]
    (if (map? p)
      (-> p
          (update :cites #(vec (or % [])))
          (update :confidence #(if (number? %) (double %) 0.0))
          (update :effect #(or % :noop)))
      {:summary "LLM応答を解釈できませんでした" :rationale (str content)
       :cites [] :effect :noop :stake nil :confidence 0.0})))

(defn llm-advisor
  "An advisor backed by a `langchain.model/ChatModel` (real inference)."
  ([chat-model] (llm-advisor chat-model {}))
  ([chat-model gen-opts]
   (reify Advisor
     (-advise [_ st req]
       (let [msgs [{:role :system :content system-prompt}
                   {:role :user :content (str "操作: " (:op req)
                                              "\n対象: " (:subject req)
                                              "\n事実: " (pr-str (facts-for st req)))}]
             resp (model/-generate chat-model msgs gen-opts)]
         (parse-proposal (:content resp)))))))

(defn trace
  "Decision-grounded audit record -- persisted to the :audit channel."
  [request proposal]
  {:t          :memberorgopsllm-proposal
   :op         (:op request)
   :subject    (:subject request)
   :summary    (:summary proposal)
   :rationale  (:rationale proposal)
   :cites      (:cites proposal)
   :confidence (:confidence proposal)})
