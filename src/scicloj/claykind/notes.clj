(ns scicloj.claykind.notes
  "Notes organize contexts by removing whitespace and joining adjacent comment blocks."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [scicloj.claykind.read :as read]
            [scicloj.claykind.kinds :as kinds])
  (:import (java.io File)))

(defn join-comment-blocks [comment-blocks]
  {:kind           :kindly/comment
   :kindly/comment (-> (map :kindly/comment comment-blocks)
                       (str/join))})

(def notebook-xform
  "Transducer to infer kinds, join comment blocks, and remove unnecessary whitespace."
  (comp
    ;; infer kinds
    (map (fn [context]
           (if (-> context :kind #{:kindly/comment :kindly/whitespace})
             context
             (kinds/infer-kind context))))
    ;; join comment blocks
    (partition-by (comp some? :kindly/comment))
    (mapcat (fn [part]
              (if (-> part first :kindly/comment)
                [(join-comment-blocks part)]
                part)))
    ;; remove whitespace
    (remove (comp #{:kindly/whitespace} :kind))))

(defn eval-notes [code]
  (into [] notebook-xform (read/parse-forms code)))

(defn safe-eval-notes [code]
  (binding [read/*on-eval-error* nil]
    (eval-notes code)))

(defn clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-matches #".*\.clj[cx]?$" (.getName file)))))

(defn all-notes
  "Find and read all notebooks."
  [dirs]
  (for [dir dirs
        file (file-seq (io/file dir))
        :when (clojure-source? file)]
    {:file     file
     :contexts (-> (slurp file)
                   (safe-eval-notes))}))
