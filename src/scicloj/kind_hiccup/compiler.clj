(ns scicloj.kind-hiccup.compiler
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.to-hiccup :as ahiccup]
            [scicloj.kindly-advice.v1.api :as ka]))

(set! *warn-on-reflection* true)

(declare compile-hiccup)

(def primitive?
  (some-fn string? number? boolean? nil?))

(defn kind [x]
  ;; TODO: advise should indicate if user set or inferred
  (let [context (ka/advise {:value x})
        k (:kind context)]
    (and k
         (not (contains? #{:kind/vector :kind/set :kind/map :kind/seq :kind/hiccup} k))
         context)))

(def escapes
  {\& "&amp;"
   \< "&lt;"
   \> "&gt;"
   \" "&quot;"
   \\ "&#39;"})

;; TODO: but don't over quote!
(defn escape-quotes [s]
  (str/replace s "\"" "\\\""))

;; TODO: when to quote??
(defn stringify [x]
  (cond (nil? x) ""
        (string? x) (str/escape x escapes)
        (keyword? x) (str/escape (str (symbol x)) escapes)
        (ratio? x) (str (double x))
        (number? x) (str x)
        :else (str/escape (str x) escapes)))

(defn empty-or-div [seen]
  (if (empty? seen)
    "div"
    (str/join seen)))

;; TODO: do these protect against bad keywords?
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

(defn stringi [x]
  (cond (nil? x) ""
        (string? x) x
        (keyword? x) (subs (str x) 1)
        :else (str x)))

(defn attribute-name [s]
  (str/replace s #"[\s\u0000\"'>/=]" "-"))

(defn compile-attr-key [^StringBuilder sb k]
  (->> (stringi k)
       (attribute-name)
       (.append sb)))

(defn attr-value? [x]
  (not (or (contains? #{"" nil false} x)
           (and (coll? x) (empty? x)))))

(defn compile-attr-value [^StringBuilder sb x]
  (cond
    (string? x) (.append sb (escape-quotes x))
    (ratio? x) (.append sb (str (double x)))
    (number? x) (.append sb (str x))
    ;; {:rotate [1 2 3]} => rotate(1 2 3)
    (map? x) (let [started (volatile! false)]
               (doseq [[k v] x
                       :when (attr-value? v)]
                 (if @started
                   (.append sb " ")
                   (vreset! started true))
                 (compile-attr-key sb k)
                 (.append sb \()
                 (compile-attr-value sb v)
                 (.append sb \))))
    (coll? x) (doseq [k (interpose " " x)]
                (compile-attr-value sb k))
    :else (.append sb (escape-quotes (str x)))))

(defn compile-style-map [^StringBuilder sb m]
  (doseq [[k v] m]
    (compile-attr-key sb k)
    (.append sb \:)
    (compile-attr-value sb v)
    (when (and (number? v) (not (zero? v)))
      (.append sb "px"))
    (.append sb \;)))

(defn compile-attrs [^StringBuilder sb attrs]
  (doseq [[k value] attrs
          :when (attr-value? value)]
    (.append sb " ")
    (compile-attr-key sb k)
    (.append sb "=\"")
    (if (and (= k :style) (map? value))
      (compile-style-map sb value)
      (compile-attr-value sb value))
    (.append sb \")))

;; lifted from hiccup.compiler
(def void-tags
  "A list of elements that must be rendered without a closing tag."
  #{"area" "base" "br" "col" "command" "embed" "hr" "img" "input" "keygen"
    "link" "meta" "param" "source" "track" "wbr"})

(defn compile-tag-node [^StringBuilder sb tag attrs children]
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
        (compile-attrs sb attrs)
        (compile-attrs sb {:id tag-id :class (remove str/blank? tag-classes)}))
      (if (contains? void-tags (name tag))
        (.append sb " />")
        (.append sb ">")))
    (doseq [c children]
      (compile-hiccup sb c))
    (doseq [[tag] (reverse tag-infos')]
      (when-not (contains? void-tags (name tag))
        (.append sb "</")
        (.append sb (name tag))
        (.append sb ">")))))

(defn scittle [^StringBuilder sb ^String code]
  (.append sb "<script type=application/x-scittle>")
  (.append sb code)
  (.append sb "</script>"))

(defn reagent [^StringBuilder sb component args]
  ;; TODO: use a namespaced sequential id (see id-generator)
  (let [id (gensym)]
    (.append sb "<div id=\"")
    (.append sb id)
    (.append sb "\">")
    (.append sb "<script type=application/x-scittle>")
    (.append sb "(dom/render (js/document.getElementById \"")
    (.append sb id)
    (.append sb "\") [")
    (.append sb (pr-str (into [component] args)))
    (.append sb "])")
    (.append sb "</script>")))

(defn compile-hiccup [^StringBuilder sb hiccup]
  (if-let [kindly-context (kind hiccup)]
    (recur sb (ahiccup/adapt kindly-context))
    (cond (primitive? hiccup)
          (.append sb (stringify hiccup))

          ;; seqs are treated as fragments
          (seq? hiccup)
          (doseq [h hiccup]
            (compile-hiccup sb h))

          (vector? hiccup)
          (let [[tag & children] hiccup]
            (cond
              ;; explicit fragment
              (= tag :<>)
              (doseq [h children]
                (compile-hiccup sb h))

              ;; TODO: maybe check that the keyword is good too
              ;; normal hiccup expression
              (simple-keyword? tag)
              (let [[head & more] children
                    head-is-attrs (and (map? head)
                                       (not= (:kind (kind head)) :kind/map))
                    [attrs children] (if head-is-attrs
                                       [head more]
                                       [nil children])]
                (compile-tag-node sb tag attrs children))

              ;; Component style
              (fn? tag)
              (recur sb (apply tag children))

              ;; Reagent expressions [portal/show ...] or ['(fn [] ...)]
              (or (symbol? tag) (and (seq? tag) (= 'fn (first tag))))
              (reagent sb tag children)

              ;; Code expressions (not really needed? Why would you embed code in hiccup?)
              ;; Maybe there should be a :kind/scittle instead?
              (seq? tag)
              ;; TODO: preserve code instead
              (scittle sb (pr-str hiccup))

              ;; When there is a problem, we want to let the user know;
              ;; Helpful information may include:
              ;; * line/column location in their code
              ;; * The invalid part (or a summary of it if it's too big)
              ;; * The entire thing with the error highlighted (or a summary or a path if it is too big)
              :else
              (do (println "WARNING: Invalid tag:" tag)
                  ;; TODO: use meta of the vector if available
                  (.append sb (stringify hiccup)))))

          :else
          (do
            (println "WARNING: Invalid hiccup encountered:" hiccup)
            ;; TODO: use meta of parent if available
            (.append sb (stringify hiccup))))))
