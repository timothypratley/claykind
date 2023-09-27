(ns scicloj.kind-adapters.markdown
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]))

;; TODO: try using QMD for Github Flavored... maybe convert with pandoc

;; Adapters take a context and produce a representation,
;; for example hiccup, markdown, or portal-annotated values.

(defmulti adapt :kind)

(defmethod adapt :kindly/table [{:keys [value]}]
  (let [{:keys [headers rows]} value]
    (str (str/join "|" headers)
         "----"
         (str/join \newline
                   (for [row rows]
                     (str/join "|" row))))))

(defmethod adapt :default [{:keys [kind value]}]
  (str (when kind
         (str "Unimplemented: " kind \newline))
       (with-out-str (pprint/pprint value))))
