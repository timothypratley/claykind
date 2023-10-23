(ns scicloj.clay-builders.markdown-page
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [scicloj.kind-adapters.to-markdown :as to-markdown]
            [scicloj.kind-hiccup.api :as kind-hiccup]
            [hiccup.page :as page]))

;; Markdown is sensitive to whitespace (especially newlines).
;; fragments like blocks must be separated by a blank line.
;; These markdown producing functions return strings with no trailing newline,
;; which are combined with double newline.

(defn join [a b]
  (str a \newline \newline b))

(defn render-eval [{:keys [code exception out err] :as context} options]
  (cond-> (to-markdown/block code "clojure")
          out (join (to-markdown/message out "stdout"))
          err (join (to-markdown/message err "stderr"))
          (contains? context :value) (join (to-markdown/adapt context options))
          exception (join (to-markdown/message (ex-message exception) "exception"))))

(defn render-md-fragment
  "Transforms advice into a Markdown string"
  [context options]
  (str/trim-newline
    (let [{:keys [code kind]} context]
      (cond
        (= kind :kind/comment) (:kindly/comment context)
        (or (contains? context :value)
            (contains? context :exception)) (render-eval context options)
        :else code))))

(defn all-fragments [contexts options]
  (->> (mapv #(render-md-fragment % options) contexts)
       (str/join (str \newline \newline))))

;; TODO: DRY and move to a css file
(def styles
  "<style>
.sourceCode:has(.printedClojure) {
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

.kind_seq {
  background:            bisque;
  display:               grid;
  grid-template-columns: repeat(auto-fit, minmax(auto, max-content));
  align-items:           center;
  justify-content:       center;
  text-align:            center;
  border:                solid 1px black;
}
</style>")

;; TODO: this should be user manageable
(def js-includes
  ["https://cdn.jsdelivr.net/npm/vega@5"
   "https://cdn.jsdelivr.net/npm/vega-lite@5"
   "https://cdn.jsdelivr.net/npm/vega-embed@6"
   "https://unpkg.com/react@18/umd/react.production.min.js"
   "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
   "https://scicloj.github.io/scittle/js/scittle.js"
   "https://scicloj.github.io/scittle/js/scittle.reagent.js"
   "/js/portal-main.js"])

;; TODO: should kindly specify a way to provide front-matter?
(defn page-setup [contexts options]
  (let [[{:keys [form]}] contexts
        {:keys [front-matter]} (meta form)]
    (str
      (when front-matter
        (str "---" \newline
             (str/trim-newline (json/write-str front-matter)) \newline
             "---" \newline \newline))
      styles \newline \newline
      ;; TODO: for a book these should just go in _quarto.yml as include-in-header
      ;; But for a standalone markdown file we need them
      ;; How do we tell the difference?
      (kind-hiccup/html (apply page/include-js js-includes)) \newline
      ;; TODO: this could should only exist when user needs it,
      ;; either detected, or requested, or they could just add it as hiccup??
      ;; (kind/hiccup '[(require [reagent.core :as r] [reagent.dom :as dom])])
      (kind-hiccup/html
        ['(require '[reagent.core :as r]
                   '[reagent.dom :as dom])]))))

(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (str (page-setup contexts options) \newline \newline
       (all-fragments contexts options) \newline))
