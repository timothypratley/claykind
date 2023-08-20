(ns portal
  (:require [scicloj.claykind.notes :as notes]
            [portal.api :as p]))

(def p (p/open)) ; Open a new inspector

;; or with an extension installed, do:
(comment
  (def p (p/open {:launcher :vs-code}))
  (def p (p/open {:launcher :intellij})))

(add-tap #'p/submit) ; Add portal as a tap> target

(tap> :hello) ; Start tapping out values

(p/clear)

(->> (slurp "notebooks/test/basic.clj")
     (notes/safe-eval-notes)
     (map tap>))
