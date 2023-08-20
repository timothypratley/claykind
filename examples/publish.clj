(ns publish
  "This example converts any namespace in the `notebooks` directory
  into a markdown file in the `docs` directory.
  You could use this to publish a blog that works with markdown,
  or use Pandoc or Quarto to convert the markdown into HTML."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [scicloj.claykind.notes :as notes]))

(def cli-options
  [["-d" "--dirs" :default ["notebooks"]]
   ["-o" "--output-dir" :default "docs"]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn render-md
  "Transform the context into a string"
  [context]
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
