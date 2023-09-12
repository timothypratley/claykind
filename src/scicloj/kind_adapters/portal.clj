(ns scicloj.kind-adapters.portal)

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

(defn portal-widget [value]
  ['(fn [{:keys [edn-str]}]
      (let [api (js/portal.extensions.vs_code_notebook.activate)]
        [:div
         [:div
          {:ref (fn [el]
                  (.renderOutputItem api
                                     (clj->js {:mime "x-application/edn"
                                               :text (fn [] edn-str)})
                                     el))}]]))
   {:edn-str (pr-str-with-meta value)}])

(defn page [widgets]
  (hiccup.page/html5
    [:head]
    (into
      [:body
       (hiccup.page/include-js "https://unpkg.com/react@18/umd/react.production.min.js"
                               "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
                               "https://scicloj.github.io/scittle/js/scittle.js"
                               "https://scicloj.github.io/scittle/js/scittle.reagent.js"
                               portal-url)
       (scittle-script '(ns main
                          (:require [reagent.core :as r]
                                    [reagent.dom :as dom])))]
      (->> widgets
           (map-indexed (fn [i widget]
                          (div-and-script (str "widget" i)
                                          widget)))
           (apply concat)))))

(defn as-portal-hiccup [hiccup]
  (with-meta
    hiccup
    {:portal.viewer/default :portal.viewer/hiccup}))

(defn img [url]
  (as-portal-hiccup
    [:img {:height 50 :width 50
           :src url}]))

(defn md [text]
  (as-portal-hiccup
    [:portal.viewer/markdown
     text]))

(defn vega-lite-point-plot [data]
  (as-portal-hiccup
    [:portal.viewer/vega-lite
     (-> {:data {:values data},
          :mark "point"
          :encoding
          {:size {:field "w" :type "quantitative"}
           :x {:field "x", :type "quantitative"},
           :y {:field "y", :type "quantitative"},
           :fill {:field "z", :type "nominal"}}})]))
