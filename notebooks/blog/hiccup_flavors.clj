(ns blog.hiccup-flavors
  "Wherein we explore several flavors of hiccup"
  (:require [hiccup2.core :as hiccup2]
            [lambdaisland.hiccup :as lhiccup]
            [huff.core :as hhiccup]
            [scicloj.kind-hiccup.api :as khiccup]
            [scicloj.kindly.v4.kind :as kind]))

;|test|
;|----|
;|<h2>foo</h2><img src="https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg">|
;|test|

(def hiccup-implementations
  [{:name   "Hiccup"
    :author "James Reeves (weavejester)"
    :id     'hiccup/hiccup
    :url    "https://github.com/weavejester/hiccup"
    :features #{"fragments"}}

   {:name   "LambdaIsland Hiccup"
    :author "Arne Brasseur (plexus)"
    :id     'com.lambdaisland/hiccup
    :url    "https://github.com/lambdaisland/hiccup"
    :platforms #{"Clojure"}
    :features #{"auto-escape strings"
                "fragments"
                "components"
                "style maps"
                "unsafe strings"
                "kebab-case"}}

   {:name   "Huff"
    :author "Bryan Maass (escherize)"
    :id     'io.github.escherize/huff
    :url    "https://github.com/escherize/huff"
    :perf {:runtime 1
           :compiled 1}
    :tests {}
    :features #{"extendable grammar"
                "unsafe strings"
                "components"
                "style maps"
                "HTML-encoded by default"
                "Parse tags in any order :div#id.c or :div.c#id"
                "Babashka"
                "[:<> ...] fragments"
                "(...) fragments"
                "Extreme shorthand syntax [:. {:color :red}]"}}

   {:name   "Reagent"
    :author "Dan Holmsand (holmsand)"
    :id     'reagent/reagent
    :url    "https://github.com/reagent-project/reagent"
    :features #{"ClojureScript"}}

   {:name   "kind-hiccup"
    :author "Timothy Pratley"
    :id     'org.scicloj/kind-hiccup
    :url    "https://github.com/timothypratley/claykind"
    :features #{"Babashka"}}])

(kind/table
  {:column-names ["name" "author" "project" "features"]
   :row-vectors  (for [{:keys [name author id url features]} hiccup-implementations]
                   [name author (kind/hiccup [:a {:href url} id]) features])})

[:div "hello world" ['(fn [] [:div [myjscomponent]])]]

;; TODO: these should just be data!

^:kind/table
{:row []}

;; | Library | Platforms | Features
;; |---
;; | Hiccup | Clojure |
;; | LambdaIsland Hiccup | Clojure
;; | Huff | Clojure, Babashka
;; | kind-hiccup | Clojure, Babashka | |
;; | Reagent | ClojureScript |


;; ESCAPING and security
;; Handling of raw strings

;; something that just expands kinds might be better?

(defn html [x]
  [(str (hiccup2/html x))
   (khiccup/html x)
   (lhiccup/render x {:doctype? false})
   (hhiccup/html x)])

(html [:div "Hello" [:em "World"]])
