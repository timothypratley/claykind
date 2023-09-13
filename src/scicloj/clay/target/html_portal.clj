(ns scicloj.clay.target.html-portal
  (:require [hiccup2.core :as hiccup2]
            [scicloj.kind-portal.v1.impl :as kpi]
            [scicloj.clay.target.reagent :as cre]))

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


(defn notes-to-html-portal [contexts options]
  (->> (map kpi/prepare contexts)
       (map portal-widget)
       (cre/page)))
