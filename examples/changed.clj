(ns changed
  "This example demonstrates a process to collect all the computed values in a notebook,
  ignoring comments.
  This might be useful for generating a test that only cares if the computed result was changed,
  perhaps by an upstream dependency affecting the result we calculated."
  (:require [clojure.tools.cli :as cli]
            [scicloj.claykind.notes :as notes]))

(set! *warn-on-reflection* true)

(def cli-options
  [["-d" "--dirs" :default ["notebooks"]]
   ["-v" "--verbose"]
   ["-h" "--help"]])

;; TODO: generate a test ns per notebook (it won't change)
(def test-template
  "(deftest my-test
  (is (= (edn/read-string (slurp %s))
         (read/safe-eval-notes %s))))
  ")

(def values-xform
  "Transducer to filter and select only values from contexts."
  (comp
    (filter (comp #{:kindly/value} :kind))
    (map :kindly/value)))

(defn note-to-values
  "Saves all the values into an edn file"
  [{:keys [dirs]}]
  (doseq [{:keys [file contexts]} (notes/all-notes dirs)]
    (spit (str file "-values.edn")
          (into [] values-xform contexts))))

(defn -main [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [help]} options]
    (if help
      (println summary)
      (note-to-values options))))

(comment
  (-main))
