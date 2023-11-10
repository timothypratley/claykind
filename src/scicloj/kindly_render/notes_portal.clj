(ns scicloj.kindly-render.notes-portal
  (:require [scicloj.kindly-render.value-hiccup :as value-hiccup]
            [scicloj.kindly-render.notes-html :as notes-html]))

(defn expr-result [{:keys [code] :as context} options]
  ;; TODO: maybe error/stdout to show
  (if (contains? context :value)
    [:div
     ;; code
     [:pre [:code code]]
     ;; value
     (value-hiccup/portal context options)]
    [:div (:code context)]))

(defn notes-to-html-portal [{:keys [contexts]} options]
  (-> (mapv #(expr-result % options) contexts)
      (notes-html/page options)))
