(ns try-again)

;> If at first you don't succeed,
;> dust yourself off and try again.
;>
;> -- <cite>Aaliyah</cite>

; One thing I love about Clojure is that it allows me to change functions on the fly.
; The Read-Eval-Print-Loop (REPL) becomes a trusted companion, a real-time feedback mechanism,
; and a dynamic environment fostering experimentation and rapid iteration.
; But I'm not usually "in" the REPL.
; I'm "in" my editor, sending forms to the REPL.
; Evaluating Expressions at the cursor,
; running tests, navigating, and viewing docstrings or parameter lists.

; A common scenario I find myself in is that I set up a little bit of code to try something.
; Then I make some changes, and need to retry the same snippet or test.
; In the meantime I have navigated away and I need to either go back to the form I am interested in,
; or navigate to the REPL and use history to try it again.
; Both of these strategies work, but there is a better way!

; 💡
; The idea is to create a REPL command that creates a function in the `user` namespace.
; Then we can make a keybinding like "send form before caret to REPL",
; and another keybinding to execute the saved function.
; That way even if we send other forms to the REPL,
; we can conveniently retry the snippet we are focused on.

; Here is how I set the "try" command up:
; ![img](try-form-before-caret.png)

; I use Cursive Clojure (IntelliJ).
; If you use a different editor, you can find instructions for creating key bound commands
; in the [Clay setup documentation](https://scicloj.github.io/clay/#setup).

; `~form-before-caret` gets replaced with code.

; So if our cursor is at the end of an expression, the `_` below:

; ```clojure
; (+ 1 2)_
; ```

; My editor will send the following code to the REPL:

; ```clojure
; (do (intern 'user 'retry (fn retry []
;                            (+ 1 2)))
;     (user/retry))
; ```

; This creates the `retry` var in the `user` namespace (`user/retry`),
; and assigns it to a function that will run the code I am interested in.

; It is worth considering a few questions about namespaces at this point.
; *Which namespace will the command execute in?*
; *How can `user/retry` "see" variables in the currently edited namespace?*

; The command will execute in the namespace of the file I am editing,
; because I configured the command to do so through Cursive.
; The function is created in the current namespace, because
; `(fn retry [] (+ 1 2))` is eagerly evaluated as an argument to `intern`.
; Because the function is created in the current namespace, `x` is resolved correctly.
; `user/retry` can be invoked from any namespace by the fully qualified name of the var `user/retry`.

; Here is how I set the "retry" command which simply invokes `(user/retry)`:
; ![img](retry-form.png)

; And here are the keybindings for the commands I use:
; ![img](retry-keybindings.png)

; I've bound `alt-z` to **"try form before caret"** and `ctrl-z` to **"retry"**.

; Now let's take it for a spin.
; Here is some code I've been working on; I'm drawing a heart shape.

(def heart-path
  "M 12.0 7.2 C 10.5 5.6 8.1 5.2 6.3 6.7 C 4.5 8.1 4.2 10.6 5.7 12.4 L 12.0 18.3 L 18.3 12.4 C 19.7 10.6 19.5 8.1 17.7 6.7 C 15.8 5.2 13.4 5.6 12.0 7.2 Z")

; This shape comes from [svg-paths](https://www.nan.fyi/svg-paths) by [@nandafyi](https://twitter.com/nandafyi).

; I'll visualize it in an SVG image:

(defn svg [& body]
  (into ^{:kindly/kind :kind/hiccup}
        [:svg {:width   256
               :height  256
               :viewBox [0 0 24 24]
               :xmlns   "http://www.w3.org/2000/svg"}]
        body))

; The heart path goes into an SVG element:

(defn heart []
  [:path {:fill "green"
          :d    heart-path}])

; And to see what it looks like I'll make a ["rich comment block"](https://www.youtube.com/watch?v=Qx0-pViyIDU&t=1229s):

; ```clojure
; (comment
;   (svg (heart)))
; ```

; The comment block allows me to conveniently send the expression `(svg (heart))` to the REPL,
; and of course we'll use my new **"try form before caret"** on it.

(svg (heart))

; Now I can navigate throughout my code base, making changes and fixes,
; and seeing the effect is just one keystroke away with **"retry"**:

(defn heart []
  [:path {:fill "red"
          :d    heart-path}])

(svg (heart))

; Setting up the command to sync all changes before executing makes it easy to test changes as I go,
; often I can rely on syncing instead of sending updates to the REPL individually.

; Special thanks to [@chrishouser](https://twitter.com/chrishouser)
; and Jon Boone for dedicating a session of [LiSP reading group](https://chouser.us/lisp2022/)
; to improving my workflow with try/retry.
