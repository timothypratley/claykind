(ns scicloj.kind-hiccup.id-generator
  (:require [clojure.string :as str]))

(def ^:dynamic *scope-name* "unscoped")
(def ^:dynamic *counter* (atom 0))

(defmacro scope [scope-name & body]
  `(binding [*scope-name* ~scope-name
             *counter* (atom 0)]
     ~@body))

(defn no-spaces [s]
  (str/replace s #"\s" "-"))

(defn gen-id []
  (str (no-spaces *scope-name*) "-" (swap! *counter* inc)))
