(ns scicloj.kind-adapters.to-markdown
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kind-adapters.to-hiccup :as to-hiccup]
            [scicloj.kind-adapters.to-html :as to-html]))

(defmulti adapt :kind)

(defmethod adapt :kind/table [{:keys [value]} options]
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

;; There are several potential ways to print values:
;; ```edn
;; ```clojure {.printedClojure}
;; pandoc removes {.printedClojure}
;; ```clojure class=printedClojure
;; >```clojure
;; <pre><code>...</code></pre>

;; TODO: how to specify a flavor?
(defn pprint-block [value {:keys [flavor]}]
  (cond-> (with-out-str (pprint/pprint value))
          true (block (if (= flavor "gfm")
                        "clojure"
                        "clojure {.printedClojure}"))
          true (block-quote)))

(defmethod adapt :kind/pprint [{:keys [value]} options]
  (pprint-block value options))

(defmethod adapt :kind/code [{:keys [code]} options]
  code)

(defmethod adapt :kind/vec [{:keys [value]} options]
  (pprint-block value options))

(defmethod adapt :kind/seq [{:keys [value]} options]
  (pprint-block value options))

(defmethod adapt :kind/set [{:keys [value]} options]
  (pprint-block value options))

(defmethod adapt :kind/map [{:keys [value]} options]
  (pprint-block value options))

(defmethod adapt :default [{:as   context
                            :keys [kind value]} options]
  (if kind
    (-> (to-hiccup/adapt context)
        (to-html/html))
    (pprint-block value options)))
