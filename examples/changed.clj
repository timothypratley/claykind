(ns changed
  (:require [clojure.java.io :as io]
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

;; TODO: generate a test ns per notebook (it won't change)
(def test-template
  "(deftest my-test
  (is (= (edn/read-string (slurp %s))
         (read/safe-eval-notes %s))))
  ")

(defn note-to-values
  "Saves all the values into an edn file"
  [dirs options]
  (doseq [dir dirs
          file (file-seq (io/file dir))
          :when (clojure-source? file)]
    (->> (slurp file)
         (read/safe-eval-notes)
         (filter (comp #{:kindly/value} :kind))
         (mapv :kindly/value)
         (spit (str file "-values.edn")))))

(defn -main [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [dirs help]} options]
    (if help
      (println summary)
      (note-to-values dirs options))))

(comment
  (-main))
