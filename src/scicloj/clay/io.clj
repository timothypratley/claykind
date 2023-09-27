(ns scicloj.clay.io
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

(def clojure-file-ext-regex
  #"\.clj[cx]?$")

(defn clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-find clojure-file-ext-regex (.getName file)))))

(defn as [s extension]
  (str/replace s clojure-file-ext-regex (str "." extension)))

(defn target
  "Given a clojure source file, constructs a file for output relative to target-dir if it exists."
  [^File file extension {:keys [target-dir]}]
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

(defmacro check [x clause]
  `(when (-> ~x ~clause)
     ~x))

(defn find-config []
  (some-> (io/file "claykind.edn")
          (check (.exists))
          (slurp)
          (edn/read-string)))
