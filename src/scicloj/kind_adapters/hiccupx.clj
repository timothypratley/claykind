(ns scicloj.kind-adapters.hiccupx
  "Derived from io.github.escherize/huff"
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [malli.core :as m]
            [scicloj.kind-adapters.to-hiccup :as ahiccup]
            [scicloj.kindly-advice.v1.api :as ka]))

(def ^:dynamic *escape?* true)

(set! *warn-on-reflection* true)

(defn kind [x]
  (let [context (ka/advise {:value x})
        k (:kind context)]
    ;; TODO: Convince Daniel these kinds shouldn't exist,
    ;; they are neither user annotated, nor tool helpful.
    (and k
         (not (contains? #{:kind/vector :kind/set :kind/map :kind/seq} k))
         context)))

(def hiccup-schema
  [:schema
   {:registry
    {"hiccup" [:or
               [:orn
                [:kindly [:fn kind]]
                [:primitive [:or string? number? boolean? nil?]]
                [:fragment [:and seq?
                            [:catn [:children [:* [:schema [:ref "hiccup"]]]]]]]]
               [:schema [:ref "node"]]]
     ;; TODO: attrs may be over restricted (why not accept anything stringable?) and under specified (only styles can be maps)
     "attrs"  [:map-of
               [:or string? keyword? symbol?]
               [:or string? keyword? symbol? number? boolean? nil? vector?
                [:schema [:ref "attrs"]]]]
     "node"   [:and vector?
               [:orn
                [:fragment [:catn [:fragment-indicator [:= :<>]]
                            [:children [:* [:schema [:ref "hiccup"]]]]]]
                [:tag-node [:catn [:tag simple-keyword?]
                            [:attrs [:? [:and [:not [:fn kind]]
                                         [:schema [:ref "attrs"]]]]]
                            [:children [:* [:schema [:ref "hiccup"]]]]]]
                [:raw-node [:catn [:raw-indicator [:= :hiccup/raw-html]]
                            [:content string?]]]
                [:component-node [:catn [:view-fn [:and fn?
                                                   [:function [:=> [:cat any?]
                                                               [:schema [:ref "hiccup"]]]]]]
                                  [:children [:* any?]]]]
                [:reagent-node [:catn [:component [:or [:and list? [:cat [:= 'fn]]]
                                                   symbol?]]
                                [:args [:* any?]]]]
                [:scittle-node [:catn [:forms [:+ list?]]]]]]}}
   "hiccup"])

(def valid? (m/validator hiccup-schema))
(def explainer (m/explainer hiccup-schema))
(def parser (m/parser hiccup-schema))

(defn stringify
  "Take a primitive, and turn it into a string."
  [x]
  (cond (nil? x) ""
        (string? x) x
        (keyword? x) (str (symbol x))
        (ratio? x) (str (double x))
        (vector? x) (str/join " " x)
        :else (str x)))

(def escape
  {\& "&amp;"
   \< "&lt;"
   \> "&gt;"
   \" "&quot;"
   \\ "&#39;"})

(defn maybe-escape-html
  "1. Change special characters into HTML character entities when *escape?* otherwise don't change text.
   2. Append the maybe-transformed text value onto a stringbuilder"
  [^StringBuilder sb x]
  (let [text-str (stringify x)]
    (if (or (not *escape?*) (not (some escape text-str)))
      (.append sb text-str)
      (doseq [c text-str]
        (.append sb (escape c c))))))

(defmulti emit (fn [_sb [tag]] tag))

(defmethod emit :primitive [sb [_tag x] _opts]
  (maybe-escape-html sb x))

(defn- empty-or-div [seen]
  (if (empty? seen)
    "div"
    (str/join seen)))

(defn- emit-style [^StringBuilder sb s]
  (.append sb "style=\"")
  (cond
    (map? s) (doseq [[k v] (sort-by first s)]
               [(.append sb (stringify k))
                (.append sb ":")
                (.append sb (stringify v))
                (when (number? v)
                  (.append sb "px"))
                (.append sb ";")])
    (string? s) (.append sb s)
    :else (throw (ex-info "style attributes need to be a string or a map."
                          {:id ::invalid-style-attribute
                           :s  s})))
  (.append sb "\""))

(defn step
  "Used to extract :.class.names.and#ids from keywords."
  [{:keys [mode seen] :as acc} char]
  (case mode
    :tag (cond (= char \#) (assoc acc :tag (empty-or-div seen) :seen [] :mode :id)
               (= char \.) (assoc acc :tag (empty-or-div seen) :seen [] :mode :class)
               :else (update acc :seen conj char))
    :id (cond (= char \#) (throw (ex-info "can't have 2 #'s in a tag."
                                          {:id  ::illegal-tag
                                           :acc acc}))
              (= char \.) (assoc acc :id (str/join seen) :seen [] :mode :class) :else (update acc :seen conj char))
    :class (cond (= char \#) (-> acc
                                 (update :class (fn [c] (cond-> c (not-empty seen) (conj (str/join seen)))))
                                 (assoc :seen [] :mode :id))
                 (= char \.) (-> acc
                                 (update :class (fn [c] (cond-> c (not-empty seen) (conj (str/join seen)))))
                                 (assoc :seen [] :mode :class))
                 :else (update acc :seen conj char))))

(defn tag->tag+id+classes* [tag]
  (-> (reduce step
              {:mode :tag :class [] :seen [] :id nil}
              (name tag))
      (step \.)                                             ;; move "seen " into the right place
      (map [:tag :id :class])))

(defn tag->tag+id+classes [tag]
  (mapv (comp tag->tag+id+classes* keyword) (str/split (name tag) #">")))

(defn emit-attrs [^StringBuilder sb attrs]
  (doseq [[k value] attrs]
    (when-not
      (or (contains? #{"" nil false} value)
          (and (coll? value) (empty? value)))
      (.append sb " ")
      (cond
        (= :style k)
        (emit-style sb value)

        (coll? value)
        (do (.append sb (stringify k))
            (.append sb "=\"")
            (doseq [x (interpose " " value)] (maybe-escape-html sb x))
            (.append sb "\""))

        :else
        (do (.append sb (stringify k))
            (.append sb "=\"")
            (maybe-escape-html sb value)
            (.append sb "\""))))))

;; lifted from hiccup.compiler
(def void-tags
  "A list of elements that must be rendered without a closing tag."
  #{"area" "base" "br" "col" "command" "embed" "hr" "img" "input" "keygen"
    "link" "meta" "param" "source" "track" "wbr"})

(defmethod emit :tag-node [^StringBuilder sb [_ {:keys [tag attrs children]}] opts]
  (let [tag-infos (tag->tag+id+classes tag)
        [_final-tag final-tag-id final-tag-classes] (last tag-infos)
        attrs (-> attrs
                  (update :id #(or % final-tag-id))
                  (update :class #(->>
                                    (cond
                                      (string? %) (concat [%] final-tag-classes)
                                      (coll? %) (concat % final-tag-classes)
                                      (nil? %) final-tag-classes)
                                    (remove str/blank?))))
        ;; attrs go on the last tag-info:
        tag-infos' (update tag-infos (dec (count tag-infos)) (fn [l] (conj (vec l) attrs)))]
    (doseq [[tag tag-id tag-classes & [attrs]] tag-infos']
      (.append sb "<")
      (.append sb (name tag))
      (if attrs
        (emit-attrs sb attrs)
        (emit-attrs sb {:id tag-id :class (remove str/blank? tag-classes)}))
      (if (contains? void-tags (name tag))
        (.append sb " />")
        (.append sb ">")))
    (doseq [c children] (emit sb c opts))
    (doseq [[tag] (reverse tag-infos')]
      (when-not (contains? void-tags (name tag))
        (.append sb "</")
        (.append sb (name tag))
        (.append sb ">")))))

(defmethod emit :raw-node [^StringBuilder sb [_ {:keys [content]}] {:keys [allow-raw]}]
  (when-not allow-raw
    (throw (ex-info ":hiccup/raw-html is not allowed. Maybe you meant to pass {:allow-raw true} as options?"
                    {:id        ::raw-not-allowed
                     :content   content
                     :allow-raw allow-raw})))
  (.append sb content))

(defmethod emit :fragment [^StringBuilder sb [_ {:keys [children]}] opts]
  (doseq [c children]
    (emit sb c opts)))

(defmethod emit :siblings-node [^StringBuilder sb [_ {:keys [children]}] opts]
  (doseq [c children]
    (emit sb c opts)))

(defmethod emit :component-node [^StringBuilder sb [_ {:keys [view-fn children]}] opts]
  (emit sb (parser (apply view-fn children)) opts))

(defn scittle [^StringBuilder sb forms]
  (.append sb "<script type=\"application/x-scittle\">")
  (doseq [form forms]
    (binding [pprint/*print-pprint-dispatch* pprint/code-dispatch]
      (.append sb (with-out-str (pprint/pprint form)))))
  (.append sb "</script>"))

(defmethod emit :reagent-node [^StringBuilder sb [_ {:keys [component args]}] _opts]
  (->> [(list 'js/reagent.dom.mount (list '.getParent) (into [component] args))]
       (scittle sb)))

(defmethod emit :scittle-node [^StringBuilder sb [_ {:keys [forms]}] _opts]
  (scittle sb forms))

(declare html)

(defmethod emit :kindly [^StringBuilder sb [_ x] _opts]
  (.append sb (html (ahiccup/adapt {:value x}))))

(defn html
  ([hiccup] (html hiccup {}))
  ([hiccup opts]
   (let [parsed (parser hiccup)]
     (if (= parsed :malli.core/invalid)
       (let [{:keys [errors]} (explainer hiccup)]
         (throw (ex-info "Invalid form passed to html. See `hiccup-schema` for more info."
                         {:id     ::invalid
                          :value  hiccup
                          :errors errors})))
       (let [sb (StringBuilder.)]
         (emit sb parsed opts)
         (str sb))))))

(comment
  (html "hello")
  => "hello"

  (html [:div "hello"])
  => "<div>hello</div>"

  (html [:div '("hi" "bye")])
  => "<div>hibye</div>"

  (html [:div ^{:kindly/kind :kind/pprint} {:foo "bar" :baz 1}])
  => "Kindly!!" {:foo "bar", :baz 1} :kind/pprint

  (html [:div ['(fn [] [:div "I'm in reagent!"])]])
  => "<div><script type=\"application/x-scittle\">(fn [] [:div \"I'm in reagent!\"])</script></div>"

  (html [:div ['(let [x 1] (prn x)) '(println "I'm in scittle!")]])
  => "<div><script type=\"application/x-scittle\">(let [x 1] (prn x))(println \"I'm in scittle!\")</script></div>"
  )

(defn page
  ([hiccup] (page hiccup {}))
  ([hiccup opts] (str "<!doctype html>" (html hiccup opts))))
