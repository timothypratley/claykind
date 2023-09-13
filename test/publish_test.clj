(ns publish-test
  (:require [clojure.test :refer :all]
            [scicloj.clay.publish :as publish]))

(deftest -main-test
  ;; This test runs the "publish" example to make sure it succeeds.
  ;; Files will be generated as a side effect.
  ;; You can conveniently use the "run last test" action as a shortcut to generate files.
  (publish/-main))
