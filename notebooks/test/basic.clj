(ns test.basic)

(+ 1 2 3)

;; # section 1
;;
;;  asss

(defn f [x]
  (+ x ; hmm
     ;; hmmm
     9))

(f 20)

;; # section 2

;;        ad
;; a

^:kindly/table
{:rows ["a" "b" "c"]}

^:kindly/table
{:headers [:a :b]
 :rows    [{:a "a" :b "b"}]}

^:kindly/table-matrix
[["a" "b" "c"]]


;; add things that translate to the existing kindly specs
;; BUT! I have this different thing
;; SVG images, tables that have double rows
;; Pushing handling code out the display tools
;; The categories of features

;; Possible feature: Order of evaluation
;; present the last form first!
;; Just reverse the contexts.
;;
;; |  |  |  |
;; |--|--|--|
;; | "a" | "b" | "c" |
;;
;; standards to abide by for tool creators
;;
;; * unknown tags render the content of the tags
;; * noscript
;;
;; css allows fallback tags
;; anomalies library is interesting
;; ring was enough and won
;; adapter to portal
;; portal is not only a tool, it is a specification (could be a kind)
;; configure which viewer to render with?
;; tim: make example to portal
