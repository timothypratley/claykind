(ns publish
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [scicloj.claykind.read :as read])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(def cli-options
  [["-d" "--dirs" :default ["notebooks"]]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn clojure-source? [^File file]
  (boolean
    (and (.isFile file)
         (re-matches #".*\.clj[cx]?$" (.getName file)))))

(defn render [x]
  ;; TODO:
  )

(defn note-to-html
  "Saves all the values into an edn file"
  [dirs options]
  (doseq [dir dirs
          file (file-seq (io/file dir))
          :when (clojure-source? file)]
    (->> (slurp file)
         (read/safe-eval-notes)
         (map render)
         (str/join \newline)
         (spit (str file "-values.edn")))))

(defn -main [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [dirs help]} options]
    (if help
      (println summary)
      (note-to-html dirs options))))

(comment
  (-main))
