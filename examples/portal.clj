(ns portal
  "This example demonstrates sending a notebook to Portal"
  (:require [scicloj.claykind.notes :as notes]
            [scicloj.kind-portal.v1.api :as kp]
            [scicloj.kindly.v3.api :as kind]
            [scicloj.kindly-default.v1.api :as kindly]
            [portal.api :as p]))

(kindly/setup!)
(kp/open-if-needed)

;; TODO: support plugins
(comment
  (def p (p/open {:launcher :vs-code}))
  (def p (p/open {:launcher :intellij})))

(add-tap #'p/submit)
(tap> :hello)
(p/clear)

(def basic-notebook
  (-> (slurp "notebooks/test/basic.clj")
      (notes/safe-eval-notes)))

(kp/kindly-submit-context (second basic-notebook))

(run! kp/kindly-submit-context basic-notebook)
(kp/kindly-submit-context (first (kind/advice {:value basic-notebook})))
(tap> basic-notebook)

(def md
  ^{:kindly/kind :kind/md}
  ["# Header"
   "some text"])
(kp/kindly-submit-context (first (kind/advice {:value md})))
