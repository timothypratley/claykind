(ns scicloj.claykind.api-test
  (:require [clojure.test :refer :all]
            [scicloj.read-kinds.api :as read-kinds]))

(deftest notebook-test
  (is (read-kinds/notebook "notebooks/test/basic.clj")))

(deftest all-notebooks-test
  (is (seq (read-kinds/all-notebooks))))
