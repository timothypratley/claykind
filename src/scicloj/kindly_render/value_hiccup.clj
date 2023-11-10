(ns scicloj.kindly-render.value-hiccup
  (:require [clojure.data.json :as json]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kind-portal.v1.impl :as kpi]
            [scicloj.kindly-advice.v1.api :as ka]
            [scicloj.kindly-render.from-markdown :as from-markdown]
            [scicloj.read-kinds.notes :as notes])
  (:import (clojure.lang IDeref)))

(defmulti adapt :kind)

(def ^:dynamic *deps* (atom #{}))
(def ^:dynamic *scope-name* "unscoped")
(def ^:dynamic *counter* (atom 0))

;; TODO: might be better to pass scope through options?
(defmacro scope
  "Scoping to filename avoids id collisions when compiling multiple pages into a book"
  [scope-name & body]
  `(binding [*scope-name* ~scope-name
             *counter* (atom 0)]
     ~@body))

(def dep-includes
  {:portal  ["portal-main.js"]
   :scittle ["https://scicloj.github.io/scittle/js/scittle.js"]
   :reagent ["https://unpkg.com/react@18/umd/react.production.min.js"
             "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
             "https://scicloj.github.io/scittle/js/scittle.js"
             "https://scicloj.github.io/scittle/js/scittle.reagent.js"]
   :vega    ["https://cdn.jsdelivr.net/npm/vega@5"
             "https://cdn.jsdelivr.net/npm/vega-lite@5"
             "https://cdn.jsdelivr.net/npm/vega-embed@6"]})

(defn include-js []
  (distinct (mapcat dep-includes @*deps*)))

(defmethod adapt :default [{:keys [value kind]} options]
  (if kind
    [:div
     [:div "Unimplemented: " [:code (pr-str kind)]]
     [:code (pr-str value)]]
    (str value)))

;; TODO: might not be necessary??
;; Don't show
(defmethod adapt :kind/hidden [context options])

(defn pprint [value]
  [:pre [:code (binding [*print-meta* true]
                 (with-out-str (pprint/pprint value)))]])

(defmethod adapt :kind/pprint [{:keys [value]} options]
  (pprint value))

(defn adapt-value [v options]
  (adapt (notes/derefing-advise {:value v}) options))

(defn grid [props vs options]
  (into [:div props]
        ;; TODO: adapt outside! not a grid except by css
        (for [v vs]
          [:div {:style {:border  "1px solid grey"
                         :padding "2px"}}
           (adapt-value v options)])))

(defmethod adapt :kind/vector [{:keys [value]} options]
  (grid {:class "kind_vector"} value options))

(defmethod adapt :kind/map [{:keys [value]} options]
  (grid {:class "kind_map"} (apply concat value) options))

(defmethod adapt :kind/set [{:keys [value]} options]
  (grid {:class "kind_set"} value options))

(defmethod adapt :kind/seq [{:keys [value]} options]
  (grid {:class "kind_seq"} value options))

(defmethod adapt :kind/image [{:keys [value]} options]
  (if (string? value)
    [:img {:src value}]
    [:div "Image kind not implemented"]))

(defmethod adapt :kind/md [{:keys [value]} options]
  (from-markdown/hiccup value options))

;; Don't show vars
(defmethod adapt :kind/var [{:keys [value]} options])

(defmethod adapt :kind/table [{:keys [value]} options]
  (let [{:keys [column-names row-vectors]} value]
    [:table
     [:thead
      (into [:tr]
            (for [header column-names]
              [:th (adapt header options)]))]
     (into [:tbody]
           (for [row row-vectors]
             (into [:tr]
                   (for [column row]
                     [:td (adapt column options)]))))]))

(defn vega [value {:keys [flavor]}]
  (if (= flavor "gfm")
    [:div {:style {:width "100%"}}
     [:img {:src "https://placehold.co/600x400"}]]
    (do (swap! *deps* conj :vega)
        [:div {:style {:width "100%"}}
         [:script (str "vegaEmbed(document.currentScript.parentElement, " (json/write-str value) ");")]])))

(defmethod adapt :kind/vega [{:keys [value]} options]
  (vega value options))

(defmethod adapt :kind/vega-lite [{:keys [value]} options]
  (vega value options))

(defn format-code [form]
  (binding [pprint/*print-pprint-dispatch* pprint/code-dispatch]
    (with-out-str (pprint/pprint form))))

(defn scittle [forms {:keys [flavor]}]
  (let [s (str/join \newline (map format-code forms))]
    (if (= flavor "gfm")
      [:pre [:code s]]
      (do (swap! *deps* conj :scittle)
          [:script {:type "application/x-scittle"}
           (str/join \newline (map format-code forms))]))))

(defn no-spaces [s]
  (str/replace s #"\s" "-"))

(defn gen-id []
  (str (no-spaces *scope-name*) "-" (swap! *counter* inc)))

(defn reagent [v options]
  (when (= (:flavor options) "gfm")
    (swap! *deps* conj :reagent))
  (let [id (gen-id)]
    [:div {:id id}
     (-> [(list 'dom/render v (list 'js/document.getElementById id))]
         (scittle options))]))

(defmethod adapt :kind/reagent [{:keys [value]} options]
  (if (vector? value)
    (reagent value options)
    (reagent [value] options)))

(defn portal [context {:keys [flavor]}]
  (if (= flavor "gfm")
    (pprint (:value context))
    (do (swap! *deps* conj :portal)
        (let [portal-value (kpi/prepare context)
              value-str (binding [*print-meta* true]
                          (pr-str portal-value))]
          [:div
           [:script
            (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
  {\"mime\": \"x-application/edn\",
   \"text\": (() => " (pr-str value-str) ")},
  document.currentScript.parentElement);")]]))))

(defmethod adapt :kind/portal [{:keys [value]} options]
  ;; TODO: it isn't clear that value must be a vector wrapper, but it probably is???
  ;; Wouldn't it be nicer if :tool/portal was orthogonal to :kind/vega etc...
  ;; what about :kind/chart, :grammar/vega, :tool/portal ... too much?
  ;; Also, this conflicts with choosing a tool to render with (what if I want everything to be a portal?)

  ;; TODO: kind/portal is replacing kind/hiccup
  (portal {:value (first value)} options))

(defn reagent?
  "Reagent components may be requested by symbol: `[my-component 1]`
   or by an inline function: `['(fn [] [:h1 123])]`"
  [tag]
  (or (symbol? tag)
      (and (seq? tag)
           (= 'fn (first tag)))))

(defn kind-request [x]
  (let [context (notes/derefing-advise {:value x})]
    (when (:kind-meta context)
      context)))

(defn expand [hiccup options]
  (if-let [context (kind-request hiccup)]
    (adapt context)
    (cond (instance? IDeref hiccup)
          (recur @hiccup options)

          (vector? hiccup)
          (let [[tag & children] hiccup
                c (first children)
                attrs (and (map? c) (not (kind-request c)) c)]
            (cond (reagent? tag) (reagent hiccup options)
                  (seq? tag) (scittle hiccup options)
                  :else (if attrs
                          (into [tag attrs] (map #(expand % options)) (next children))
                          (into [tag] (map #(expand % options)) children))))

          :else
          hiccup)))

(defmethod adapt :kind/hiccup [{:keys [value]} options]
  (expand value options))
