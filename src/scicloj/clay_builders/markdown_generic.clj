(ns scicloj.clay-builders.markdown-generic
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.markdown :as amd]))

(defn message [msg]
  (str ">" msg \newline))

(defn as-comments [leader s]
  (str \newline
       leader (->> (str/split-lines s)
                   (str/join (str \newline ";   "))) \newline))

(defn clojure-code [{:keys [code error stdout stderr] :as context}]
  (str "```clojure" \newline
       code \newline
       (when stdout
         (as-comments ";OUT " stdout))
       (when stderr
         (as-comments ";ERR " stderr))
       (when (contains? context :value)
         (as-comments ";=> " (amd/adapt context)))
       "```" \newline
       (when error
         (str \newline
              (message error)))))

(defn render-md
  "Transforms advice into a Markdown string"
  [context]
  (let [{:keys [code kind]} context]
    (cond
      (= kind :kind/comment) (:kindly/comment context)
      (or (contains? context :value)
          (contains? context :error)) (clojure-code context)
      :else code)))

;; TODO: ways to control order... sort by metadata?
(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (mapv render-md contexts)
       (str/join \newline)))
