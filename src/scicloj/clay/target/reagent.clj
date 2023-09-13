(ns scicloj.clay.target.reagent
  (:require [clojure.string :as str]
            [hiccup.page :as page]))

(defn scittle-script [& cljs-forms]
  [:script {:type "application/x-scittle"}
   (->> cljs-forms
        (map pr-str)
        (str/join "\n"))])

(defn div-and-script [id widget]
  [[:div {:id id}]
   (scittle-script
     (list 'dom/render (list 'fn [] widget)
           (list '.getElementById 'js/document id)))])

(defn page [widgets]
  (page/html5
    [:head]
    (into
      [:body
       (page/include-js "https://unpkg.com/react@18/umd/react.production.min.js"
                        "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
                        "https://scicloj.github.io/scittle/js/scittle.js"
                        "https://scicloj.github.io/scittle/js/scittle.reagent.js")
       (scittle-script '(ns main
                          (:require [reagent.core :as r]
                                    [reagent.dom :as dom])))]
      (->> widgets
           (map-indexed (fn [i widget]
                          (div-and-script (str "widget" i)
                                          widget)))
           (apply concat)))))
