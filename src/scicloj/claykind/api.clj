(ns scicloj.claykind.api
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [scicloj.clay-builders.html-plain :as html]
            [scicloj.clay-builders.html-portal :as hpp]
            [scicloj.clay-builders.markdown-page :as mdp]
            [scicloj.claykind.io :as clay.io]
            [scicloj.clay-builders.id-generator :as idg]
            [scicloj.claykind.version :as version]
            [scicloj.read-kinds.api :as read-kinds])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(def flavors
  {"html"     {:compile        html/notes-to-html
               :file-extension "html"
               :docs           "https://developer.mozilla.org/en-US/docs/Learn/HTML"
               :spec           "https://www.w3.org/TR/2011/WD-html5-20110405/"}
   "portal"   {:compile        hpp/notes-to-html-portal
               :file-extension "html"
               :docs           "https://github.com/djblue/portal"
               :spec           "https://github.com/djblue/portal/blob/master/src/portal/api.cljc"}
   "markdown" {:compile        mdp/notes-to-md
               :file-extension "md"
               :docs           "https://pandoc.org/MANUAL.html#pandocs-markdown"
               :spec           "https://pandoc.org/"}
   "gfm"      {:compile        mdp/notes-to-md
               :file-extension "md"
               :docs           "https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax"
               :spec           "https://github.github.com/gfm/"}})

;; slides are the same markdown with different build time stuff
;; Steps should be visible
;; front-matter per file vs ___ for book
(def default-options
  {:paths            ["notebooks"]
   :flavor           "gfm"
   :targets          {"docs" {:flavor "gfm"}
                      "md"   {:flavor "markdown"}}
   :verbose          false
   :fail-if-warnings false})

(defn- render* [^File file {:keys [targets verbose] :as options}]
  (when verbose
    (println "Rendering" (str file) (pr-str targets)))
  (doseq [[target-dir target-options] targets
          :let [options (merge options target-options)
                {:keys [paths]} options]
          :when (first (filter #(clay.io/inside? (io/file %) file) paths))]
    (let [
          {:keys [file-extension flavor]} options
          compile (or (get-in flavors [flavor :compile])
                      (throw (ex-info (str "Flavor '" flavor "' not found in " (keys flavors))
                                      options)))
          extension (or file-extension
                        (get-in flavors [flavor :file-extension])
                        "md")
          target (clay.io/target target-dir file extension)
          notebook (read-kinds/notebook file options)
          result (idg/scope (str file) (compile notebook options))]
      (clay.io/spit! target result)
      (when verbose
        (println "Wrote" (str target))))))

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

(comment
  (render! {:verbose true}))
