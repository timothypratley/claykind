(ns scicloj.read-kinds.read
  "Convert code into contexts.
  Contexts are maps that contain top-level forms and their evaluated value,
  which will be further annotated with more information."
  (:refer-clojure :exclude [read-string])
  (:require [clojure.java.io :as io]
            [clojure.tools.reader]
            [clojure.tools.reader.reader-types]
            [clojure.string :as str]
            [rewrite-clj.parser :as parser]
            [rewrite-clj.node :as node]
            [sci.core :as sci]
            [nrepl.core :as nrepl]
            [sci.impl.io :as sio]
            #_[babashka.main :as bb])
  (:import (clojure.lang LineNumberingPushbackReader)
           (java.io StringWriter)))

(def evaluators #{:clojure :sci :babashka})

(defn- validate-options [{:keys [evaluator]}]
  (when evaluator
    (assert (contains? evaluators evaluator)
            (str "evaluator must be one of: " evaluators))))

(defn ^:dynamic *on-eval-error*
  "By default, eval errors will be rethrown.
  Binding *on-eval-error* to nil will cause the error to pass through instead.
  *on-eval-error* may be bound to a function to provide alternative behavior like warning.
  When bound to a function the result will be ignored, but subsequent exceptions will propagate."
  [context ex]
  (throw (ex-info (str "Eval failed: " (ex-message ex))
                  context
                  ex)))

(defn- eval-node
  "Given an Abstract Syntax Tree node, returns a context.
  A context represents a top level form evaluation."
  [node options]
  (let [tag (node/tag node)
        code (node/string node)
        {:keys [evaluator ctx]} options]
    (case tag
      (:newline :whitespace)
      {:code code
       :kind :kind/whitespace}

      :uneval
      {:code code
       :kind :kind/uneval}

      ;; extract text from comments
      :comment
      {:code           code
       :kind           :kind/comment
       ;; remove leading semicolons or shebangs, and one non-newline space if present.
       :kindly/comment (str/replace-first code #"^(;|#!)*[^\S\r\n]?" "")}

      ;; evaluate for value, taking care to capture stderr/stdout and exceptions
      (let [form (node/sexpr node)
            out (new StringWriter)
            err (new StringWriter)
            context {:code code
                     :form form}]
        (merge context
               (try
                 {:value (if #_(= evaluator :sci) false
                           ;; TODO: binding out/err does not work, not sure why.
                           ;; maybe try System.setOut(myPrintStream) instead?
                           (sci/binding [sci/out out
                                         sci/err err]
                                        (sci/eval-form ctx form))
                           (binding [*out* out
                                     *err* err]
                             (eval form)))}
                 (catch Throwable ex
                   (when *on-eval-error*
                     (*on-eval-error* context ex))
                   {:exception ex}))
               (when (pos? (.length (.getBuffer out)))
                 {:out (str out)})
               (when (pos? (.length (.getBuffer err)))
                 {:err (str err)}))))))

(defn read-string
  "Parse and evaluate the first form in a string.
  Suitable for sending text representing one thing for visualization."
  ([code] (read-string code {}))
  ([code options]
   (-> (parser/parse-string code)
       (eval-node options))))

(defn- babashka? [node]
  (-> (node/string node)
      (str/starts-with? "#!/usr/bin/env bb")))

(defn- init-babashka []
  ;; TODO: babashka needs more complicated options, which I'm not sure how to replicate
  (sci/init {}))

(defn- eval-ast [ast options]
  "Given the root Abstract Syntax Tree node,
  returns a vector of contexts that represent evaluation"
  (let [top-level-nodes (node/children ast)
        b (some-> (first top-level-nodes) (babashka?))
        options (cond-> options b (assoc :evaluator :sci
                                         :ctx (init-babashka)))
        nodes (if b
                (rest top-level-nodes)
                top-level-nodes)]
    ;; must be eager to restore current bindings
    (mapv #(eval-node % options) nodes)))

(defn read-string-all
  "Parse and evaluate all forms in a string.
  Suitable for sending a selection of text for visualization.
  When reading a file, prefer using `read-file` to preserve the current ns bindings."
  ([code] (read-string-all code {}))
  ([code options]
   (validate-options options)
   (-> (parser/parse-string-all code)
       (eval-ast options))))

(defn read-file
  "Similar to `clojure.core/load-file`,
  but returns a representation of the forms and results of evaluation.
  Suitable for processing an entire namespace."
  ([file] (read-file file {}))
  ([file options]
   ;; preserve current ns bindings
   (binding [*ns* *ns*
             *warn-on-reflection* *warn-on-reflection*
             *unchecked-math* *unchecked-math*]
     (-> (parser/parse-file-all file)
         (eval-ast options)))))

(comment

  ;; this doesn't seem to work...
  (require '[babashka.main :as bb])
  (bb/-main)

  ;; prepl seems like a perfect thing to try but I'm not sure how to make it work
  ;; this hangs the REPL and nothing happens...
  (let [file "notebooks/babashka/bb.clj"
        in-reader (clojure.lang.LineNumberingPushbackReader. (io/reader file))
        out-fn (fn [x]
                 (prn "BB" x))]
    (clojure.core.server/prepl in-reader out-fn))

  (import (java.net Socket))
  (import (java.io DataInputStream DataOutputStream))

  (require '[nrepl.core :as nrepl])
  (with-open [conn (nrepl/connect :port 1667)]
    (-> (nrepl/client conn 1000)                            ; message receive timeout required
        (nrepl/message {:op "eval" :code "^:kind/hiccup [:div]"})

        ;;(nrepl/response-values)
        (nrepl/combine-responses)
        ))

  ;; we could make `read-kinds` babashka compatible (it probably already is!) and spit out the values.edn file
  ;; then use that for the notebook tooling

  )
