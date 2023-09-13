(ns scicloj.read-kinds.notes
  "Notebooks are organized by removing whitespace, joining adjacent comment blocks,
  and converting forms into Kindly advice.
  Advice represents the original code plus aggregated information such as the evaluated value and kind."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [scicloj.kindly-advice.v1.api :as ka]
            [scicloj.read-kinds.read :as read])
  (:import (java.io File)))

(defn- join-comment-blocks [comment-blocks]
  {:kind    :kind/comment
   :kindly/comment (-> (map :kindly/comment comment-blocks)
                       (str/join))})

(def ^:private notebook-xform
  "Transducer to infer kinds, join comment blocks, and remove unnecessary whitespace."
  (comp
    (remove (comp #{:kind/uneval} :kind))
    ;; infer kinds
    (map (fn [context]
           (if (-> context :kind #{:kind/comment :kind/whitespace :kind/uneval})
             context
             (ka/advise context))))
    ;; join comment blocks
    (partition-by (comp some? :kindly/comment))
    (mapcat (fn [part]
              (if (-> part first :kindly/comment)
                [(join-comment-blocks part)]
                part)))
    ;; remove whitespace
    (remove (comp #{:kind/whitespace} :kind))))

(defn read-notes
  "Reads a clojure source file and returns contexts.
  See `read-all-notes` for more information."
  [^File file]
  (into [] notebook-xform (read/read-file file)))

(defn safe-read-notes
  "Like `read-notes` but stores errors in the advice."
  [^File file]
  (binding [read/*on-eval-error* nil]
    (read-notes file)))

(defn- clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-matches #".*\.clj[cx]?$" (.getName file)))))

(defn all-notes
  "Find and read all notebooks in a sequence of directory paths.
  Notebooks are represented as `{:file f :contexts [...]}`,
  Where contexts have the shape `{:code \"(...)\", :form '(...), :value ..., :kind ..., :advice (...)}`,
  representing the original form, evaluated value, and kindly kind.
  Contexts are suitable for passing to visualization tools, or Kindly plugins that talk to visualization tools."
  [dirs]
  (for [dir dirs
        file (file-seq (io/file dir))
        :when (clojure-source? file)]
    {:file     file
     :contexts (safe-read-notes file)}))
