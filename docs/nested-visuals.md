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





# Nested Visuals

```clojure
(ns blog.nested-visuals
  "Wherein we nest Kindly visualizations for fun and profit"
  (:require [scicloj.kindly.v4.kind :as kind]))
```



Intro

```clojure
(def x 1)
```

> ```clojure
> #'blog.nested-visuals/x
> ```

```clojure
(def my-svg
  (kind/hiccup
    [:svg {}
     [:circle {:cx 50
               :cy 50
               :r  50}]]))
```

<svg><circle cx="50" cy="50" r="50"></circle></svg>

```clojure
(kind/portal [my-svg])
```

<pre><code>^#:kindly{:kind :kind/hiccup}
[:svg {} [:circle {:r 50, :cx 50, :cy 50}]]
</code></pre>



Visualization requests

```clojure
(kind/table
  {:column-names ["project" "features"]
   :row-vectors  [[]
                  []]})
```

| project | features |
| ---- | ---- |
|  |
|  |

```clojure
^:kind/hiccup
(def a
  [:svg {}
   [:circle {:cx 50
             :cy 50
             :r 20}]])
```

<svg><circle cx="50" cy="50" r="20"></circle></svg>
