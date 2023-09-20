(ns scicloj.read-kinds.api
  (:require [clojure.java.io :as io]
            [scicloj.read-kinds.notes :as notes]))

;; TODO: maybe we don't need this namespace???

(defn notebook
  "Reads a notebook (clojure source file) from path and returns a representation.
  A representation is the file and a vector of contexts suitable for visualization.
  Contexts contains the original code, the evaluated value, the kind, and advice."
  ([path] (notebook path {}))
  ([path options]
   (notes/safe-read-notes (io/file path) options)))

(defn all-notebooks
  "Finds all notebooks in dirs (or [\"notebooks\"])
  and returns a lazy sequence of `{:file ..., :contexts ...}`,
  where contexts represent the contents of the notebook after evaluation and advice.
  Contexts contain the original code, the evaluated value, and the kind."
  ;; TODO: not sure this makes sense
  ([] (notes/all-notes {:dirs ["notebooks"]}))
  ([options] (notes/all-notes options)))
