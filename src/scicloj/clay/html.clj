(ns scicloj.clay.html
  (:require [hiccup2.core :as hiccup2]
            [scicloj.kind-adapters.hiccup :as ahiccup]))

(defn expr-result [{:keys [code advice]}]
  [:div
   ;; code
   [:pre [:code (pr-str code)]]
   ;; value
   advice])

(defn page [nodes]
  [:html [:body nodes]])

;; TODO: ways to control order... sort by metadata?
(defn notes-to-html
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (map ahiccup/adapt contexts)
       (map expr-result)
       (page)
       (hiccup2/html)))
