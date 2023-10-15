(ns scicloj.kind-adapters.to-markdown
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [scicloj.kind-adapters.to-hiccup :as to-hiccup]
            [scicloj.kind-hiccup.api :as kind-hiccup]
            [scicloj.kindly-advice.v1.api :as advice]))

(defmulti adapt :kind)

;; TODO: not all markdown is nestable
;; idea: note when inside a table, and behave accordingly
;; or create HTML in those situations
;; Main issue is that blocks can't exist inside tables

(defn html [context options]
  (-> (to-hiccup/adapt context)
      (kind-hiccup/html)))

(defn divide [xs options]
  ;; CHOICE: this makes everything inside the table HTML,
  ;; which is probably great...?
  (str "| " (str/join " | " (map #(html (advice/advise {:value %}) options)
                                 xs)) " |"))

(defmethod adapt :kind/table [{:keys [value]} options]
  (let [{:keys [column-names row-vectors]} value]
    (str (divide column-names options) \newline
         (divide (repeat (count column-names) "----") options) \newline
         (str/join \newline
                   (for [row row-vectors]
                     (divide row options))))))

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

;; TODO: only want blocks as results
(defmethod adapt :default [{:as   context
                            :keys [kind value]} options]
  (if kind
    (html context options))
    value
    #_(pprint-block value options))
