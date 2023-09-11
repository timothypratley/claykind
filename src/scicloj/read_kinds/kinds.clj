(ns scicloj.read-kinds.kinds
  "Kinds are advice for how to visualize values that will be passed to downstream tools.
  If no kinds have been configured, uses kindly-default."
  (:require [scicloj.kindly.v3.api :as kind]
            [scicloj.kindly-default.v1.api :as kd]))

;; If users forget to set up advisors, let's use kindly-default
(when (empty? @kind/*advisors)
  (kd/setup!))

(defn infer-kind
  "Provides advice for a context.
  Advice is a Kindly representation of a value."
  [context]
  (let [advices (kind/advice context)]
    ;; TODO: figure out a priority system
    (first advices)))
