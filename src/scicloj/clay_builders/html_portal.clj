(ns scicloj.clay-builders.html-portal
  (:require [scicloj.kind-portal.v1.impl :as kpi]
            [scicloj.clay-builders.html_reagent :as cre]))

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

;; TODO: quasi-quasi-quote
;; `(... ~@()) <- qualifies
;; '( ~) <- does not work
;; #'( ~x)  <- without qualifying
;; We should make this
(defn portal-widget [value]
  (list 'fn '[id]
        (list '.renderOutputItem '(js/portal.extensions.vs_code_notebook.activate)
              ;; Could this come from a file instead for large values?
              (list 'clj->js {:mime "x-application/edn"
                              :text (list 'fn [] (pr-str-with-meta value))})
              '(js/document.getElementById id))))

(defn expr-result [context]
  (if-let [c (:kindly/comment context)]
    [:div c]
    (if (contains? context :value)
      (-> (kpi/prepare context)
          (portal-widget))
      ;; TODO: maybe error
      [:div (:code context)])))

(defn notes-to-html-portal [{:keys [contexts]} options]
  (->> (mapv expr-result contexts)
       (cre/page)))
