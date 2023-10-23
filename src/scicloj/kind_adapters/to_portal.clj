(ns scicloj.kind-adapters.to-portal
  (:require [scicloj.kind-portal.v1.impl :as kpi]))

(defmulti adapt :kind)

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

(defn portal-widget [value options]
  [:div
   [:script
    (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
  {\"mime\": \"x-application/edn\",
   \"text\": (() => " (pr-str (pr-str-with-meta value)) ")},
  document.currentScript.parentElement);")]])

(defmethod adapt :default [context options]
  (portal-widget (kpi/prepare context) options))
