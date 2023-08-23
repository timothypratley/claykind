(ns test.basic)

;; # section 1
;;
;;  hello, welcome to my wonderful test notebook

(+ 1 2 3)

(defn f [x]
  (+ x                                                      ; let's do some addition
     ;; I like addition
     9))

(f 20)

;; # section 2

;; What if I told you
;;
;;     That codeblocks can exist inside comments?
;;
;; And that you can do HTML?

^:kind/hiccup
[:h1 "Hello world"]

;; We can add things that translate to the existing kindly specs...
;; But! Now I have this different thing; SVG images, tables that have double rows.
;; Pushing handling code out the display tools
;; The categories of features

;; Notice that there is only whitespace between this comment and the previous one,
;; and that the whitespace was preserved in the markdown. Wonderful!

;; Possible feature: Order of evaluation
;; present the last form first!
;; Just reverse the contexts.
;; Might want to annotate this in the namespace itself somehow,
;; perhaps metadata on the `ns` form?
;; And even on individual parts? Bringing them to the top or bottom or setting their position.
;;
;; Here is a Markdown table
;;
;; |  |  |  |
;; |--|--|--|
;; | "a" | "b" | "c" |
