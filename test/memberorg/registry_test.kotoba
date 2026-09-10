(ns memberorg.registry-test
  (:require [clojure.test :refer [deftest is]]
            [memberorg.registry :as r]))

;; ----------------------------- position-review-overdue? -----------------------------

(deftest not-overdue-when-within-interval
  (is (not (r/position-review-overdue?
            {:days-since-last-review 30 :max-review-interval-days 90})))
  (is (not (r/position-review-overdue?
            {:days-since-last-review 90 :max-review-interval-days 90}))))

(deftest overdue-when-past-interval
  (is (r/position-review-overdue?
       {:days-since-last-review 120 :max-review-interval-days 90})))

(deftest missing-fields-are-not-treated-as-overdue
  (is (not (r/position-review-overdue? {})))
  (is (not (r/position-review-overdue? {:days-since-last-review 120}))))

;; ----------------------------- register-position-publication -----------------------------

(deftest publication-is-a-draft-not-a-real-publication
  (let [result (r/register-position-publication "position-1" "JPN" 0)]
    (is (nil? (get-in result ["certificate" "proof"])))
    (is (= (get-in result ["certificate" "issued_by_registry"]) false))
    (is (= (get-in result ["certificate" "status"]) "draft-unsigned"))))

(deftest publication-assigns-publication-number
  (let [result (r/register-position-publication "position-1" "JPN" 7)]
    (is (= (get result "publication_number") "JPN-POS-000007"))
    (is (= (get-in result ["record" "position_id"]) "position-1"))
    (is (= (get-in result ["record" "kind"]) "position-publication-draft"))
    (is (= (get-in result ["record" "immutable"]) true))))

(deftest publication-validation-rules
  (is (thrown? Exception (r/register-position-publication "" "JPN" 0)))
  (is (thrown? Exception (r/register-position-publication "position-1" "" 0)))
  (is (thrown? Exception (r/register-position-publication "position-1" "JPN" -1))))

(deftest history-is-append-only
  (let [c1 (r/register-position-publication "position-1" "JPN" 0)
        hist (r/append [] c1)
        c2 (r/register-position-publication "position-2" "JPN" 1)
        hist2 (r/append hist c2)]
    (is (= 2 (count hist2)))
    (is (= "JPN-POS-000000" (get-in hist2 [0 "record_id"])))
    (is (= "JPN-POS-000001" (get-in hist2 [1 "record_id"])))))
