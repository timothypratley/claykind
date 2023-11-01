(ns scicloj.kindly-render.notes-html
  (:require [hiccup.page :as page]
            [scicloj.kindly-render.value-hiccup :as value-hiccup]))

(defn expr-result [{:keys [code] :as context} options]
  ;; TODO: handle errors (in adapter???)
  [:div
   ;; code
   [:pre [:code code]]
   ;; value
   (value-hiccup/adapt context options)])

(defn page [elements]
  (page/html5
    [:head
     (page/include-css "style.css")
     (apply page/include-js (value-hiccup/include-js))]
    (into [:body] elements)))

;; TODO: ways to control order... sort by metadata?
(defn notes-to-html
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (mapv #(expr-result % options) contexts)
       (page)))
