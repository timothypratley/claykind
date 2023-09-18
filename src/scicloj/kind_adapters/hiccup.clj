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

(defn grid [props n vs]
  (into [:div (merge-with merge
                          {}
                          props)]
        ;; TODO: adapt outside! not a grid except by css
        (for [v vs]
          [:div {:style {:border "1px solid grey"
                         :padding "2px"}}
           (adapt-value v)])))

(defmethod adapt :kind/vector [{:keys [value]}]
  (grid {:class "kind_vector"} 2 (apply concat (zipmap (range) value))))

(defmethod adapt :kind/map [{:keys [value]}]
  (grid {:class "kind_map"} 2 (apply concat value)))

(defmethod adapt :kind/set [{:keys [value]}]
  (grid {:class "kind_set"} 1 value))

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

(defmethod adapt :kind/hiccup [{:keys [value]}]
  value)
