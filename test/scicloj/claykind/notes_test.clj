(ns scicloj.claykind.notes-test
  (:require [clojure.test :refer :all])
  (:require [scicloj.claykind.notes :as notes]))

(deftest safe-eval-notes-test
  (->> (slurp "notebooks/test/basic.clj")
       (notes/safe-eval-notes)
       (= '({:code         "(ns test.basic)"
             :form         (ns
                             test.basic)
             :value        nil
             :kind         :kindly/value
             :kindly/value nil}
            {:code         "(+ 1 2)"
             :form         (+
                             1
                             2)
             :value        3
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
             :value        #'test.basic/f
             :kind         :kindly/value
             :kindly/value "#'test.basic/f"}
            {:code         "(f 20)"
             :form         (f
                             20)
             :value        29
             :kind         :kindly/value
             :kindly/value 29}
            {:kind           :kindly/comment
             :kindly/comment "# section 2

       ad

a
"}))
       (is)))
