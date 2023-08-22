(ns scicloj.claykind.kinds
  "Kinds are advice for how to visualize values that will be passed to downstream tools."
  (:require [scicloj.kindly.v3.api :as kind]))

(comment
  (require 'scicloj.kindly-default.v1.api)
  (scicloj.kindly-default.v1.api/setup!)
  (kind/advice {:value [:ul
                          [:li "hello"]
                          [:li "there"]]})
  (def advices
    (kind/advice {:value ^{:kindly/kind :kind/hiccup} [:ul
                                                         [:li "hello"]
                                                         [:li "there"]]}))
  (require '[scicloj.kind-portal.v1.api :as kp])
  (kp/kindly-submit-context (first advices))

  )

(defn infer-kind
  "Provides advice for a context.
  Advice is a Kindly representation of a value."
  [context]
  (let [advices (kind/advice context)]
    ;; TODO: figure out a priority system
    (first advices)))
