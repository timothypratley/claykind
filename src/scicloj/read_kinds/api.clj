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
   (notes/read-notes (io/file path) options)))
