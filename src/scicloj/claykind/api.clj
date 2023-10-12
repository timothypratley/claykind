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

;; TODO: is there a nicer way? Should this be a `format` protocol? multimethod? One single method??
(def flavors
  {"html"     {:fn        html/notes-to-html
               :docs      "https://developer.mozilla.org/en-US/docs/Learn/HTML"
               :spec      "https://www.w3.org/TR/2011/WD-html5-20110405/"
               :extension "html"}
   "portal"   {:fn        hpp/notes-to-html-portal
               :docs      "https://github.com/djblue/portal"
               :spec      "https://github.com/djblue/portal/blob/master/src/portal/api.cljc"
               :extension "html"}
   "markdown" {:fn        mdp/notes-to-md
               :docs      "https://pandoc.org/MANUAL.html#pandocs-markdown"
               :spec      "https://pandoc.org/"
               :extension "md"}
   "gfm"      {:fn        mdp/notes-to-md
               :docs      "https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax"
               :spec      "https://github.github.com/gfm/"
               :extension "md"}})

(def default-options
  {:flavor           "gfm"
   :paths            ["notebooks"]
   :target-dir       "docs"
   :fail-if-warnings false})

(defn- render* [^File file {:keys [verbose] :as options}]
  (when verbose
    (println "Rendering" (str file)))
  ;; TODO: handle formats and extensions properly
  (let [builder mdp/notes-to-md #_(get formats format)
        target (clay.io/target file "md" options)]
    (-> (read-kinds/notebook file options)
        (builder options)
        (->> (idg/scope (str file)))
        (->> (clay.io/spit! target)))
    (when verbose
      (println "Wrote" (str target)))))

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
