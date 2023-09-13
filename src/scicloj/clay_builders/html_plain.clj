(ns scicloj.clay-builders.html-plain
  (:require [hiccup2.core :as hiccup2]
            [scicloj.kind-adapters.hiccup :as ahiccup]))

(defn expr-result [{:keys [code] :as context}]
  ;; TODO: handle error
  (if-let [c (:kindly/comment context)]
    [:div c]
    [:div
     ;; code
     [:pre [:code (pr-str code)]]
     ;; value
     (ahiccup/adapt context)]))

(defn page [nodes]
  (hiccup2/html
    [:html (into [:body]  nodes)]))

;; TODO: ways to control order... sort by metadata?
(defn notes-to-html
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (mapv expr-result contexts)
       (page)))
