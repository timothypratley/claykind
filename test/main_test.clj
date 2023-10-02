(ns main-test
  (:require [clojure.test :refer :all]
            [scicloj.claykind.main :as main]))

(deftest -main-test
  ;; This test runs the "publish" example to make sure it succeeds.
  ;; Files will be generated as a side effect.
  ;; You can conveniently use the "run last test" action as a shortcut to generate files.
  (main/-main))
