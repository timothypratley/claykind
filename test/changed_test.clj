(ns changed-test
  (:require [clojure.test :refer :all]
            [changed]))

(deftest -main-test
  ;; This test runs the "changed" example to make sure it succeeds.
  ;; Files will be generated as a side effect.
  ;; You can conveniently use the "run last test" action as a shortcut to generate files.
  (changed/-main))
