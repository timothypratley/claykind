(ns scicloj.clay.publish
  "This example converts any namespace in the `notebooks` directory
  into a markdown file in the `docs` directory.
  You could use this to publish a blog that works with markdown,
  or use Pandoc or Quarto to convert the markdown into HTML."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [scicloj.read-kinds.api :as api]
            [scicloj.read-kinds.notes :as notes]
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
   ["-d" "--dirs DIRS"
    :parse-fn edn/read-string
    :default ["notebooks"]]
   ["-o" "--output-dir DIR" :default "docs"]
   ["-f" "--format FMT" :default "all"
    ;; TODO: Allow collection of formats
    #_#_:validate [formats (str "must be one or more of: " (cons "all" (keys formats)))]]
   ["-e" "--evaluator" :default :clojure
    :validate [read/evaluators (str "must be one of " read/evaluators)]]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn target [source extension {:keys [output-dir]}]
  (-> (str/replace source notes/clojure-file-ext-regex "")
      (str "." extension)
      (->> (io/file output-dir))))

(defn spit! [file content]
  (io/make-parents file)
  (spit file content))

;; TODO: needs to handle relative paths better
(defn render!
  ([{:keys [format] :as options}]
   (doseq [{:keys [file] :as notebook} (api/all-notebooks options)
           [ext format] (if (= format "all")
                          formats
                          (select-keys formats [format]))]
     (->> (format notebook options)
          (spit! (target file ext options)))))
  ([source {:keys [format] :as options}]
   (-> (api/notebook source)
       ((get formats format) options)
       (spit! (target (io/file source) format options)))))

(defn -main
  "Invoke with `clojure -M:dev scicloj.clay.publish --help` to see options"
  [& args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)
        {:keys [help]} options
        ;; TODO: take formats from cli
        #_#_formats (if (= "all" format)
                      (vals formats)
                      (if (coll? format)
                        (mapv formats format)
                        [(get formats format)]))]
    (if help
      (println summary)
      (render! options))))

(comment
  (-main))

(defn render [clj-path qmd-path]
  (let [options {:evaluator :babashka
                 :quarto    {:format
                             {:html {:toc   true
                                     :theme :spacelab}
                              ;; :revealjs {:theme :serif
                              ;;            :navigation-mode :vertical
                              ;;            :transition :slide
                              ;;            :background-transition :fade
                              ;;            :incremental true}
                              }
                             :highlight-style :solarized}}]
    (->> (qmd/notes-to-md {:contexts (api/notebook clj-path options)} options)
         (spit! qmd-path))))

;; Hard coding some things for now...
(defn render-babashka-example-book []
  (render "babashka-example-book/notebooks/bb.clj"
          "babashka-example-book/chapters/bb.qmd")
  (render "babashka-example-book/notebooks/bb1.clj"
          "babashka-example-book/chapters/bb1.qmd")
  (render "babashka-example-book/notebooks/git_log_analysis.clj"
          "babashka-example-book/chapters/git-log-analysis.qmd"))

(comment
  (render-babashka-example-book))

;; then run `quarto preview` in the babashka-example-book directory
;; and view it at https://timothypratley.github.io/claykind/babashka-example-book/_book


;; TODO: needs to handle relative paths better
#_(defn render-babashka-example-book []
    (render! {:dirs       ["babashka-example-book/notebooks"]
              :output-dir "babashka-example-book/chapters"
              :format     "qmd"
              ;; TODO: probably we want this data to live somewhere else (maybe a config file)
              ;; and it should be the default, so we don't need to specify it
              :quarto     {:format
                           {:html {:toc   true
                                   :theme :spacelab}
                            ;; :revealjs {:theme :serif
                            ;;            :navigation-mode :vertical
                            ;;            :transition :slide
                            ;;            :background-transition :fade
                            ;;            :incremental true}
                            }
                           :highlight-style :solarized}}))


;; We can control selecting the input files and output directory (and one day format)
;; clojure -M:dev -m scicloj.clay.publish --dirs ["some"] --output-dir book
;;
(comment
  (-main "--dirs" "[\"notebooks/babashka\"]" "--output-dir" "babashka-example-book/chapters"))

;; To render just one file (maybe we can add a keybinding to use current file one day)
(comment
  (render! "notebooks/babashka/bb.clj"
           {:output-dir "babashka-example-book/chapters"
            :format     "qmd"}))
