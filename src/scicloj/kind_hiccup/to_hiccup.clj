(ns scicloj.kind-hiccup.to-hiccup
  (:require [backtick :as bt]
            [clojure.data.json :as json]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kind-hiccup.id-generator :as idg]
            [scicloj.kindly-advice.v1.api :as ka]))

(defmulti adapt :kind)

(defmethod adapt :default [{:keys [value kind]}]
  (if kind
    [:div
     [:div "Unimplemented: " [:code (pr-str kind)]]
     [:code (pr-str value)]]
    (str value)))

(defmethod adapt :kind/hiccup [{:keys [value]}]
  value)

(defn adapt-value [v]
  (adapt (ka/advise {:value v})))

(defn grid [props vs]
  (into [:div props]
        ;; TODO: adapt outside! not a grid except by css
        (for [v vs]
          [:div {:style {:border  "1px solid grey"
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
  #_(md/render (:kindly/comment context)))

(defmethod adapt :kind/md [{:keys [value]}]
  ;; value might be ^:kind/md ["markdown"] or just a string
  [:p (cond (coll? value) (first value)
            (string? value) value
            :else (str value))]
  #_(md/render value))

(defmethod adapt :kind/var [{:keys [value]}]
  [:div (str value)])

(defmethod adapt :kind/table [{:keys [value]}]
  (let [{:keys [column-names row-vectors]} value]
    [:table
     [:thead
      (into [:tr]
            (for [header column-names]
              [:th (adapt header)]))]
     (into [:tbody]
           (for [row row-vectors]
             (into [:tr]
                   (for [column row]
                     [:td (adapt column)]))))]))

(defmethod adapt :kind/seq [{:keys [value]}]
  (into [:div] (map adapt-value) value))

(defn vega [value]
  [:div {:style {:width "100%"}}
   [:script (str "vegaEmbed(document.currentScript.parentElement, " (json/write-str value) ");")]])

(defmethod adapt :kind/vega [{:keys [value]}]
  (vega value))

(defmethod adapt :kind/vega-lite [{:keys [value]}]
  (vega value))

(defn format-code [form]
  (binding [pprint/*print-pprint-dispatch* pprint/code-dispatch]
    (with-out-str (pprint/pprint form))))

(defn scittle [& forms]
  [:script {:type "application/x-scittle"}
   (str/join \newline (map format-code forms))])

(defn reagent [component args]
  (let [id (idg/gen-id)]
    [:div {:id id}
     (-> (bt/template (dom/render (js/document.getElementById ~id)
                                  ~(into [component] args)))
         (scittle))]))

(defmethod adapt :kind/reagent [{:keys [value]}]
  (if (vector? value)
    (let [[component & args] value]
      (reagent component args))
    (reagent value nil)))
