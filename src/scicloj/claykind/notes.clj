(ns scicloj.claykind.notes
  "Notes organize contexts by removing whitespace and joining adjacent comment blocks."
  (:require [clojure.string :as str]
            [scicloj.claykind.read :as read]
            [scicloj.claykind.kinds :as kinds]))

(defn join-comment-blocks [comment-blocks]
  {:kind           :kindly/comment
   :kindly/comment (-> (map :kindly/comment comment-blocks)
                       (str/join))})

(defn eval-notes [code]
  (->> (read/parse-forms code)
       (map (fn [context]
              (if (-> context :kind #{:kindly/comment :kindly/whitespace})
                context
                (kinds/infer-kind context))))
       (partition-by (comp some? :kindly/comment))
       (mapcat (fn [part]
                 (if (-> part first :kindly/comment)
                   [(join-comment-blocks part)]
                   part)))
       (remove (comp #{:kindly/whitespace} :kind))))

(defn safe-eval-notes [code]
  (try
    (eval-notes code)
    (catch Exception ex
      (println :invalid-notes (-> (Throwable->map ex)
                                  (select-keys [:cause :data])))
      nil)))
