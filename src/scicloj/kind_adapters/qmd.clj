(ns scicloj.kind-adapters.qmd
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kind-adapters.hiccup]
            [hiccup2.core]))

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

(defn block [s language]
  (str "```" language \newline
       (str/trim-newline s) \newline
       "```"))

(defn block-quote [s]
  (->> (str/split-lines s)
       (map #(str "> " %))
       (str/join \newline)))

(defn message [s channel]
  (-> (str "**" channel "**" \newline \newline
           s)
      (block-quote)))

(defn result-block [s language]
  (str "<div class=\"printedClojure\">" \newline \newline
       (block s language) \newline \newline
       "</div>"))

(defn pprint-block [value]
  (-> (with-out-str (pprint/pprint value))
      (result-block "clojure")))

(defmethod adapt :kind/pprint [{:keys [kind value]}]
  (pprint-block value))

(defmethod adapt :kind/vec [{:keys [kind value]}]
  (pprint-block value))

(defmethod adapt :kind/seq [{:keys [kind value]}]
  (pprint-block value))

(defmethod adapt :kind/set [{:keys [kind value]}]
  (pprint-block value))

(defmethod adapt :kind/map [{:keys [kind value]}]
  (pprint-block value))

(defmethod adapt :default [{:as   context
                            :keys [kind value]}]
  (if kind
    (-> (scicloj.kind-adapters.hiccup/adapt context)
        (hiccup2.core/html))
    (pprint-block value)))
