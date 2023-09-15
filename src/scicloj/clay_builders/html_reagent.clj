(ns scicloj.clay-builders.html_reagent
  (:require [clojure.string :as str]
            [hiccup.page :as page]
            [portal.api :as portal]))

(defn scittle-script [& cljs-forms]
  [:script {:type "application/x-scittle"}
   ;; TODO: maybe we can use cljfmt?
   (->> (map pr-str cljs-forms)
        (str/join \newline))])

(defn div-and-script [idx widget]
  (if (vector? widget)
    [widget]
    (let [id (str "widget" idx)]
      [[:div {:id id}]
       (scittle-script (list widget id))])))

;; TODO: don't do this
(defonce portal-dev (portal/url (portal/start {})))
(def portal-url (let [[host query] (str/split portal-dev #"\?")]
                  (str host "/main.js?" query)))

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
  [:head (page/include-js "https://unpkg.com/react@18/umd/react.production.min.js"
                          "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
                          "https://scicloj.github.io/scittle/js/scittle.js"
                          "https://scicloj.github.io/scittle/js/scittle.reagent.js"
                          ;; TODO: these aren't really reagent
                          "https://cdn.jsdelivr.net/npm/vega@5"
                          "https://cdn.jsdelivr.net/npm/vega-lite@5"
                          "https://cdn.jsdelivr.net/npm/vega-embed@6"
                          "portal-main.js"
                          portal-url)])

(def body
  [:body #_(scittle-script '(ns main
                            (:require [reagent.core :as r]
                                      [reagent.dom :as dom]
                                      [emmy-viewers.sci]))
                         ;; TODO: EmmyViewers are not loaded...
                         ;; do we need to make a scittle.emmy?
                         ;; cljs.pprint plugin
                         '(emmy-viewers.sci/install!))])

(def as-skittle-xform
  (comp (map-indexed div-and-script) cat))

(defn page [widgets]
  (page/html5 head (into body as-skittle-xform widgets)))
