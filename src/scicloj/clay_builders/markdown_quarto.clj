(ns scicloj.clay-builders.markdown-quarto
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.qmd :as qmd]
            [clj-yaml.core :as yaml]
            [hiccup.core :as hiccup]
            [hiccup.page :as page]))

(defn message [msg]
  (str ">" msg \newline))

(defn clojure-code [{:keys [code exception out err] :as context}]
  (str "```clojure" \newline
       code \newline
       "```" \newline
       (when out
         (qmd/as-printed-clj #_";OUT " out)) ; TODO: a separate box?
       (when err
         (qmd/as-printed-clj #_";ERR " err)) ; TODO: a separate box?
       (when (contains? context :value)
         (qmd/adapt context))
       (when exception
         (str \newline
              (message exception)))))

(defn render-md
  "Transforms advice into a Markdown string"
  [context]
  (let [{:keys [code kind]} context]
    (cond
      (= kind :kind/comment) (:kindly/comment context)
      (or (contains? context :value)
          (contains? context :exception)) (clojure-code context)
      :else code)))

(def styles
  "
<style>
.printedClojure .sourceCode {
  background-color: transparent;
  border-style: none;
}

.kind_map {
  background:            lightgreen;
  display:               grid;
  grid-template-columns: repeat(2, auto);
  justify-content:       center;
  text-align:            right;
  border: solid 1px black;
  border-radius: 10px;
}

.kind_vector {
  background:            lightblue;
  display:               grid;
  grid-template-columns: repeat(1, auto);
  align-items:           center;
  justify-content:       center;
  text-align:            center;
  border:                solid 2px black;
  padding:               10px;
}

.kind_set {
  background:            lightyellow;
  display:               grid;
  grid-template-columns: repeat(auto-fit, minmax(auto, max-content));
  align-items:           center;
  justify-content:       center;
  text-align:            center;
  border:                solid 1px black;
}
</style>
")

(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} {:keys [quarto]}]
  (format "---\n%s\n---\n%s\n%s\n%s"
          (yaml/generate-string quarto)
          styles
          (hiccup/html
           (page/include-js
            "https://cdn.jsdelivr.net/npm/vega@5"
            "https://cdn.jsdelivr.net/npm/vega-lite@5"
            "https://cdn.jsdelivr.net/npm/vega-embed@6"
            "portal-main.js"))
          (->> contexts
               (map render-md )
               (str/join \newline))))
