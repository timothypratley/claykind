(ns scicloj.kind-hiccup.api2
  (:require [scicloj.kind-hiccup.to-hiccup :as to-hiccup]
            [scicloj.kindly-advice.v1.api :as ka]
            [hiccup.core :as hiccup]))

(defn when-pred [x pred]
  (when (pred x)
    x))

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
  (when-pred (ka/advise {:value x}) (comp requested? :kind)))

(defn expand [hiccup]
  (if-let [context (kind-request hiccup)]
    (to-hiccup/adapt context)
    (if (vector? hiccup)
      (let [[tag & children] hiccup
            attrs (when-pred (first children) (every-pred map? (complement kind-request)))]
        (cond (reagent? tag) (to-hiccup/reagent tag children)
              (seq? tag) (apply to-hiccup/scittle tag children)
              :else (if attrs
                      (into [tag attrs] (map expand) (next children))
                      (into [tag] (map expand) children))))
      hiccup)))

(expand [:div "Hello " [:em "world"]])
(expand [:div "Hello " [:em "world"] ['(fn [] (render-it!)) 2]])

(defn html
  "Given hiccup that may contain embedded kinds, returns an HTML string."
  [hiccup]
  (hiccup/html (expand hiccup)))
