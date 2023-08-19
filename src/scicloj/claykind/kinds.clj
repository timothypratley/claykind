(ns scicloj.claykind.kinds
  "Kinds are advice for how to visualize values that will be passed to downstream tools.")

(def primitive?
  (some-fn nil? number? string? symbol? keyword?))

(defn infer-kind
  "Provides advice for a context.
  Advice is a Kindly representation of a value."
  [context]
  (let [{:keys [value form]} context
        [kind representation]
        (cond
          (primitive? value)
          [:kindly/value value]

          (coll? value)
          [:kindly/value value]

          (fn? value)
          [:kindly/function form]

          ;; The default handles Objects that cannot be compared or displayed
          ;; e.g.: vars
          :default
          [:kindly/value (str value)])]
    (assoc context
      :kind kind
      kind representation))

  ;; TODO: is this :kind or :kindly/kind ?
  ;; perhaps representation is another thing, and it may need some user adjustment

  ;; TODO: can we make use of [:kindly/type ...] maybe
  ;; :kindly/representation [:kindly/something ...]

  )
