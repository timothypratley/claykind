(ns scicloj.claykind.notes
  (:require [clojure.string :as str]
            [scicloj.claykind.read :as read]
            [scicloj.claykind.kinds :as kinds]))

(defn join-comment-blocks [comment-blocks]
  {:kind           :kindly/comment
   :kindly/comment (->> (map :kindly/comment comment-blocks)
                        (str/join "\n"))})

(defn eval-notes [code]
  (->> (read/parse-forms code)
       (remove (comp #{:kindly/whitespace} :kind))
       (map kinds/infer-kind)
       (partition-by (comp some? :kindly/comment))
       (mapcat (fn [part]
                 (if (-> part first :kindly/comment)
                   [(join-comment-blocks part)]
                   part)))))

(defn safe-eval-notes [code]
  (try
    (eval-notes code)
    (catch Exception ex
      (println :invalid-notes (-> (Throwable->map ex)
                                  (select-keys [:cause :data])))
      nil)))
