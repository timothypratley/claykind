(ns scicloj.claykind.notes-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [scicloj.read-kinds.notes :as notes]))

(deftest safe-eval-notes-test
  (->> (notes/safe-read-notes (io/file "notebooks/test/basic.clj"))
       (is)))
