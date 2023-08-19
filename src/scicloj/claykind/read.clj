(ns scicloj.claykind.read
  (:require [clojure.tools.reader]
            [clojure.tools.reader.reader-types]
            [clojure.string :as string]
            [rewrite-clj.parser :as parser]
            [rewrite-clj.node :as node]))

(defn eval-node [child]
  (let [tag (node/tag child)
        code (node/string child)]
    (case tag
      (:newline :whitespace)
      nil

      ;; extract text from comments
      :comment
      {:code           code
       :kind           :kindly/comment
       :kindly/comment (-> child str (string/replace #"^;*\s?" ""))}

      ;; evaluate for value
      (let [form (node/sexpr child)]
        ;; TODO: maybe infer kind or check for kind metadata
        {:code         code
         :form         form
         ;; TODO: is this :kind or :kindly/kind ?
         :kind         :kindly/value
         :kindly/value (eval form)
         ;; TODO: vars should be represented as strings for comparison, etc
         ;; perhaps representation is another thing, and it may need some user adjustment

         ;; TODO: can we make use of [:kindly/type ...] maybe
         ;; :kindly/representation [:kindly/something ...]

         }))))

(defn parse-form [code]
  (->> code
       (parser/parse-string)
       (eval-node)))

(defn parse-forms [code]
  (->> code
       parser/parse-string-all
       :children
       (map eval-node)
       (remove nil?)))

(defn unified-comment-block [comment-blocks]
  {:kind           :kindly/comment
   :kindly/comment (->> comment-blocks
                        (map :kindly/comment)
                        (string/join "\n"))})

(defn eval-notes [code]
  (->> code
       parse-forms
       (partition-by (comp some? :kindly/comment))
       (mapcat (fn [part]
                 (if (-> part first :kindly/comment)
                   [(unified-comment-block part)]
                   part)))))

(defn safe-eval-notes [code]
  (try
    (eval-notes code)
    (catch Exception ex
      (println :invalid-notes (-> ex
                                  Throwable->map
                                  (select-keys [:cause :data])))
      nil)))
