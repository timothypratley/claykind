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
  (map adapt-value value))
