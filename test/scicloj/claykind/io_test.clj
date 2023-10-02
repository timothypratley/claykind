(ns scicloj.claykind.io-test
  (:require [clojure.test :refer :all]
            [scicloj.claykind.io :as clay.io]))

(deftest clojure-source?-test
  (is (= (re-find clay.io/clojure-file-ext-regex "foo.clj") ".clj"))
  (is (= (re-find clay.io/clojure-file-ext-regex "baz.html") nil)))
