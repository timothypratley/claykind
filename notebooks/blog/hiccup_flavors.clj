;; # Hiccup flavors

;; Does this break Claykind? :(
;; TODO: what should be the correct way to provide a top level h1?
(ns blog.hiccup-flavors
  "Wherein we explore several flavors of hiccup"
  (:require [hiccup2.core :as hiccup2]
            [lambdaisland.hiccup :as lhiccup]
            [huff.core :as hhiccup]
            [scicloj.kind-hiccup.api :as khiccup]
            [scicloj.kindly.v4.kind :as kind]))


; ![Hiccup is concise](https://i.redd.it/59i7rh6wt3271.jpg)

;; Hiccup uses vectors to represent elements, and maps to represent an element's attributes.

(kind/hiccup
  [:div "Hello " [:em "World"]])

(str (hiccup2/html
       [:div "Hello " [:em "World"]]))

(str (hiccup2/html
       [:table
        [:thead
         [:tr [:th "header1"] [:th "header2"]]]
        [:tbody
         [:tr [:td 1] [:td 2]]
         [:tr [:td 3] [:td 4]]]]))

; <table>
;  <thead>
;    <tr><th>header1</th><th>header2</th></tr>
;  </thead>
;  <tbody>
;    <tr><td>1</td><td>2</td></tr>
;    <tr><td>3</td><td>4</td></tr>
;  </tbody>
; </table>

;; ## Templating

;; Dissenters


(def hiccup-implementations
  [{:name     "Hiccup"
    :author   "James Reeves (weavejester)"
    :id       'hiccup/hiccup
    :url      "https://github.com/weavejester/hiccup"
    :features #{"fragments"}}

   {:name      "LambdaIsland Hiccup"
    :author    "Arne Brasseur (plexus)"
    :id        'com.lambdaisland/hiccup
    :url       "https://github.com/lambdaisland/hiccup"
    :platforms #{"Clojure"}
    :features  #{"auto-escape strings"
                 "fragments"
                 "components"
                 "style maps"
                 "unsafe strings"
                 "kebab-case"}}

   {:name     "Huff"
    :author   "Bryan Maass (escherize)"
    :id       'io.github.escherize/huff
    :url      "https://github.com/escherize/huff"
    :perf     {:runtime  1
               :compiled 1}
    :tests    {}
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

   {:name     "Reagent"
    :author   "Dan Holmsand (holmsand)"
    :id       'reagent/reagent
    :url      "https://github.com/reagent-project/reagent"
    :features #{"ClojureScript"}}

   {:name     "kind-hiccup"
    :author   "Timothy Pratley"
    :id       'org.scicloj/kind-hiccup
    :url      "https://github.com/timothypratley/claykind"
    :features #{"Babashka"}}])

(kind/table
  {:column-names ["name" "author" "project" "features"]
   :row-vectors  (for [{:keys [name author id url features]} hiccup-implementations]
                   [name author (kind/hiccup [:a {:href url} id]) features])})

;; ## Error Handling

;; ## Security (avoiding XSS)

;; ### Escaping

;; Handling of raw strings

;; ## Extensibility

[:div "hello world" ['(fn [] [:div [myjscomponent]])]]


;; IDEA: something that just expands kinds might be better?

;; TODO: but why does this even work?
;; Markdown don't care, it's not escaped (but it could be)
;; What should this show?
;; <h3>I'm big</h3>
;; **should it** work?
;; Or should it be &lt;h3&gt;
;; what about 3 > 2?
;; or
;; ```
;; 3 > 2
;; ```
;; Probably we don't want `<script>...</script>`,
;; but maybe we do? (after all we encourage scittle and reagent)

(defn html [x]
  [(str (hiccup2/html x))
   (khiccup/html x)
   (lhiccup/render x {:doctype? false})
   (hhiccup/html x)])

(html [:div "Hello" [:em "World"]])
