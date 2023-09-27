(ns scicloj.read-kinds.api-test
  (:require [clojure.test :refer :all]
            [scicloj.read-kinds.api :as read-kinds]))

(deftest notebook-test
  (is (read-kinds/notebook "notebooks/test/basic.clj")))
