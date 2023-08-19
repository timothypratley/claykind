(ns scicloj.claykind.kinds)

every-pred

(def primitive?
  (some-fn nil? number? string? symbol? keyword?))

(defn infer-kind
  "Provides advice for a context.
  Advice is a Kindly representation of a value."
  [context]
  (let [{:keys [value]} context
        [kind representation]
        (cond
          (primitive? value)
          [:kindly/value value]

          (coll? value)
          [:kindly/value value]

          :default
          [:kindly/value (str value)])]
    (assoc context
      :kind kind
      kind representation))

  ;; TODO: is this :kind or :kindly/kind ?
  ;; TODO: vars should be represented as strings for comparison, etc
  ;; perhaps representation is another thing, and it may need some user adjustment

  ;; TODO: can we make use of [:kindly/type ...] maybe
  ;; :kindly/representation [:kindly/something ...]

  )
