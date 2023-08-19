(ns scicloj.claykind.read-test
  (:require [clojure.test :refer :all])
  (:require [scicloj.claykind.read :as read]))

(deftest safe-eval-notes-test
  (->> (slurp "notebooks/test/basic.clj")
       (read/safe-eval-notes)
       (= '({:code         "(ns test.basic)"
             :form         (ns
                             test.basic)
             :kind         :kindly/value
             :kindly/value nil}
            {:code         "(+ 1 2)"
             :form         (+
                             1
                             2)
             :kind         :kindly/value
             :kindly/value 3}
            {:kind           :kindly/comment
             :kindly/comment "# section 1


 asss
"}
            {:code         "(defn f [x]
  (+ x ; hmm
     ;; hmmm
     9))"
             :form         (defn
                             f
                             [x]
                             (+
                               x
                               9))
             :kind         :kindly/value
             :kindly/value #'test.basic/f}
            {:code         "(f 20)"
             :form         (f
                             20)
             :kind         :kindly/value
             :kindly/value 29}
            {:kind           :kindly/comment
             :kindly/comment "# section 2

       ad

a
"}))
       (is)))
