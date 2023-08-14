(ns scicloj.claykind.read
  (:require [clojure.tools.reader]
            [clojure.tools.reader.reader-types]
            [parcera.core :as parcera]
            [clojure.string :as string]
            [rewrite-clj.parser :as parser]
            [rewrite-clj.node :as node]))

(defn read-by-rewrite-clj [code]
  (->> code
       parser/parse-string-all
       :children
       (map (fn [child]
              (let [tag (node/tag child)]
                (cond
                  ;;
                  ;; skip whitespace
                  (#{:newline :whitespace} tag) nil
                  ;;
                  ;; extract text from comments
                  (= tag :comment) #:kindly{:comment (-> child
                                                         str
                                                         (string/replace #"^;*\s*" ""))}
                  ;;
                  ;; evaluate code
                  :else (let [code (str child)
                              _ (println [:code code])
                              form (read-string code)]
                          #:kindly{:code code
                                   :form form
                                   :value (eval form)})))))
       (remove nil?)))

(defn unified-comment-block [comment-blocks]
  {:kindly/comment (->> comment-blocks
                        (map :kindly/comment)
                        (string/join "\n"))})

(defn ->notes [code]
  (->> code
       read-by-rewrite-clj
       (partition-by (comp some? :kindly/comment))
       (mapcat (fn [part]
                 (if (-> part first :kindly/comment)
                   [(unified-comment-block part)]


(defn ->safe-notes [code]
  (try
    (->notes code)
    (catch Exception e
      (println :invalid-notes (-> e
                                  Throwable->map
                                  (select-keys [:cause :data])))
      nil)))

(comment
  (->> "notebooks/dum/dummy.clj"
       slurp
       ->safe-notes))
