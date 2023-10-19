(ns scicloj.kind-hiccup.api
  (:require [scicloj.kind-hiccup.compiler :as compiler]))

(defn html
  "Given hiccup, returns an HTML string."
  [hiccup]
  (let [sb (new StringBuilder)]
    (compiler/compile-hiccup sb hiccup)
    (str sb)))
