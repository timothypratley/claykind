#!/usr/bin/env bb

;; # Babashka notebooks

;; Babashka is, by design, as close to Clojure as possible.

;; ## Basic Examples

(+ 1 2)

{:x (range 3)}

;; ## Hiccup

^:kind/hiccup
[:div {:style {:background-color "#effeef"}}
 [:big [:big 3]]]

;; ## Time
(def now (java.time.ZonedDateTime/now))
(def LA-timezone (java.time.ZoneId/of "America/Los_Angeles"))
(def LA-time (.withZoneSameInstant now LA-timezone))
(def pattern (java.time.format.DateTimeFormatter/ofPattern "HH:mm"))
(.format LA-time pattern)

;; ## Babashka vs clojure

;; The notable differences are:

;; * Code is evaluated with [Sci](https://github.com/babashka/SCI)
;; * Not all of Clojure is available
;; * Files start with a shell directive instead of a namespace

#! Did you know that Clojure treats `#!` as a comment?

;; So you can already create Babashka notebooks if you ignore the differences,
;; but this project (claykind) will detect Babashka and use Sci,
;; which will make it more directly compatible.

;; Would it be interesting thing to try is running claykind from babashka?
;; What possibilities does that open up?
;; Faster command-line blog generation?
