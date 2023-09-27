(ns scicloj.clay-builders.markdown-generic
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.markdown :as amd]))

(defn message [msg]
  (str ">" msg \newline))

(defn as-comments [leader s]
  (str \newline
       leader (->> (str/split-lines s)
                   (str/join (str \newline ";   "))) \newline))

(defn clojure-code [{:keys [code exception out err] :as context}]
  (str "```clojure" \newline
       code \newline
       (when out
         (as-comments ";OUT " out))
       (when err
         (as-comments ";ERR " err))
       (when (contains? context :value)
         (as-comments ";=> " (amd/adapt context)))
       "```" \newline
       (when exception
         (str \newline
              (message exception)))))

(defn render-md
  "Transforms advice into a Markdown string"
  [context]
  (let [{:keys [code kind]} context]
    (cond
      (= kind :kind/comment) (:kindly/comment context)
      (or (contains? context :value)
          (contains? context :exception)) (clojure-code context)
      :else code)))

;; TODO: ways to control order... sort by metadata?
(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (mapv render-md contexts)
       (str/join (str \newline \newline))))
