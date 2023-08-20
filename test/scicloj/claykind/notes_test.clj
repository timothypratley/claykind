(ns scicloj.claykind.notes-test
  (:require [clojure.test :refer :all])
  (:require [scicloj.claykind.notes :as notes]))

(deftest safe-eval-notes-test
  (->> (slurp "notebooks/test/basic.clj")
       (notes/safe-eval-notes)
       (= [{:code         "(ns test.basic)"
            :form         '(ns test.basic)
            :kind         :kindly/value
            :kindly/value nil
            :value        nil}
           {:kind           :kindly/comment
            :kindly/comment "# section 1

 hello, welcome to my wonderful test notebook
"}
           {:code         "(+ 1 2 3)"
            :form         '(+ 1 2 3)
            :kind         :kindly/value
            :kindly/value 6
            :value        6}
           {:code         "(defn f [x]
  (+ x                                                      ; let's do some addition
     ;; I like addition
     9))"
            :form         '(defn f [x] (+ x 9))
            :kind         :kindly/value
            :kindly/value "#'test.basic/f"
            :value        #'test.basic/f}
           {:code         "(f 20)"
            :form         '(f 20)
            :kind         :kindly/value
            :kindly/value 29
            :value        29}
           {:kind           :kindly/comment
            :kindly/comment "# section 2
"}
           {:kind           :kindly/comment
            :kindly/comment "What if I told you

    That codeblocks can exist inside comments?

And that tables are tricky
"}
           {:code         "^:kindly/table
{:rows [[\"a\" \"b\" \"c\"]]}"
            :form         {:rows [["a"
                                   "b"
                                   "c"]]}
            :kind         :kindly/value
            :kindly/value {:rows [["a"
                                   "b"
                                   "c"]]}
            :value        {:rows [["a"
                                   "b"
                                   "c"]]}}
           {:code         "^:kindly/table
{:headers  [:a :b]
 :row-maps [{:a \"a\" :b \"b\"}]}"
            :form         {:headers  [:a
                                      :b]
                           :row-maps [{:a "a"
                                       :b "b"}]}
            :kind         :kindly/value
            :kindly/value {:headers  [:a
                                      :b]
                           :row-maps [{:a "a"
                                       :b "b"}]}
            :value        {:headers  [:a
                                      :b]
                           :row-maps [{:a "a"
                                       :b "b"}]}}
           {:code         "^:kindly/table-matrix
[[\"a\" \"b\" \"c\"]]"
            :form         [["a"
                            "b"
                            "c"]]
            :kind         :kindly/value
            :kindly/value [["a"
                            "b"
                            "c"]]
            :value        [["a"
                            "b"
                            "c"]]}
           {:kind           :kindly/comment
            :kindly/comment "We can add things that translate to the existing kindly specs...
But! Now I have this different thing; SVG images, tables that have double rows.
Pushing handling code out the display tools
The categories of features
"}
           {:kind           :kindly/comment
            :kindly/comment "Notice that there is only whitespace between this comment and the previous one,
and that the whitespace was preserved in the markdown. Wonderful!
"}
           {:kind           :kindly/comment
            :kindly/comment "Possible feature: Order of evaluation
present the last form first!
Just reverse the contexts.
Might want to annotate this in the namespace itself somehow,
perhaps metadata on the `ns` form?
And even on individual parts? Bringing them to the top or bottom or setting their position.

Here is a markdown table

|  |  |  |
|--|--|--|
| \"a\" | \"b\" | \"c\" |
"}])
       (is)))
