(ns publish
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [scicloj.claykind.notes :as notes])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(def cli-options
  [["-d" "--dirs" :default ["notebooks"]]
   ["-o" "--output-dir" :default "docs"]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-matches #".*\.clj[cx]?$" (.getName file)))))

(defn render-md [context]
  (let [{:keys [code form kind value]} context]
    (cond
      (= kind :kindly/comment) (:kindly/comment context)
      ;; TODO: should always have a form if not a comment
      (and form kind value)
      (str "```clojure" \newline
           code
           \newline "```" \newline
           "```" \newline "=>" \newline
           (get context kind)
           \newline "```")
      :else code)))

(defn target [source extension {:keys [output-dir]}]
  (io/file output-dir (str source extension)))

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

(defn notes-to-md
  "Saves all the values into an edn file"
  [dirs options]
  (doseq [dir dirs
          file (file-seq (io/file dir))
          :when (clojure-source? file)]
    (->> (slurp file)
         (notes/safe-eval-notes)
         ;; TODO: ways to control order
         ;;(reverse)
         (map render-md)
         (str/join (str \newline \newline))
         (spit! (target file ".md" options)))))

;; TODO: differentiate
(def render-html render-md)

(defn notes-to-html
  "Saves all the values into an edn file"
  [dirs options]
  (doseq [dir dirs
          file (file-seq (io/file dir))
          :when (clojure-source? file)]
    (->> (slurp file)
         (notes/safe-eval-notes)
         ;;(reverse)
         (map render-html)
         (str/join (str \newline \newline))
         (spit! (target file ".html" options)))))

(defn -main [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [dirs help]} options]
    (if help
      (println summary)
      (do (notes-to-md dirs options)
          (notes-to-html dirs options)))))

(comment
  (-main))
