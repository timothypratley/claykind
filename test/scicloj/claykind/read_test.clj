(ns scicloj.claykind.read-test
  (:require [clojure.test :refer :all]
            [scicloj.claykind.read :as read]))

(deftest parse-form-test
  (->> (read/parse-form "(+ 1 2)")
       (= {:code         "(+ 1 2)"
           :form         '(+ 1 2)
           :value        3})
       (is)))
