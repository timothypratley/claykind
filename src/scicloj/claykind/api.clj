(ns scicloj.claykind.api
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
    ;; TODO: abstract builders as write?
            [scicloj.clay-builders.html-plain :as html]
            [scicloj.clay-builders.html-portal :as hpp]
            [scicloj.clay-builders.markdown-page :as mdp]
            [scicloj.claykind.io :as clay.io]
            [scicloj.clay-builders.id-generator :as idg]
            [scicloj.claykind.version :as version]
            [scicloj.read-kinds.api :as read-kinds])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

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
   "md"   mdp/notes-to-md
   "htm"  hpp/notes-to-html-portal})

(defn- render* [^File file {:keys [verbose] :as options}]
  (when verbose
    (println "Rendering" (str file)))
  ;; TODO: handle formats properly
  (let [builder mdp/notes-to-md #_(get formats format)]
    (-> (read-kinds/notebook file options)
        (builder options)
        (->> (idg/scope (str file)))
        (->> (clay.io/spit! (clay.io/target file "md" options))))))

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
         files (clay.io/clojure-files paths)]
     (when verbose
       (println "Claykind" version/version "render options:")
       (pprint/pprint options)
       (println "Found" (count files) "source files to render."))
     (doseq [file files]
       (render* file options))
     (when verbose
       (println "Done."))))
  ([path options]
   (render* (io/file path)
            (merge default-options
                   (clay.io/find-config)
                   options))))
