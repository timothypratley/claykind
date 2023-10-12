(ns hiccups
  "Wherein we explore several flavors of hiccup"
  (:require [hiccup2.core :as hiccup2]
            [lambdaisland.hiccup :as lhiccup]
            [huff.core :as hhiccup]
            [scicloj.kind-hiccup.api :as khiccup]))

;; | Library | Author | project |
;; |---
;; | Hiccup | James Reeves (weavejester) | [hiccup/hiccup](https://github.com/weavejester/hiccup) |
;; | LambdaIsland Hiccup | Arne Brasseur (plexus) | [com.lambdaisland/hiccup](https://github.com/lambdaisland/hiccup) |
;; | Huff | Bryan (escherize) | [io.github.escherize/huff](https://github.com/escherize/huff) |
;; | kind-hiccup | | |
;; | Reagent | Dan Holmsand (holmsand) | https://github.com/reagent-project/reagent |

;; TODO: these should just be data!

;; | Library | Platforms | Features
;; |---
;; | Hiccup | Clojure |
;; | LambdaIsland Hiccup | Clojure
;; | Huff | Clojure, Babashka
;; | kind-hiccup | Clojure, Babashka | |
;; | Reagent | ClojureScript |


(defn html [x]
  [(str (hiccup2/html x))
   (khiccup/html x)
   (lhiccup/render x {:doctype? false})
   (hhiccup/html x)])

(html [:div "Hello" [:em "World"]])
