(ns scicloj.claykind.read
  "Convert code into contexts.
  Contexts are maps that contain top-level forms and their evaluated value,
  which will be further annotated with more information."
  (:require [clojure.tools.reader]
            [clojure.tools.reader.reader-types]
            [clojure.string :as str]
            [rewrite-clj.parser :as parser]
            [rewrite-clj.node :as node]
            [sci.core :as sci]))

(defn ^:dynamic *on-eval-error*
  "By default, eval errors will be rethrown.
  Binding *on-eval-error* to nil will cause the error to pass through instead.
  *on-eval-error* may be bound to a function to provide alternative behavior like warning.
  When bound to a function the result will be ignored."
  [context ex]
  (throw (ex-info "Eval failed" context ex)))

(defn eval-node
  "Given an Abstract Syntax Tree node, returns a context.
  A context represents a top level form evaluation."
  [node options]
  (let [tag (node/tag node)
        code (node/string node)
        {:keys [babashka sci-ctx]} options]
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
        (try
          {:code  code
           :form  form
           :value (if babashka
                    (sci/eval-form form sci-ctx)
                    (eval form))}
          (catch Throwable ex
            (when *on-eval-error*
              (*on-eval-error* {:code code
                                :form form}
                               ex))
            {:code  code
             :form  form
             :error ex}))))))

(defn parse-form
  ([code] (parse-form code {}))
  ([code options]
   (-> (parser/parse-string code)
       (eval-node options))))

(defn parse-forms
  ([code] (parse-forms {}))
  ([code options]
   (->> (parser/parse-string-all code)
        (:children)
        (map #(eval-node % options)))))
