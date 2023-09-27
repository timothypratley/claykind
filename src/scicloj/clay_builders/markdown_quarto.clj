(ns scicloj.clay-builders.markdown-quarto
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.qmd :as qmd]
            [clj-yaml.core :as yaml]
            [hiccup2.core :as hiccup2]
            [hiccup.page :as page]))

;; Markdown is sensitive to whitespace (especially newlines).
;; fragments like blocks must be separated by a blank line.
;; These markdown producing functions return strings with no trailing newline,
;; which are combined with double newline.

(defn join [a b]
  (str a \newline \newline b))

(defn render-eval [{:keys [code exception out err] :as context}]
  (cond-> (qmd/block code "clojure")
          out (join (qmd/message out "stdout"))
          err (join (qmd/message err "stderr"))
          (contains? context :value) (join (qmd/adapt context))
          exception (join (qmd/message (ex-message exception) "exception"))))

(defn render-md-fragment
  "Transforms advice into a Markdown string"
  [context]
  (str/trim-newline
    (let [{:keys [code kind]} context]
      (cond
        (= kind :kind/comment) (:kindly/comment context)
        (or (contains? context :value)
            (contains? context :exception)) (render-eval context)
        :else code))))

(defn all-fragments [contexts]
  (->> (mapv render-md-fragment contexts)
       (str/join (str \newline \newline))))

(def styles
  "<style>
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
</style>")

(defn page-setup [{:keys [front-matter]}]
  (str
    (when front-matter
      (str "---"
           (str/trim-newline (yaml/generate-string front-matter)) \newline
           "---" \newline \newline))
    styles \newline \newline
    (hiccup2/html
      (page/include-js "https://cdn.jsdelivr.net/npm/vega@5"
                       "https://cdn.jsdelivr.net/npm/vega-lite@5"
                       "https://cdn.jsdelivr.net/npm/vega-embed@6"
                       "portal-main.js"))))

(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (str (page-setup options) \newline \newline
       (all-fragments contexts) \newline))
