(ns scicloj.clay.target.html
  (:require [hiccup2.core :as hiccup2]
            [scicloj.kind-adapters.hiccup :as ahiccup]))

(defn expr-result [{:keys [code] :as context}]
  [:div
   ;; code
   [:pre [:code (pr-str code)]]
   ;; value
   (ahiccup/adapt context)])

(defn page [nodes]
  (hiccup2/html
    [:html [:body nodes]]))

;; TODO: ways to control order... sort by metadata?
(defn notes-to-html
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (map expr-result contexts)
       (page)))
