(ns scicloj.claykind.io
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

(defn inside? [^File parent ^File child]
  (let [base (.getCanonicalFile parent)]
    (loop [c (.getCanonicalFile child)]
      (and c (or (.equals base c)
                 (recur (.getParentFile c)))))))

(def clojure-file-ext-regex
  #"\.clj[cx]?$")

(defn clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-find clojure-file-ext-regex (.getName file)))))

(defn kebab
  "Markdown filenames replace underscores with hyphens (https://docs.github.com/en/contributing/syntax-and-versioning-for-github-docs/using-yaml-frontmatter#filenames)"
  [s]
  (str/replace s "_" "-"))

(defn as [s extension]
  (-> (kebab s)
      (str/replace clojure-file-ext-regex (str "." extension))))

(defn target
  "Given a Clojure source file, constructs a file for output relative to target-dir if it exists."
  [target-dir ^File file extension]
  (if target-dir
    (io/file target-dir (-> (.getName file) (as extension)))
    (io/file (-> (.getPath file) (as extension)))))

(defn clojure-files
  "Given path strings, returns a sequence of files that are Clojure source files.
  When a path is a directory, will find all source files in that directory."
  [paths]
  (for [path paths
        file (file-seq (io/file path))
        :when (clojure-source? file)]
    file))

(defn when-pred [x pred]
  (when (pred x)
     x))

(defn find-config []
  (some-> (io/file "claykind.edn")
          (when-pred (memfn ^File exists))
          (slurp)
          (edn/read-string)))
