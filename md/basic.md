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

<link href="style.css" rel="stylesheet" type="text/css" />
<script src="https://scicloj.github.io/scittle/js/scittle.js" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script><script src="https://unpkg.com/react@18/umd/react.production.min.js" type="text/javascript"></script><script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js" type="text/javascript"></script><script src="https://scicloj.github.io/scittle/js/scittle.reagent.js" type="text/javascript"></script>
<script type="application/x-scittle">(require
  '[reagent.core :as r]
  '[reagent.dom :as dom]
  '[clojure.str :as str])
</script>

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

<h1>Hello world</h1>

```clojure
[(range 20) (reverse (range 20))]
```

<div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_seq"><div style="border:1px solid grey;padding:2px;">0</div><div style="border:1px solid grey;padding:2px;">1</div><div style="border:1px solid grey;padding:2px;">2</div><div style="border:1px solid grey;padding:2px;">3</div><div style="border:1px solid grey;padding:2px;">4</div><div style="border:1px solid grey;padding:2px;">5</div><div style="border:1px solid grey;padding:2px;">6</div><div style="border:1px solid grey;padding:2px;">7</div><div style="border:1px solid grey;padding:2px;">8</div><div style="border:1px solid grey;padding:2px;">9</div><div style="border:1px solid grey;padding:2px;">10</div><div style="border:1px solid grey;padding:2px;">11</div><div style="border:1px solid grey;padding:2px;">12</div><div style="border:1px solid grey;padding:2px;">13</div><div style="border:1px solid grey;padding:2px;">14</div><div style="border:1px solid grey;padding:2px;">15</div><div style="border:1px solid grey;padding:2px;">16</div><div style="border:1px solid grey;padding:2px;">17</div><div style="border:1px solid grey;padding:2px;">18</div><div style="border:1px solid grey;padding:2px;">19</div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_seq"><div style="border:1px solid grey;padding:2px;">19</div><div style="border:1px solid grey;padding:2px;">18</div><div style="border:1px solid grey;padding:2px;">17</div><div style="border:1px solid grey;padding:2px;">16</div><div style="border:1px solid grey;padding:2px;">15</div><div style="border:1px solid grey;padding:2px;">14</div><div style="border:1px solid grey;padding:2px;">13</div><div style="border:1px solid grey;padding:2px;">12</div><div style="border:1px solid grey;padding:2px;">11</div><div style="border:1px solid grey;padding:2px;">10</div><div style="border:1px solid grey;padding:2px;">9</div><div style="border:1px solid grey;padding:2px;">8</div><div style="border:1px solid grey;padding:2px;">7</div><div style="border:1px solid grey;padding:2px;">6</div><div style="border:1px solid grey;padding:2px;">5</div><div style="border:1px solid grey;padding:2px;">4</div><div style="border:1px solid grey;padding:2px;">3</div><div style="border:1px solid grey;padding:2px;">2</div><div style="border:1px solid grey;padding:2px;">1</div><div style="border:1px solid grey;padding:2px;">0</div></div></div></div>

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
