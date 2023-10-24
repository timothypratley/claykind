(ns scicloj.claykind.api
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [scicloj.clay-builders.html-plain :as html]
            [scicloj.clay-builders.html-portal :as hpp]
            [scicloj.clay-builders.markdown-page :as mdp]
            [scicloj.claykind.io :as clay.io]
            [scicloj.claykind.version :as version]
            [scicloj.kind-hiccup.id-generator :as idg]
            [scicloj.read-kinds.api :as read-kinds]
            [scicloj.read-kinds.notes :as notes])
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

(defn- render* [target-dir ^File file {:keys [verbose file-extension flavor] :as options}]
  (when verbose
    (println "Rendering" (notes/relative-path file)))
  (let [compile (or (get-in flavors [flavor :compile])
                    (throw (ex-info (str "Flavor '" flavor "' not found in " (keys flavors))
                                    {:id      ::flavor-not-found
                                     :options options})))
        extension (or file-extension
                      (get-in flavors [flavor :file-extension])
                      "md")
        target (clay.io/target target-dir file extension)
        ;; TODO: should avoid re-reading for multiple targets
        notebook (read-kinds/notebook file options)
        result (idg/scope (str file) (compile notebook options))]
    (clay.io/spit! target result)
    (when verbose
      (println "Wrote" (str target)))))

(defn- find-file-targets
  "Given a file, figure out which targets require it to be rendered."
  [file {:keys [targets] :as options}]
  (for [[target-dir target-options] targets
        :let [options (merge options target-options)
              {:keys [paths]} options]
        :when (first (filter #(clay.io/inside? (io/file %) file) paths))]
    [target-dir options]))

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
         {:keys [verbose targets]} options]
     (when verbose
       (println "Claykind" version/version "render options:")
       (pprint/pprint options))
     (if (seq targets)
       (doseq [[target-dir target-options] targets
               :let [options (merge options target-options)
                     files (clay.io/clojure-files (:paths options))]]
         (when verbose
           (println "Found" (count files) "source files to render into '" target-dir "'."))
         (doseq [file files]
           (render* target-dir file options)))
       (throw (ex-info (str "No targets configured")
                       {:id      ::no-targets
                        :options options})))
     (when verbose
       (println "Done."))))
  ([path options]
   (let [file (io/file path)
         options (merge default-options
                        (clay.io/find-config)
                        options)
         file-targets (find-file-targets file options)]
     (if (seq file-targets)
       (doseq [[target-dir options] file-targets]
         (render* target-dir file options))
       (throw (ex-info (str "Failed to render file '" path "': No targets configured")
                       {:id      ::no-targets-for-file
                        :options options}))))))

(comment
  (render! {:verbose true}))
