(ns scicloj.kindly-render.notes-markdown
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [hiccup.core :as hiccup]
            [scicloj.kindly-render.value-hiccup :as value-hiccup]
            [scicloj.kindly-render.value-markdown :as value-markdown]
            [hiccup.page :as page]))

;; Markdown is sensitive to whitespace (especially newlines).
;; fragments like blocks must be separated by a blank line.
;; These markdown producing functions return strings with no trailing newline,
;; which are combined with double newline.

(defn join [a b]
  (if (str/blank? a)
    b
    (str a \newline \newline b)))

(defn render-context
  "Transforms a context with advice into a markdown string"
  [context options]
  (let [{:keys [code exception out err kind]} context]
    (str/trim-newline
      (cond-> ""
              code (str (value-markdown/block code "clojure"))
              out (join (value-markdown/message out "stdout"))
              err (join (value-markdown/message err "stderr"))
              (contains? context :value) (join (value-markdown/adapt context options))
              exception (join (value-markdown/message (ex-message exception) "exception"))))))

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
      (hiccup/html (page/include-css "style.css")) \newline
      (hiccup/html (apply page/include-js (value-hiccup/include-js))) \newline
      ;; TODO: this could should only exist when user needs it,
      ;; either detected, or requested, or they could just add it as hiccup??

      (hiccup/html
        (value-hiccup/scittle '[(require '[reagent.core :as r]
                                         '[reagent.dom :as dom]
                                         '[clojure.str :as str])]
                              options))
      )))

(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  ;; rendering must happen before page-setup to gather dependencies (maybe not for a book though?)
  (let [context-strs (mapv #(render-context % options) contexts)]
    (str (page-setup contexts options) \newline \newline
         (str/join (str \newline \newline) context-strs) \newline)))
