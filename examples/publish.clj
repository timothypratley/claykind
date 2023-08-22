(ns publish
  "This example converts any namespace in the `notebooks` directory
  into a markdown file in the `docs` directory.
  You could use this to publish a blog that works with markdown,
  or use Pandoc or Quarto to convert the markdown into HTML."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [scicloj.claykind.notes :as notes]
            [scicloj.claykind.read :as read]
            [scicloj.kindly-default.v1.api :as kindly]))

(kindly/setup!)

(def cli-options
  [["-d" "--dirs" :default ["notebooks"]]
   ["-o" "--output-dir" :default "docs"]
   ["-e" "--evaluator" :default :clojure
    :validate [read/evaluators (str "must be one of " read/evaluators)]]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn render-md
  "Transform the context into a string"
  [context]
  (let [{:keys [code kind value error]} context]
    (cond
      (= kind :kind/comment) (:kindly/comment context)
      error (str error)
      (contains? context :value)
      (str "```clojure" \newline
           code
           \newline "```" \newline
           "```" \newline "=>" \newline
           value
           \newline "```" \newline)
      :else code)))

(defn target [source extension {:keys [output-dir]}]
  (io/file output-dir (str source extension)))

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

(defn notes-to-md
  "Creates a markdown file from a notebook"
  [{:keys [file contexts]} options]
  (->> contexts
       ;; TODO: ways to control order
       ;;(reverse)
       (map render-md)
       (str/join \newline)
       (spit! (target file ".md" options))))

(defn -main
  "Invoke with `clojure -M:dev publish --help` to see options"
  [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [dirs help]} options]
    (if help
      (println summary)
      (doseq [notebook (notes/all-notes dirs)]
        (notes-to-md notebook options)))))

(comment
  (-main))
