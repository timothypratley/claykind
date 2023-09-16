(ns scicloj.kind-adapters.hiccup
  (:require [clojure.data.json :as json]
            [scicloj.kindly-advice.v1.api :as ka]
            [scicloj.clay.markdown :as md]))

(defmulti adapt :kind)

(defmethod adapt :default [{:keys [value kind]}]
  (if kind
    [:div
     [:div "Unimplemented: " [:code (pr-str kind)]]
     [:code (pr-str value)]]
    [:code (pr-str value)]))

(defn adapt-value [v]
  (adapt (ka/advise {:value v})))

(defn grid [n vs color]
  (into [:div {:style {:display               "grid"
                       :grid-template-columns (str "repeat(" n ", auto)")
                       :gap                   10
                       :align-items           "center"
                       :justify-content       "center"
                       :text-align            "center"
                       :border                "solid 1px lightgray"
                       :background            color}}]
        (for [v vs]
          (adapt-value v))))

;; TODO: make it pretty... grid? grid sux
(defmethod adapt :kind/vector [{:keys [value]}]
  (grid 1 value "lightblue"))

(defmethod adapt :kind/map [{:keys [value]}]
  (grid 2 (apply concat value) "lightgreen"))

(defmethod adapt :kind/set [{:keys [value]}]
  (grid 1 value "lightyellow"))

(defmethod adapt :kind/image [{:keys [value]}]
  [:img {:src value}])

;; TODO: comments are currently handled higher, should we handle them here or there??
;; probably here, because kind/md exists also
(defmethod adapt :kind/comment [context]
  (md/render (:kindly/comment context)))

(defmethod adapt :kind/md [{:keys [value]}]
  (md/render value))

(defmethod adapt :kind/var [{:keys [value]}]
  [:div "VAR" (str value)])

(defmethod adapt :kind/table [{:keys [value]}]
  [:div "TABLE" (pr-str value)])

(defmethod adapt :kind/seq [{:keys [value]}]
  (into [:div] (map adapt-value value)))

(defn- vega [value]
  [:div
   [:script (str "vegaEmbed(document.currentScript.parentElement, " (json/write-str value) ");")]])

(defmethod adapt :kind/vega [{:keys [value]}]
  (vega value))

;; TODO: it would be nice if we had id passed in and didn't need a lambda
(defmethod adapt :kind/vega-lite [{:keys [value]}]
  (vega value))
