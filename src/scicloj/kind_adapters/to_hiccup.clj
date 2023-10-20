(ns scicloj.kind-adapters.to-hiccup
  (:require [clojure.data.json :as json]
            [scicloj.kindly-advice.v1.api :as ka]))

(defmulti adapt :kind)

(defmethod adapt :default [{:keys [value kind]}]
  (if kind
    [:div
     [:div "Unimplemented: " [:code (pr-str kind)]]
     [:code (pr-str value)]]
    [:code (pr-str value)]))

(defn adapt-value [v]
  (adapt (ka/advise {:value v})))

(defn grid [props vs]
  (into [:div props]
        ;; TODO: adapt outside! not a grid except by css
        (for [v vs]
          [:div {:style {:border "1px solid grey"
                         :padding "2px"}}
           (adapt-value v)])))

(defmethod adapt :kind/vector [{:keys [value]}]
  (grid {:class "kind_vector"} value))

(defmethod adapt :kind/map [{:keys [value]}]
  (grid {:class "kind_map"} (apply concat value)))

(defmethod adapt :kind/set [{:keys [value]}]
  (grid {:class "kind_set"} value))

(defmethod adapt :kind/seq [{:keys [value]}]
  (grid {:class "kind_seq"} value))

(defmethod adapt :kind/image [{:keys [value]}]
  (if (string? value)
    [:img {:src value}]
    [:div "Image kind not implemented"]))

;; TODO: is there a nice way to be able to render markdown by adding an adapter?
;; because we don't want flexmark or nextjournal dependencies in this project
;; TODO: comments are currently handled higher, should we handle them here or there??
;; probably here, because kind/md exists also
(defmethod adapt :kind/comment [context]
  [:p (:kindly/comment context)]
  #_ (md/render (:kindly/comment context)))

(defmethod adapt :kind/md [{:keys [value]}]
  ;; TODO: this seems a bit wierd but is to support ^:kind/md ["markdown"]
  [:p (if (coll? value)
        (first value)
        value)]
  #_(md/render value))

(defmethod adapt :kind/var [{:keys [value]}]
  [:div "VAR" (str value)])

(defmethod adapt :kind/table [{:keys [value]}]
  [:div "TABLE" (pr-str value)])

(defmethod adapt :kind/seq [{:keys [value]}]
  (into [:div] (map adapt-value value)))

(defn- vega [value]
  [:div {:style {:width "100%"}}
   [:script
    [:hiccup/raw-html
     (str "vegaEmbed(document.currentScript.parentElement, " (json/write-str value) ");")]]])

(defmethod adapt :kind/vega [{:keys [value]}]
  (vega value))

;; TODO: it would be nice if we had id passed in and didn't need a lambda
(defmethod adapt :kind/vega-lite [{:keys [value]}]
  (vega value))

(defmethod adapt :kind/hiccup [{:keys [value]}]
  value)
