(ns scicloj.clay.api
  (:require [clojure.pprint :as pprint]
    ;; TODO: abstract builders as write?
            [scicloj.clay-builders.html-plain :as html]
            [scicloj.clay-builders.html-portal :as hp]
            [scicloj.clay-builders.markdown-generic :as md]
            [scicloj.clay-builders.markdown-quarto :as qmd]
            [scicloj.clay.io :as io]
            [scicloj.clay.io :as clay.io]
            [scicloj.clay.version :as version]
            [scicloj.read-kinds.api :as read-kinds]))

(def default-options
  {:formats          [["markdown" "pandoc" ".md"]]
   :paths            ["notebooks"]
   :target-dir       "docs"
   :fail-if-warnings false})

;; TODO: is there a nicer way? Should this be a `format` protocol?
(def formats
  {"html" html/notes-to-html
   ;; TODO: allow users to choose the flavor of markdown
   ;; maybe pandoc can convert between flavors?
   "md"   md/notes-to-md
   "qmd"  qmd/notes-to-md
   "htm"  hp/notes-to-html-portal})

(defn spy [x]
  (prn "X" x)
  x)

(defn- render* [path {:keys [verbose] :as options}]
  (when verbose
    (println "Rendering" (str path)))
  (let [builder qmd/notes-to-md #_(get formats format)]
    (-> (read-kinds/notebook path options)
        (builder options)
        (->> (clay.io/spit! (clay.io/target path "md" options))))))

;; TODO: needs to handle relative paths better
(defn render!
  "Renders Clojure source as Markdown.
  Options may be provided from a file `claykind.edn` in the project root,
  or passed in explicitly. Where both exist, options will be merged in the following order:
  1. default-options
  2. file configuration
  3. passed options
  See scicloj.main/cli-options for more information about available options."
  ([] (render! {}))
  ([options]
   (let [options (merge default-options
                        (clay.io/find-config)
                        options)
         {:keys [verbose paths]} options
         files (io/clojure-files paths)]
     (when verbose
       (println "Claykind" version/version "render options:")
       (pprint/pprint options)
       (println "Found" (count files) "source files to render."))
     (doseq [file files]
       (render* file options))
     (when verbose
       (println "Done."))))
  ([path options]
   (render* path (merge default-options
                        (clay.io/find-config)
                        options))))
