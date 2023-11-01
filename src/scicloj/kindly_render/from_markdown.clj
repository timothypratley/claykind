(ns scicloj.kindly-render.from-markdown
  (:require [nextjournal.markdown :as md]
            [nextjournal.markdown.transform :as mdt]))

;; TODO: this might not be the best way to render markdown, but for now it seems good enough
;; Note that either hiccup or a string is fine
;; TODO: is there a nice way to be able to render markdown by adding an adapter?
;; because we don't want flexmark or nextjournal dependencies in this project
;; yes, conditionally require them.

;; TODO: shouldn't need this (something upstream should have unwrapped it already)
(defn normalize-md [value]
  (if (vector? value)
    (str (first value))
    (str value)))

(defn hiccup [value options]
  (mdt/->hiccup (md/parse (normalize-md value))))

(defn html [value options]
  (mdt/->text (md/parse (normalize-md value))))
