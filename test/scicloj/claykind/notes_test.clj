(ns scicloj.claykind.notes-test
  (:require [clojure.test :refer :all]
            [scicloj.claykind.notes :as notes]))

(deftest safe-eval-notes-test
  (->> (slurp "notebooks/test/basic.clj")
       (notes/safe-eval-notes)
       (is)))
