(ns scicloj.clay-builders.markdown-quarto
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.qmd :as qmd]
            [clj-yaml.core :as yaml]))

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
</style>
")

(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} {:keys [quarto]}]
  (format "---\n%s\n---\n%s%s"
          (yaml/generate-string quarto)
          styles
          (->> contexts
               (map render-md )
               (str/join \newline))))
