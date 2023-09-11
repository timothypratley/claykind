(ns scicloj.clay.markdown
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]))

(defn message [msg]
  (str ">" msg \newline))

(defn clojure-code [{:keys [code error value]}]
  (str "```clojure" \newline
       code \newline
       (when value
         (str \newline
              ";=> " (->> (pprint/pprint value)
                          (with-out-str)
                          (str/split-lines)
                          (str/join (str \newline ";   "))) \newline))
       "```" \newline
       (when error
         (str \newline
              (message error)))))

(defn render-md
  "Transforms advice into a Markdown string"
  [advice]
  (let [{:keys [code kind]} advice]
    (cond
      (= kind :kind/comment) (:kindly/comment advice)
      (or (contains? advice :value)
          (contains? advice :error)) (clojure-code advice)
      :else code)))
