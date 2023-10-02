(ns scicloj.claykind.main
  "This example converts any namespace in the `notebooks` directory
  into a markdown file in the `docs` directory.
  You could use this to publish a blog that works with markdown,
  or use Pandoc or Quarto to convert the markdown into HTML."
  (:require [clojure.edn :as edn]
            [clojure.tools.cli :as cli]
            [scicloj.claykind.api :as api]
            [scicloj.claykind.version :as version]
            [scicloj.read-kinds.read :as read]))

(set! *warn-on-reflection* true)

;; Similar to [pandoc options](https://pandoc.org/MANUAL.html#general-options),
;  To support working with a single configuration file.
;; TODO: Pandoc could be used to transform markdown to a common format

(def cli-options
  (let [{:keys [paths target-dir evaluator]} api/default-options]
    [["-f" "--formats FORMATS"
      :validate [api/formats (str "must be one or more of: " (cons "all" (keys api/formats)))]]
     ["-p" "--paths PATHS"
      :validate [sequential? (str "paths should be a sequence like " (pr-str paths))]
      :default-desc (pr-str paths)
      :parse-fn edn/read-string]
     ["-t" "--target-dir DIR" :default-desc target-dir]
     [nil "--evaluator [:clojure|:babashka]" :default-desc evaluator
      :parse-fn edn/read-string
      :validate [read/evaluators (str "must be one of " read/evaluators)]]
     [nil "--verbose"]
     [nil "--quiet"]
     ["-h" "--help"]
     ["-v" "--version"]]))

(defn -main
  "Invoke with `clojure -M:dev -m scicloj.claykind.main --help` to see options"
  [& args]
  (let [{:keys [options summary arguments errors]} (cli/parse-opts args cli-options)
        {:keys [help version]} options]
    (cond help (println "Claykind" \newline
                        "Version: " version/version \newline
                        "Description: Claykind evaluates Clojure namespaces into Markdown" \newline
                        "Options:" \newline
                        summary)
          version (println version/version)
          errors (do (println "ERROR:" errors)
                     (System/exit -1))
          :else (do (api/render! (merge (when (seq arguments)
                                          {:paths (vec arguments)})
                                        options))
                    (System/exit 0)))))

(comment
  (-main "--verbose"))
