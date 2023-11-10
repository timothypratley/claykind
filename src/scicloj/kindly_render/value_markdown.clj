(ns scicloj.kindly-render.value-markdown
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [hiccup.core :as hiccup]
            [scicloj.kindly-render.from-markdown :as from-markdown]
            [scicloj.kindly-render.value-hiccup :as value-hiccup]
            [scicloj.read-kinds.notes :as notes]))

(defmulti adapt :kind)

;; TODO: might not be necessary??
;; Don't show
(defmethod adapt :kind/hidden [context options])

(defmethod adapt :kind/code [{:keys [code]} options]
  code)

(defmethod adapt :kind/md [{:keys [value]} options]
  (from-markdown/normalize-md value))

(defn html [context options]
  (-> (value-hiccup/adapt context options)
      (hiccup/html)))

(defn divide [xs options]
  ;; Calling html here makes everything inside the table HTML
  (str "| " (str/join " | " (map #(html (notes/derefing-advise {:value %}) options)
                                 xs))
       " |"))

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
           (block s ""))
      (block-quote)))

;; There are several potential ways to print values:
;; ```edn
;; ```clojure {.printedClojure}
;; pandoc removes {.printedClojure}
;; ```clojure class=printedClojure
;; >```clojure
;; <pre><code>...</code></pre>

(defn result-block [value {:keys [flavor]}]
  (-> (block value (if (= flavor "gfm")
                     "clojure"
                     "clojure {.printedClojure}"))
      (block-quote)))

(defn result-pprint [value options]
  (result-block (binding [*print-meta* true]
                  (with-out-str (pprint/pprint value)))
                options))

(defmethod adapt :kind/pprint [{:keys [value]} options]
  (result-pprint value options))

;; Don't show vars
(defmethod adapt :kind/var [{:keys [value]} options])

(comment
  ;; leaving collections to html to allow nesting consistently

  (defn adapt-value [context options]
    (let [{:keys [value]} context]
      (if (nested-kinds? value)
        (html context options)
        (result-pprint value options))))

  (defmethod adapt :kind/vec [context options]
    (adapt-value context options))

  (defmethod adapt :kind/seq [{:keys [value]} options]
    (result-pprint value options))

  (defmethod adapt :kind/set [{:keys [value]} options]
    (result-pprint value options))

  (defmethod adapt :kind/map [{:keys [value]} options]
    (result-pprint value options)))

(defmethod adapt :default [context options]
  ;; TODO: some kinds require JavaScript...
  ;; falling through is probably not great?
  (html context options))
