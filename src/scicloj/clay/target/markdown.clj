(ns scicloj.clay.target.markdown
  (:require [clojure.string :as str]
            [scicloj.kind-adapters.markdown :as amd]))

(defn message [msg]
  (str ">" msg \newline))

(defn as-comments [leader s]
  (str \newline
       leader (->> (str/split-lines s)
                   (str/join (str \newline ";   "))) \newline))

(defn clojure-code [{:keys [code error value stdout stderr]}]
  (str "```clojure" \newline
       code \newline
       (when stdout
         (as-comments ";OUT " stdout))
       (when stderr
         (as-comments ";ERR " stderr))
       (when value
         (as-comments ";=> " (amd/adapt value)))
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

;; TODO: ways to control order... sort by metadata?
(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [contexts]} options]
  (->> (map render-md contexts)
    (str/join \newline)))
