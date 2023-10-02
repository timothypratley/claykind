(ns scicloj.clay-builders.html_reagent
  (:require [clojure.string :as str]
            [hiccup.page :as page]))

;; TODO: how do we want to consume dependencies?
;;
;; Serving dependencies:

;; * remote (easy, simple)
;; * bundled (encapsulated) (qmd include everything or just reference external files)
;; * duplicated, npm-installed, or curl (offline friendly)
;; * custom ClojureScript build (complicated)

;; * How do you specify the dependencies? (I don't want all the dependencies all the time)
;; * In a book, combining dependencies

;; What about serving data?
;; example, need a dataset, or a sql-lite db
;; Dependency strategy can apply to data as well.
;; Loading resources can be a pain point especially between different tools.
;; Needs to work in development and in publication.
;; We need compatible URLs.

;; How do we handle multiple pages in a book?
;; Can we make this the same in development and publication?
;; Not solved by Clay/Clerk

(def head
  [:head
   (page/include-css "style.css")
   (page/include-js "https://unpkg.com/react@18/umd/react.production.min.js"
                    "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
                    "https://scicloj.github.io/scittle/js/scittle.js"
                    "https://scicloj.github.io/scittle/js/scittle.reagent.js"
                    ;; TODO: these aren't really reagent
                    "https://cdn.jsdelivr.net/npm/vega@5"
                    "https://cdn.jsdelivr.net/npm/vega-lite@5"
                    "https://cdn.jsdelivr.net/npm/vega-embed@6"
                    "portal-main.js")])

(def body
  [:body [:script {:type "application/x-scittle"}
          "(ns main
            (:require [reagent.core :as r]
                      [reagent.dom :as dom]))"]])

(defn page [widgets]
  (page/html5 head (into body widgets)))
