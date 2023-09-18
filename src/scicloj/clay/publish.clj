(ns scicloj.clay.publish
  "This example converts any namespace in the `notebooks` directory
  into a markdown file in the `docs` directory.
  You could use this to publish a blog that works with markdown,
  or use Pandoc or Quarto to convert the markdown into HTML."
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :as cli]
            [scicloj.read-kinds.api :as api]
            [scicloj.read-kinds.read :as read]
            [scicloj.clay-builders.html-plain :as html]
            [scicloj.clay-builders.markdown-generic :as md]
            [scicloj.clay-builders.markdown-quarto :as qmd]
            [scicloj.clay-builders.html-portal :as hp]))

;; TODO: is there a nicer way? Should this be a `format` protocol?
(def formats
  {"html" html/notes-to-html
   "md"   md/notes-to-md
   "qmd"  qmd/notes-to-md
   "htm"  hp/notes-to-html-portal})

(def cli-options
  [["-i" "--input-file"
    ;; TODO: work with a single input file
    :validate [#(-> % (io/file) (.exists)) "file does not exist"]]
   ["-d" "--dirs" :default ["notebooks"]]
   ["-o" "--output-dir" :default "docs"]
   ["-f" "--format" :default "all"
    ;; TODO: Allow collection of formats
    #_#_:validate [formats (str "must be one or more of: " (cons "all" (keys formats)))]]
   ["-e" "--evaluator" :default :clojure
    :validate [read/evaluators (str "must be one of " read/evaluators)]]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn target [source extension {:keys [output-dir]}]
  (io/file output-dir (str source "." extension)))

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

(defn -main
  "Invoke with `clojure -M:dev publish --help` to see options"
  [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [help dirs format]} options
        ;; TODO: take formats from cli
        #_#_formats (if (= "all" format)
                  (vals formats)
                  (if (coll? format)
                    (mapv formats format)
                    [(get formats format)]))]
    (if help
      (println summary)
      (doseq [{:keys [file] :as notebook} (api/all-notebooks dirs)
              [ext format] formats]
        (->> (format notebook options)
             (spit! (target file ext options)))))))

(comment
  (-main))

(defn render [clj-path qmd-path]
  (->> clj-path
       api/notebook
       (#(qmd/notes-to-md
          {:contexts %}
          {:format
           {:html {:toc true
                   :theme :spacelab}
            ;; :revealjs {:theme :serif
            ;;            :navigation-mode :vertical
            ;;            :transition :slide
            ;;            :background-transition :fade
            ;;            :incremental true}
            }
           :highlight-style :solarized}))
       (spit qmd-path)))

(defn run! []
  (render "notebooks/babashka/bb.clj" "book/chapters/bb.qmd")
  (render "notebooks/babashka/bb1.clj" "book/chapters/bb1.qmd"))

(comment
  (run!))
