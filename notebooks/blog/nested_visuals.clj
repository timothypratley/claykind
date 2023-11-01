;; # Nested Visuals
(ns blog.nested-visuals
  "Wherein we nest Kindly visualizations for fun and profit"
  (:require [scicloj.kindly.v4.kind :as kind]))

;; Intro

(def x 1)

(def my-svg
  (kind/hiccup
    [:svg {}
     [:circle {:cx 50
               :cy 50
               :r  50}]]))

(kind/portal [my-svg])


;; Visualization requests

(kind/table
  {:column-names ["project" "features"]
   :row-vectors  [[]
                  []]})

^:kind/hiccup
(def a
  [:svg {}
   [:circle {:cx 50
             :cy 50
             :r 20}]])
