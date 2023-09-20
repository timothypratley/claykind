#!/usr/bin/env bb

;; # Analysing git logs

;; ## Setup
(+ 1 2)

(require '[scicloj.kindly.v4.kind :as kind])

;; ## Data preparation

(str *ns*)
(-> "asaaajjjhsahah"
    (str/split #"a")
    kind/pprint)

#_(-> (clojure.java.shell/sh "git" "log")
      :out
      ;; (str/split #"\n")
      pr-str
      #_kind/pprint)
