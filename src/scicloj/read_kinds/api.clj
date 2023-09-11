(ns scicloj.read-kinds.api
  (:require [clojure.java.io :as io]
            [scicloj.read-kinds.notes :as notes]))

(defn notebook
  "Reads a notebook (clojure source file) from path and returns a representation.
  A representation is a vector of kindly advices suitable for visualization.
  Advice contains the original code, the evaluated value, and the kind."
  [path]
  (notes/safe-read-notes (io/file path)))

(defn all-notebooks
  "Finds all notebooks in dirs (or [\"notebooks\"])
  and returns a lazy sequence of `{:file ..., :advices ...}`,
  where `advices` are notebooks represented as a vector of kindly advices.
  Advice contain the original code, the evaluated value, and the kind."
  ([] (notes/all-notes ["notebooks"]))
  ([dirs] (notes/all-notes dirs)))
