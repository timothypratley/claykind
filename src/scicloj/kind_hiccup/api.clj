(ns scicloj.kind-hiccup.api
  (:require [scicloj.kind-hiccup.to-hiccup :as to-hiccup]
            [scicloj.kindly-advice.v1.api :as ka]
            [hiccup.core :as hiccup]))

(defn reagent?
  "Reagent components may be requested by symbol: `[my-component 1]`
   or by an inline function: `['(fn [] [:h1 123])]`"
  [tag]
  (or (symbol? tag)
      (and (seq? tag)
           (= 'fn (first tag)))))

;; TODO: advise should indicate if user set or inferred
(def requested?
  (complement #{:kind/vector :kind/set :kind/map :kind/seq :kind/hiccup}))

(defn kind-request [x]
  (let [context (ka/advise {:value x})]
    (when (requested? (:kind context))
      context)))

(defn expand [hiccup]
  (if-let [context (kind-request hiccup)]
    (to-hiccup/adapt context)
    (if (vector? hiccup)
      (let [[tag & children] hiccup
            c (first children)
            attrs (and (map? c) (not (kind-request c)) c)]
        (cond (reagent? tag) (to-hiccup/reagent tag children)
              (seq? tag) (apply to-hiccup/scittle tag children)
              :else (if attrs
                      (into [tag attrs] (map expand) (next children))
                      (into [tag] (map expand) children))))
      hiccup)))

(defn html
  "Given hiccup that may contain embedded kinds, returns an HTML string."
  [hiccup]
  (hiccup/html (expand hiccup)))
