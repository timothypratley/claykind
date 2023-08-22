```clojure
(ns test.basic)
```
```
=>

```

```clojure
^{:kindly/kind :kind/hiccup}
[:ul
 [:li "first thing"]
 [:li "another thing"]]
```
```
=>
[:ul [:li "first thing"] [:li "another thing"]]
```

# section 1

 hello, welcome to my wonderful test notebook

```clojure
(+ 1 2 3)
```
```
=>
6
```

```clojure
(defn f [x]
  (+ x                                                      ; let's do some addition
     ;; I like addition
     9))
```
```
=>
#'test.basic/f
```

```clojure
(f 20)
```
```
=>
29
```

# section 2

What if I told you

    That codeblocks can exist inside comments?

And that tables are tricky

```clojure
^:kindly/table
{:rows [["a" "b" "c"]]}
```
```
=>
{:rows [["a" "b" "c"]]}
```

```clojure
^:kindly/table
{:headers  [:a :b]
 :row-maps [{:a "a" :b "b"}]}
```
```
=>
{:row-maps [{:b "b", :a "a"}], :headers [:a :b]}
```

```clojure
^:kindly/table-matrix
[["a" "b" "c"]]
```
```
=>
[["a" "b" "c"]]
```

We can add things that translate to the existing kindly specs...
But! Now I have this different thing; SVG images, tables that have double rows.
Pushing handling code out the display tools
The categories of features

Notice that there is only whitespace between this comment and the previous one,
and that the whitespace was preserved in the markdown. Wonderful!

Possible feature: Order of evaluation
present the last form first!
Just reverse the contexts.
Might want to annotate this in the namespace itself somehow,
perhaps metadata on the `ns` form?
And even on individual parts? Bringing them to the top or bottom or setting their position.

Here is a Markdown table

|  |  |  |
|--|--|--|
| "a" | "b" | "c" |
