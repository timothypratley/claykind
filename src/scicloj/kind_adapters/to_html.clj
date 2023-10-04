(ns scicloj.kind-adapters.to-html
  (:require [huff.core :as huff]))

(set! *warn-on-reflection* true)

(defn html [hiccup]
  (huff/html {:allow-raw true} hiccup))

;; We extend huff to do some extra things:
;; 1. Detect kindly metadata that indicates the value should be converted into hiccup.
;; 2. Detect `['(fn [] ...)]` and `[mathbox.run]` as a reagent component
;; 3. Detect `['(println "Hello world")]` as a scittle script.

(in-ns 'huff.core)

(require '[scicloj.kind-adapters.to-hiccup :as ahiccup]
         '[scicloj.kindly-advice.v1.api :as ka]
         '[scicloj.clay-builders.id-generator :as idg]
         '[clojure.pprint :as pprint])

;; ^:kind/dataset {}
;; {:vega ...}
;; :kind/pprint, :kind/map
;;
;; {} => :kind/map    ;; PROBLEM
;;
;; [:div {} "hello"] => :kind/vector    ;; PROBLEM

;; ^:kind/hiccup [:div {} "hello"]
;; [:div [:div]] => :kind/vector ;; PROBLEM

;; Solution0(current): Avoid these kinds during Hiccup recursion
;; PROBLEM, user cannot set some kinds (maybe not a big problem)

;; Solution1.5: Not infer them inside hiccup only
;; PROBLEM: but now we still need to know is the kind requested by a user
;; Just check metadata? Leakage of metadata? Kindly job
;; (kind/user-annotated?)  EASIER

;; Absence of a kind is O.K.

;; Solution1: Kindly should not infer these kinds, only users can use them to disable other kinds
;; Solution2: Use some other method for users to remove kinds
;; Solution3: ???

;; These are kinds because we may wish to specify how they should be displayed
;; The answer to displaying them should be recursive

;; Hiccup is recursive and will stop at some kinds
;; In table recursion we do not avoid them.

;; ^:kind/table {:rows [{:a 1} {:b 2}]}

;; {}         ;; this should be treated as attrs in ^:kind/hiccup [:div {} "other"]
;; {} map     ;; this should be treated as attrs unless user specifically wanted it to show as a map
;; {} vega    ;; this should always be shown as vega
;; {} dataset ;; this should always be shown as vega

(defn kind
  "Detects kindly metadata indicating that a value is intended for transformation into hiccup."
  [x]
  (let [context (ka/advise {:value x})
        k (:kind context)]
    ;; TODO: Convince Daniel that :kind/vector :kind/set :kind/map :kind/seq shouldn't exist,
    ;; they are neither user annotated, nor helpful to tools.
    ;; What about :kind/hiccup? I think it's O.K.
    (and (not (contains? #{:kind/map :kind/vector :kind/seq} k))
         k)))

(def hiccup-schema
  "Enhances huff/hiccup-schema with the ability to detect kindly metadata,
  reagent forms, and scittle forms."
  [:schema
   {:registry
    {"hiccup" [:orn
               [:kindly [:fn {:error/message "should have kindly metadata"} kind]]
               [:primative [:or string? number? boolean? nil?]]
               [:fragment [:or
                           [:and seq?
                            [:catn [:children [:* [:schema [:ref "hiccup"]]]]]]
                           [:and vector?
                            [:catn [:fragment-indicator [:= :<>]]
                             [:children [:* [:schema [:ref "hiccup"]]]]]]]]
               [:tag-node [:and vector?
                           [:catn [:tag simple-keyword?]
                            [:attrs [:? [:and [:not [:fn kind]]
                                         [:schema [:ref "attrs"]]]]]
                            [:children [:* [:schema [:ref "hiccup"]]]]]]]
               [:raw-node [:and vector?
                           [:catn [:raw-indicator [:= :hiccup/raw-html]]
                            [:content string?]]]]
               [:component-node [:and vector?
                                 [:catn [:view-fxn [:and fn?
                                                    [:function [:=> [:cat any?]
                                                                [:schema [:ref "hiccup"]]]]]]
                                  [:children [:* any?]]]]]
               [:reagent-node [:and vector?
                               [:catn [:component [:or [:and seq? [:cat [:= 'fn] [:* any?]]]
                                                   symbol?]]
                                [:args [:* any?]]]]]
               [:scittle-node [:and vector?
                               [:catn [:forms [:+ seq?]]]]]]
     ;; TODO: attrs may be over restricted (why not accept anything string-able?) and under specified (only styles can be maps)
     "attrs"  [:map-of
               [:or string? keyword? symbol?]
               [:or string? keyword? symbol? number? boolean? nil? vector?
                [:schema [:ref "attrs"]]]]}}
   [:ref "hiccup"]])
(def valid? (m/validator hiccup-schema))
(def explainer (m/explainer hiccup-schema))
(def parser (m/parser hiccup-schema))

(defmethod emit :fragment [^StringBuilder sb [_ {:keys [children]}] opts]
  (doseq [c children]
    (emit sb c opts)))

(defmethod emit :reagent-node [^StringBuilder sb [_ {:keys [component args]}] opts]
  ;; TODO: need an id that is per project
  (prn "C" component args)
  ;; why is component a vector?? malli??

  (let [component (if (vector? component)
                    (seq component)
                    component)
        id (idg/gen-id)
        form (list 'dom/render (into [component] args) (list 'js/document.getElementById id))]
    (emit sb [:tag-node
              {:tag      :script
               :attrs    {:id   id
                          :type "application/x-scittle"}
               :children [[:raw-node {:content (pr-str form)}]]}]
          opts)))

(defmethod emit :scittle-node [^StringBuilder sb [_ {:keys [forms]}] opts]
  (emit sb [:tag-node
            {:tag      :script
             :attrs    {:type "application/x-scittle"}
             :children [[:raw-node {:content (pr-str forms)}]]}]
        opts))

(defmethod emit :kindly [^StringBuilder sb [_ x] _opts]
  (.append sb (html (ahiccup/adapt {:value x}))))

(comment
  ;; regular function
  (defn info [] [:div "this is some info"])
  (html [:div [:h2 "hi"] [info]])
  ;=> "<div><h2>hi</h2><div>this is some info</div></div>"

  ;; TODO: these error messages are no good,
  ;; maybe we could have an `any?` node and error in the dispatch instead.
  (html [:div [:h2 "hi"] (list info)])
  ;=> ERROR

  (html [:div [:h2 "hi"] '(info)])
  ;=> ERROR

  (html [:div {:style {:width "100%"}} [:script "test"]])

  (html [:div [:h2 "hi"] ['(fn [] [:div "I'm a reagent component!"])]])
  ;=> "<div><h2>hi</h2><script id=\"unscoped-6\" type=\"application/x-scittle\">(dom/render [(fn [[] [:div \"I'm a reagent component!\"]])] (js/document.getElementById \"unscoped-6\"))</script></div>"

  (html [:div [:h2 "hi"] ['foo/bar]])
  ;=> "<div><h2>hi</h2><script id=\"unscoped-7\" type=\"application/x-scittle\">(dom/render [foo/bar] (js/document.getElementById \"unscoped-7\"))</script></div>"

  )
