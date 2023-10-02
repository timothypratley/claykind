(ns scicloj.clay-builders.html-portal
  (:require [scicloj.kind-portal.v1.impl :as kpi]
            [scicloj.clay-builders.html_reagent :as cre]))

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

(defn portal-widget [value]
  [:div
   [:script
    [:hiccup/raw-html
     (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
  {\"mime\": \"x-application/edn\",
   \"text\": (() => " (pr-str (pr-str-with-meta value)) ")},
  document.currentScript.parentElement);")]]])

(defn expr-result [{:keys [code] :as context}]
  ;; TODO: kpi/prepare should handle missing value
  (if (contains? context :value)
    [:div
     ;; code
     [:pre [:code code]]
     ;; value
     (portal-widget (kpi/prepare context))]
    ;; TODO: maybe error
    [:div (:code context)]))

(defn notes-to-html-portal [{:keys [contexts]} options]
  (->> (mapv expr-result contexts)
       (cre/page)))
