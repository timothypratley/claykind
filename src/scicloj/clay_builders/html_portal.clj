(ns scicloj.clay-builders.html-portal
  (:require [scicloj.kind-portal.v1.impl :as kpi]
            [scicloj.clay-builders.html_reagent :as cre]))

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

(defn portal-widget [value]
  ['(fn [{:keys [edn-str]}]
      ;; TODO: this code has not been loaded
      (let [api (js/portal.extensions.vs_code_notebook.activate)]
        [:div
         [:div
          {:ref (fn [el]
                  (.renderOutputItem api
                                     (clj->js {:mime "x-application/edn"
                                               :text (fn [] edn-str)})
                                     el))}]]))
   {:edn-str (pr-str-with-meta value)}])

(defn expr-result [context]
  (if-let [c (:kindly/comment context)]
    [:div c]
    (if (contains? context :value)
      (-> (kpi/prepare context)
          (portal-widget))
      ;; TODO: maybe error
      [:div (:code context)])))

(defn notes-to-html-portal [contexts options]
  (->> (mapv expr-result contexts)
       (cre/page)))
