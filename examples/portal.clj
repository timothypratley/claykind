(ns portal
  "This example demonstrates sending a notebook to Portal"
  (:require [clojure.java.io :as io]
            [scicloj.claykind.api :as read-kinds]
            [scicloj.kind-portal.v1.api :as kp]
            [scicloj.kind-portal.v1.session :as kps]
            [portal.api :as p]))

(defn portal-options []
  (or (and (.exists (io/file ".portal" "intellij.edn"))
           {:launcher :intellij})
      (and (.exists (io/file ".portal" "vs-code.edn"))
           {:launcher :vs-code})
      {}))

(def portal
  (swap! kps/*portal-session p/open (portal-options)))

(defn show
  "Suitable to be bound to a REPL Command that submits the current filename."
  [filename]
  (->> (read-kinds/notebook filename)
       (reverse)
       (mapv kp/kindly-submit-context)))

(comment
  (show "notebooks/test/basic.clj"))
