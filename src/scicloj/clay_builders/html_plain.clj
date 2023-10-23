(ns scicloj.clay-builders.html-plain
  (:require [scicloj.clay-builders.html_reagent :as cre]
            [scicloj.kind-hiccup.to-hiccup :as ahiccup]))

(defn expr-result [{:keys [code] :as context}]
  ;; TODO: handle errors (in adapter???)
  [:div
   ;; code
   [:pre [:code code]]
   ;; value
   (ahiccup/adapt context)])

;; using reagent for some cases still
#_
(defn page [nodes]
  (hiccup2/html
    [:html (into [:body] nodes)]))

;; TODO: ways to control order... sort by metadata?
(defn notes-to-html
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (mapv expr-result contexts)
       (cre/page)))
