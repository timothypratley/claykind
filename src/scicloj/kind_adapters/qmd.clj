(ns scicloj.kind-adapters.qmd
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kind-adapters.hiccup]
            [hiccup.core]))

;; Adapters take a context and produce a representation,
;; for example hiccup, markdown, or portal-annotated values.

(defmulti adapt :kind)

(defmethod adapt :kind/table [{:keys [value]}]
  (let [{:keys [headers rows]} value]
    (str (str/join "|" headers)
         "----"
         (str/join \newline
                   (for [row rows]
                     (str/join "|" row))))))

(defn as-printed-clj [s]
  (str "<div class=\"printedClojure\">" \newline
       "```clojure" \newline
       s \newline
       "```" \newline
       "</div>" \newline))

(defn pprint-as-printed-clj [value]
  (-> value
      pprint/pprint
      with-out-str
      as-printed-clj))

(defmethod adapt :kind/pprint [{:keys [kind value]}]
  (pprint-as-printed-clj value))

(defmethod adapt :kind/vec [{:keys [kind value]}]
  (pprint-as-printed-clj value))

(defmethod adapt :kind/seq [{:keys [kind value]}]
  (pprint-as-printed-clj value))

(defmethod adapt :kind/set [{:keys [kind value]}]
  (pprint-as-printed-clj value))

(defmethod adapt :kind/map [{:keys [kind value]}]
  (pprint-as-printed-clj value))

(defmethod adapt :default [{:as context
                            :keys [kind value]}]
  (if kind
    (-> context
        scicloj.kind-adapters.hiccup/adapt
        hiccup.core/html)
    (pprint-as-printed-clj value)))
