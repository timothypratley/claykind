(ns scicloj.clay-builders.html-portal
  (:require [scicloj.kind-adapters.to-portal :as to-portal]
            [scicloj.clay-builders.html_reagent :as cre]))

(defn expr-result [{:keys [code] :as context}]
  ;; TODO: kpi/prepare should handle missing value
  (if (contains? context :value)
    [:div
     ;; code
     [:pre [:code code]]
     ;; value
     (to-portal/adapt context)]
    ;; TODO: maybe error
    [:div (:code context)]))

(defn notes-to-html-portal [{:keys [contexts]} options]
  (->> (mapv expr-result contexts)
       (cre/page)))
