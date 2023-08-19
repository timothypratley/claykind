(ns scicloj.claykind.read
  "Convert code into contexts.
  Contexts are maps that contain top-level forms and their evaluated value,
  which will be further annotated with more information."
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
       ;; remove leading semicolons and one non-newline space if present.
       :kindly/comment (str/replace-first code #"^;*[^\S\r\n]?" "")}

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
