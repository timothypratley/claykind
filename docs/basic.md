<style>
.sourceCode:has(.printedClojure) {
  background-color: transparent;
  border-style: none;
}

.kind_map {
  background:            lightgreen;
  display:               grid;
  grid-template-columns: repeat(2, auto);
  justify-content:       center;
  text-align:            right;
  border: solid 1px black;
  border-radius: 10px;
}

.kind_vector {
  background:            lightblue;
  display:               grid;
  grid-template-columns: repeat(1, auto);
  align-items:           center;
  justify-content:       center;
  text-align:            center;
  border:                solid 2px black;
  padding:               10px;
}

.kind_set {
  background:            lightyellow;
  display:               grid;
  grid-template-columns: repeat(auto-fit, minmax(auto, max-content));
  align-items:           center;
  justify-content:       center;
  text-align:            center;
  border:                solid 1px black;
}

.kind_seq {
  background:            bisque;
  display:               grid;
  grid-template-columns: repeat(auto-fit, minmax(auto, max-content));
  align-items:           center;
  justify-content:       center;
  text-align:            center;
  border:                solid 1px black;
}
</style>

<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega@5"></script><script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega-lite@5"></script><script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega-embed@6"></script><script type="text/javascript" src="https://unpkg.com/react@18/umd/react.production.min.js"></script><script type="text/javascript" src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script><script type="text/javascript" src="https://scicloj.github.io/scittle/js/scittle.js"></script><script type="text/javascript" src="https://scicloj.github.io/scittle/js/scittle.reagent.js"></script><script type="text/javascript" src="/js/portal-main.js"></script>
<script type="application/x-scittle">[:hiccup/raw-html &quot;(ns main&#39;n                            (:require [reagent.core :as r]&#39;n                                      [reagent.dom :as dom]))&quot;]</script>

```clojure
(ns test.basic)
```

# section 1

 hello, welcome to my wonderful test notebook

```clojure
(+ 1 2 3)
```

6

```clojure
(defn f [x]
  (+ x                                                      ; let's do some addition
     ;; I like addition
     9))
```

#'test.basic/f

```clojure
(f 20)
```

29

# section 2

What if I told you

    That codeblocks can exist inside comments?

And that you can do HTML?

```clojure
^:kind/hiccup
[:h1 "Hello world"]
```

[:h1 "Hello world"]

```clojure
[(range 20) (reverse (range 20))]
```

[(0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19) (19 18 17 16 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1 0)]

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
