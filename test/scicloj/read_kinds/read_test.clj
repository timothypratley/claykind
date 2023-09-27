(ns scicloj.read-kinds.read-test
  (:require [clojure.test :refer :all]
            [scicloj.read-kinds.read :as read]))

(deftest parse-form-test
  (->> (read/read-string "(+ 1 2)")
       (= {:code         "(+ 1 2)"
           :form         '(+ 1 2)
           :value        3})
       (is)))
