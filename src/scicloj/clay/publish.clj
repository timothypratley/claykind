(ns scicloj.clay.publish
  "This example converts any namespace in the `notebooks` directory
  into a markdown file in the `docs` directory.
  You could use this to publish a blog that works with markdown,
  or use Pandoc or Quarto to convert the markdown into HTML."
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :as cli]
            [scicloj.read-kinds.api :as api]
            [scicloj.read-kinds.read :as read]
            [scicloj.clay.html :as html]
            [scicloj.clay.markdown :as md]))

(def cli-options
  [["-d" "--dirs" :default ["notebooks"]]
   ["-o" "--output-dir" :default "docs"]
   ["-e" "--evaluator" :default :clojure
    :validate [read/evaluators (str "must be one of " read/evaluators)]]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn target [source extension {:keys [output-dir]}]
  (io/file output-dir (str source extension)))

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

(defn -main
  "Invoke with `clojure -M:dev publish --help` to see options"
  [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [dirs help]} options]
    (if help
      (println summary)
      (doseq [{:keys [file] :as notebook} (api/all-notebooks dirs)]
        (->> (md/notes-to-md notebook options)
             (spit! (target file ".md" options)))
        (->> (html/notes-to-html notebook options)
             (spit! (target file ".html" options)))))))

(comment
  (-main))
