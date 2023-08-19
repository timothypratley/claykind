(ns scicloj.claykind.read
  (:require [clojure.tools.reader]
            [clojure.tools.reader.reader-types]
            [clojure.string :as str]
            [rewrite-clj.parser :as parser]
            [rewrite-clj.node :as node]))

(defn eval-node
  "Given an Abstract Syntax Tree node, returns a context.
  A context represents a top level form evaluation."
  [node]
  (let [tag (node/tag node)
        code (node/string node)]
    (case tag
      (:newline :whitespace)
      {:code code
       :kind :kindly/whitespace}

      ;; extract text from comments
      :comment
      {:code           code
       :kind           :kindly/comment
       :kindly/comment (str/replace code #"^;*\s?" "")}

      ;; evaluate for value
      (let [form (node/sexpr node)]
        ;; TODO: maybe infer kind or check for kind metadata
        {:code         code
         :form         form
         :value        (eval form)}))))

(defn parse-form [code]
  (-> (parser/parse-string code)
      (eval-node)))

(defn parse-forms [code]
  (->> (parser/parse-string-all code)
       (:children)
       (map eval-node)))
