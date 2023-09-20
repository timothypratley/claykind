(ns scicloj.read-kinds.notes
  "Notebooks are organized by removing whitespace, joining adjacent comment blocks,
  and converting forms into Kindly advice.
  Advice represents the original code plus aggregated information such as the evaluated value and kind."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [scicloj.kindly-advice.v1.api :as ka]
            [scicloj.read-kinds.read :as read])
  (:import (java.io File)))

(def clojure-file-ext-regex
  #"\.clj[cx]?$")

(defn clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-find clojure-file-ext-regex (.getName file)))))
(comment
  (re-find clojure-file-ext-regex "foo.clj") ".clj"
  (re-find clojure-file-ext-regex "baz.html") nil)

(defn join-comment-blocks [comment-blocks]
  {:kind           :kind/comment
   :kindly/comment (-> (map :kindly/comment comment-blocks)
                       (str/join))})

(def notebook-xform
  "Transducer to infer kinds, join comment blocks, and remove unnecessary whitespace."
  (comp
    ;; infer kinds
    (map (fn [context]
           (if (-> context :kind #{:kind/comment :kind/whitespace :kind/uneval})
             context
             (ka/advise context))))
    ;; join comment blocks -- whitespace and uneval still break them
    (partition-by (comp some? :kindly/comment))
    (mapcat (fn [part]
              (if (-> part first :kindly/comment)
                [(join-comment-blocks part)]
                part)))
    ;; remove uneval and whitespace
    (remove (comp #{:kind/uneval :kind/whitespace} :kind))))

(defn read-file-as-notes
  "Reads a clojure source file and returns contexts.
  See `read-all-notes` for more information."
  [^File file options]
  (into [] notebook-xform (read/read-file file options)))

(defn safe-read-notes
  "Like `read-notes` but stores errors in the advice."
  [^File file options]
  (binding [read/*on-eval-error* nil]
    (read-file-as-notes file options)))

;; This is a wrapper to handle invoking from bb command line
(defn babashka-safe-read-notes
  "Like `read-notes` but stores errors in the advice."
  [{:keys [filename options]}]
  (prn (safe-read-notes (io/file filename) options)))

(defn bb-read-notes
  [^File file options]
  (-> (sh/sh "bb"
             "-Sdeps" (pr-str '{:deps {claykind/claykind {#_#_:mvn/version ""
                                                          :local/root "."}}})
             "--exec" "scicloj.read-kinds.notes/babashka-safe-read-notes"
             (str :filename)
             (str file)
             (str :options)
             (pr-str options))
      ;; TODO: handle errors
      :out
      edn/read-string))

(defn read-notes
  "Read a single notebook.
  Notebooks are represented as `{:file f :contexts [...]}`,
  Where contexts are `{:code \"(...)\", :form '(...), :value ..., :kind ..., :advice (...)}`,
  representing the original form, evaluated value, and kindly kind.
  Contexts are suitable for passing to visualization tools,
  or Kindly plugins that talk to visualization tools."
  [^File file {:keys [evaluator] :as options}]
  (if (= evaluator :babashka)
    (bb-read-notes file options)
    (safe-read-notes file options)))

(comment
  (read-notes (io/file "notebooks/babashka/bb.clj")
              {:evaluator :babashka}))

(defn all-notes
  "Given options `{:dirs [\"dir1\" \"dir2\"]}`,
  finds and reads all notebooks in those paths.
  See details about notebooks in `read-notes`."
  [{:keys [dirs] :as options}]
  (for [dir dirs
        file (file-seq (io/file dir))
        :when (clojure-source? file)]
    {:file     file
     :contexts (read-notes file options)}))
