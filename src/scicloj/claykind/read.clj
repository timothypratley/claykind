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

(def evaluators #{:clojure :sci})

(defn ^:dynamic *on-eval-error*
  "By default, eval errors will be rethrown.
  Binding *on-eval-error* to nil will cause the error to pass through instead.
  *on-eval-error* may be bound to a function to provide alternative behavior like warning.
  When bound to a function the result will be ignored."
  [context ex]
  (throw (ex-info (str "Eval failed: " (ex-message ex))
                  context
                  ex)))

(def eval-ctx (atom (sci/init {})))

(defn eval-node
  "Given an Abstract Syntax Tree node, returns a context.
  A context represents a top level form evaluation."
  [node options]
  (let [tag (node/tag node)
        code (node/string node)
        {:keys [evaluator]} options]
    (case tag
      (:newline :whitespace)
      {:code code
       :kind :kind/whitespace}

      ;; extract text from comments
      :comment
      {:code           code
       :kind           :kind/comment
       ;; remove leading semicolons and one non-newline space if present.
       ;; TODO: should remove #!, but maybe
       :kindly/comment (str/replace-first code #"^(;|#!)*[^\S\r\n]?" "")}

      ;; evaluate for value
      (let [form (node/sexpr node)]
        (try
          {:code  code
           :form  form
           :value (if (= evaluator :sci)
                    (swap! eval-ctx sci/eval-form form)
                    ;; TODO: should limit eval of Clojure to a context
                    (eval form))}
          (catch Throwable ex
            (when *on-eval-error*
              (*on-eval-error* {:code code
                                :form form}
                               ex))
            {:code  code
             :form  form
             :error ex}))))))

(defn validate-options [{:keys [evaluator]}]
  (when evaluator
    (assert (contains? evaluators evaluator)
            (str "evaluator must be one of: " evaluators))))

(defn parse-form
  ([code] (parse-form code {}))
  ([code options]
   (-> (parser/parse-string code)
       (eval-node options))))

(defn babashka? [node]
  (and (= (node/tag node) :comment)
       (str/starts-with? (node/string node) "/usr/bin/env bb")))

(defn detect-evaluator [options nodes]
  (if (:evaluator options)
    options
    (assoc options :evaluator
                   (if (some-> (first nodes) (babashka?))
                     (do
                       (println "Detected Sci")
                       :sci)
                     :clojure))))

(defn parse-forms
  ([code] (parse-forms code {}))
  ([code options]
   (validate-options options)
   (let [ast (parser/parse-string-all code)
         top-level-nodes (:children ast)
         options (detect-evaluator options top-level-nodes)]
     (map #(eval-node % options) top-level-nodes))))
