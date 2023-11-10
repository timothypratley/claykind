(ns scicloj.read-kinds.notes
  "Notebooks are organized by removing whitespace, joining adjacent comment blocks,
  and converting forms into Kindly advice.
  Advice represents the original code plus aggregated information such as the evaluated value and kind."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kindly-advice.v1.api :as ka]
            [scicloj.kindly-advice.v1.completion :as completion]
            [scicloj.read-kinds.read :as read])
  (:import (clojure.lang IDeref)
           (java.io File)))

(set! *warn-on-reflection* true)

(defn join-comment-blocks [comment-blocks]
  {:kind  :kind/md
   :value (-> (map :value comment-blocks)
              (str/join))})

(defn comment? [context]
  (= (:kind context) :kind/comment))

(defn derefing-advise
  "Kind priority is inside out: kinds on the value supersedes kinds on the ref."
  [context]
  (let [context (ka/advise context)
        {:keys [value]} context]
    (if (instance? clojure.lang.IDeref value)
      (let [v @value
            meta-kind (completion/meta-kind v)]
        (ka/advise (derefing-advise (cond-> (assoc context :value v)
                                         meta-kind (assoc context :meta-kind meta-kind)))))
      context)))

(defn top-level-advise
  "Vars are derefed only when a user annotated kind is present, otherwise they are left alone."
  [context]
  (let [{:keys [value]} context]
    (if (var? value)
      (let [v @value
            meta-kind (or (completion/meta-kind v)
                          (completion/meta-kind value))]
        (if meta-kind
          (ka/advise (derefing-advise (assoc context :value v
                                                  :meta-kind meta-kind)))
          (ka/advise context)))
      (derefing-advise context))))

(defn maybe-advise [context]
  (if (and (contains? context :value)
           (not (contains? context :kind)))
    (top-level-advise context)
    context))

(def notebook-xform
  "Transducer to infer kinds, join comment blocks, and remove unnecessary whitespace."
  (comp
    ;; infer kinds
    (map maybe-advise)
    ;; join comment blocks -- whitespace and uneval still break them
    (partition-by comment?)
    (mapcat (fn [part]
              (if (comment? (first part))
                [(join-comment-blocks part)]
                part)))
    ;; remove uneval and whitespace
    (remove (comp #{:kind/hidden :kind/uneval :kind/whitespace} :kind))))

(defn read-file-as-notes
  "Reads a clojure source file and returns contexts."
  [^File file options]
  (into [] notebook-xform (read/read-file file options)))

(defn relative-path [^File file]
  (-> (str (.relativize (.toURI (io/file ""))
                        (.toURI (io/file file))))
      (str/replace #"/$" "")))

(defn pr-ex [ex label]
  (println label (ex-message ex))
  (when-let [data (ex-data ex)]
    (pprint/pprint data))
  (when-let [cause (ex-cause ex)]
    (recur cause "CAUSE:")))

(defn safe-read-notes
  "Like `read-notes` but stores errors in the advice."
  [^File file {:keys [verbose] :as options}]
  (binding [*ns* (find-ns 'user)
            read/*on-eval-error* (when verbose
                                   (fn [context ex]
                                     (println (str "ERROR evaluating " (relative-path file) ":" (:line context)))
                                     (println (:code context))
                                     (pr-ex ex "EXCEPTION:")))]
    (read-file-as-notes file options)))

(defn crystalize
  "Unwraps IDerefs, which cannot be serialized."
  [x]
  (cond (instance? IDeref x) (recur @x)
        (map-entry? x) [(crystalize (key x)) (crystalize (val x))]
        (coll? x) (into (empty x) (map crystalize x))
        :else x))

(defn babashka-safe-read-notes
  "This is a wrapper to handle invoking from bb command line.
  Like `read-notes` but stores errors in the advice."
  [{:keys [filename options]}]
  (prn (mapv #(cond
                (= (:kind %) :kind/var) (update % :value str)
                (contains? % :value) (update % :value crystalize)
                :else %)
             (safe-read-notes (io/file filename) options))))

;; TODO: we probably don't really need this, users can just invoke claykind from bb instead
(defn bb-read-notes
  [^File file options]
  (let [{:keys [out err]}
        (sh/sh "bb"
               "-Sdeps" (pr-str '{:deps {org.scicloj/claykind {#_#_:mvn/version ""
                                                               :local/root ".."}}})
               "--exec" "scicloj.read-kinds.notes/babashka-safe-read-notes"
               ":filename" (str file)
               ":options" (pr-str options))]
    (when err
      (println err))
    (when out
      (try
        (edn/read-string out)
        (catch Exception ex
          (println out)
          (throw (ex-info (str "Failed to read babashka contexts for " file)
                          {:id ::failed-to-read-contexts
                           :out out}
                          ex)))))))

(defn read-notes
  "Read a single notebook.
  Notebooks are represented as `{:file f :contexts [...]}`,
  Where contexts are `{:code \"(...)\", :form '(...), :value ..., :kind ..., :advice (...)}`,
  representing the original form, evaluated value, and kindly kind.
  Contexts are suitable for passing to visualization tools,
  or Kindly plugins that talk to visualization tools."
  [^File file {:keys [evaluator] :as options}]
  {:file     file
   :contexts (if (= evaluator :babashka)
               (bb-read-notes file options)
               (safe-read-notes file options))})

(defn read-string-as-context
  "Reads a form and returns a context."
  [code options]
  (maybe-advise (read/read-string code options)))

(comment
  (read-string-as-context "(+ 1 2)" {})
  (read-notes (io/file "notebooks/babashka/bb.clj")
              {:evaluator :babashka}))
