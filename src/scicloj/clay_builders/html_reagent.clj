(ns scicloj.clay-builders.html_reagent
  (:require [clojure.string :as str]
            [hiccup.page :as page]
            [portal.api :as portal]))

(defn scittle-script [& cljs-forms]
  [:script {:type "application/x-scittle"}
   (->> cljs-forms
        (map pr-str)
        (str/join "\n"))])

(defn div-and-script [idx widget]
  (if (keyword? (first widget))
    [widget]
    (let [id (str "widget" idx)]
      [[:div {:id id}]
       (scittle-script
         (list 'dom/render (list 'fn [] widget)
               (list '.getElementById 'js/document id)))])))

;; TODO: don't do this
(defonce portal-dev (portal/url (portal/open)))
(def portal-url (let [[host query] (str/split portal-dev #"\?")]
                  (str host "/main.js?" query)))

(def head
  [:head (page/include-js "https://unpkg.com/react@18/umd/react.production.min.js"
                          "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
                          "https://scicloj.github.io/scittle/js/scittle.js"
                          "https://scicloj.github.io/scittle/js/scittle.reagent.js"
                          portal-url)])

(def body
  [:body (scittle-script '(ns main
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
