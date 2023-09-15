(ns scicloj.kind-adapters.hiccup
  (:require [scicloj.kindly-advice.v1.api :as ka]))

(defmulti adapt :kind)

(defmethod adapt :default [{:keys [value kind]}]
  [:div
   (when kind
     [:div "Unimplemented: " [:code (pr-str kind)]])
   [:pre [:code (pr-str value)]]])

(defn adapt-value [v]
  (adapt (ka/advise {:value v})))

;; TODO: make it pretty... grid?
(defmethod adapt :kind/vector [{:keys [value]}]
  `[:div "[" ~@(map adapt-value value) "]"])

(defmethod adapt :kind/map [{:keys [value]}]
  `[:div "{" ~@(map adapt-value value) "}"])

(defmethod adapt :kind/set [{:keys [value]}]
  `[:div "#{" ~@(map adapt-value value) "}"])

(defmethod adapt :kind/image [{:keys [value]}]
  [:img {:src value}])

(defmethod adapt :kind/comment [{:kindly/keys [comment]}]
  [:p comment])

(defmethod adapt :kind/var [{:keys [value]}]
  [:div "VAR" (str value)])

(defmethod adapt :kind/table [{:keys [value]}]
  [:div "TABLE" (pr-str value)])

(defmethod adapt :kind/seq [{:keys [value]}]
  (into [:div] (map adapt-value value)))

(defmethod adapt :kind/vega [{:keys [value]}]
  (list 'fn '[id]
        (list 'js/vegaEmbed '(str "#" id) (list 'clj->js (list 'quote value)))))

(defmethod adapt :kind/vega-lite [{:keys [value]}]
  ;; TODO: it would be nice if we had id passed in and didn't need a lambda
  (list 'fn '[id]
        (list 'js/vegaEmbed '(str "#" id) (list 'clj->js (list 'quote value)))))
