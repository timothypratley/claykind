<style>
.printedClojure .sourceCode {
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
</style>

<script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script><script src="portal-main.js" type="text/javascript"></script>

```clojure
(ns kindly.grammars
  (:require [clojure.java.io :as io]
            [malli.core :as ma])
  (:import (javax.imageio ImageIO)))
```

<div class="printedClojure">

```clojure
nil
```

</div>

# Kindly grammars

Notebooks and rich REPLs visualize data and objects.
Kindly seeks to establish a standard way for users to request visualizations.

Nesting is an important consideration.
For example a table might contain an image in a cell.

## HTML (hiccup)

HTML is the most flexible visualization,
as you can represent pretty much anything that can display in a browser.

```clojure
(def hiccup-table
  ^:kindly/hiccup
  [:table
   [:tr [:td 1] [:td 2]]
   [:tr [:td 3] [:td 4]]])
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/hiccup-table"
```

</div>

The downside of HTML is that users need to expend considerable effort reshaping their data into views.

Hiccup has a Malli schema that can be used to validate and parse:

```clojure
(def Hiccup
  [:schema {:registry {"hiccup" [:orn
                                 [:node [:catn
                                         [:name keyword?]
                                         [:props [:? [:map-of keyword? any?]]]
                                         [:children [:* [:schema [:ref "hiccup"]]]]]]
                                 [:primitive [:orn
                                              [:nil nil?]
                                              [:boolean boolean?]
                                              [:number number?]
                                              [:text string?]]]]}}
   "hiccup"])
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/Hiccup"
```

</div>

Hiccup does not check for the creation of valid HTML; tags and attributes are not checked.

```clojure
(def parse-hiccup (ma/parser Hiccup))
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/parse-hiccup"
```

</div>

```clojure
(parse-hiccup hiccup-table)
```

<div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:table</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:tr</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:td</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:primitive</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:number</code></div><div style="border:1px solid grey;padding:2px;"><code>1</code></div></div></div></div></div></div></div></div></div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:td</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:primitive</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:number</code></div><div style="border:1px solid grey;padding:2px;"><code>2</code></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:tr</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:td</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:primitive</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:number</code></div><div style="border:1px solid grey;padding:2px;"><code>3</code></div></div></div></div></div></div></div></div></div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:node</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;"><code>:name</code></div><div style="border:1px solid grey;padding:2px;"><code>:td</code></div><div style="border:1px solid grey;padding:2px;"><code>:props</code></div><div style="border:1px solid grey;padding:2px;"><code>nil</code></div><div style="border:1px solid grey;padding:2px;"><code>:children</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:primitive</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;"><code>:number</code></div><div style="border:1px solid grey;padding:2px;"><code>4</code></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div>

## Tables

Tables are interesting because one can imagine users with data in many different shapes;
vectors of vectors, vectors of maps, Tablecloth datasets, or maybe something else.

```clojure
^:kind/table
{:headers     []
 :row-vectors []}
```

----

```clojure
^:kind/table
{:headers     []
 :row-maps []}
```

----

```clojure
^:kind/table
[[1 2]
 [3 4]]
```

----

It would be convenient to allow users several options for table inputs,
and some transformations to standardize them for downstream tools.
Perhaps this can be achieved with a shorthand helper?

```clojure
'(tabulate values)
```

<div class="printedClojure">

```clojure
(tabulate values)
```

</div>

```clojure
'(tabulate headers values)
```

<div class="printedClojure">

```clojure
(tabulate headers values)
```

</div>

Possibly a multimethod (with a row multimethod as well).

Tools should be encouraged to accept a standardized data shape defined by a schema:

```clojure
(def Table
  [:schema {:registry {"table" ['...]}}
   "table"])
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/Table"
```

</div>

## Plots

Plots have the richest grammar.
Two popular grammars for plotting are Vega and ggplot2.

```clojure
(defn vega-lite-point-plot [data]
  ^:kind/vega-lite
  {:data {:values data},
   :mark "point"
   :encoding
   {:size {:field "w" :type "quantitative"}
    :x    {:field "x", :type "quantitative"},
    :y    {:field "y", :type "quantitative"},
    :fill {:field "z", :type "nominal"}}})
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/vega-lite-point-plot"
```

</div>

```clojure
(defn random-data [n]
  (->> (repeatedly n #(- (rand) 0.5))
       (reductions +)
       (map-indexed (fn [x y]
                      {:w (rand-int 9)
                       :z (rand-int 9)
                       :x x
                       :y y}))))
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/random-data"
```

</div>

```clojure
(defn random-vega-lite-plot [n]
  (-> n
      random-data
      vega-lite-point-plot))
```

<div class="printedClojure">

```clojure
"#'kindly.grammars/random-vega-lite-plot"
```

</div>

```clojure
^:kind/vega-lite
(random-vega-lite-plot 9)
```

<div style="width:100%;"><script>vegaEmbed(document.currentScript.parentElement, {&quot;encoding&quot;:{&quot;y&quot;:{&quot;field&quot;:&quot;y&quot;,&quot;type&quot;:&quot;quantitative&quot;},&quot;fill&quot;:{&quot;field&quot;:&quot;z&quot;,&quot;type&quot;:&quot;nominal&quot;},&quot;size&quot;:{&quot;field&quot;:&quot;w&quot;,&quot;type&quot;:&quot;quantitative&quot;},&quot;x&quot;:{&quot;field&quot;:&quot;x&quot;,&quot;type&quot;:&quot;quantitative&quot;}},&quot;mark&quot;:&quot;point&quot;,&quot;data&quot;:{&quot;values&quot;:[{&quot;y&quot;:0.48584472723106953,&quot;w&quot;:3,&quot;z&quot;:5,&quot;x&quot;:0},{&quot;y&quot;:0.8781885861662642,&quot;w&quot;:5,&quot;z&quot;:6,&quot;x&quot;:1},{&quot;y&quot;:0.8383061274771147,&quot;w&quot;:2,&quot;z&quot;:3,&quot;x&quot;:2},{&quot;y&quot;:0.7282862486273248,&quot;w&quot;:3,&quot;z&quot;:0,&quot;x&quot;:3},{&quot;y&quot;:1.2257674031197172,&quot;w&quot;:3,&quot;z&quot;:8,&quot;x&quot;:4},{&quot;y&quot;:1.7187009604488206,&quot;w&quot;:7,&quot;z&quot;:8,&quot;x&quot;:5},{&quot;y&quot;:1.6125843370476258,&quot;w&quot;:4,&quot;z&quot;:2,&quot;x&quot;:6},{&quot;y&quot;:1.4004100521327216,&quot;w&quot;:7,&quot;z&quot;:2,&quot;x&quot;:7},{&quot;y&quot;:1.782901573271348,&quot;w&quot;:0,&quot;z&quot;:3,&quot;x&quot;:8}]}});</script></div>

Vega has json-schemas available which are comprehensive.
There is not yet a way to create Malli schemas from Vega json-schema.
We could use a json-schema library instead,
or improve Malli schema conversion.

## Images

No grammar is necessary for images.

Images can be specified using Markdown syntax:

    ![a heart](claykind.png)

![a heart](../../../claykind.png)

Managing the path to images can be challenging for users.

It is nice to be able to use "send form to Portal" or similar, can this be done with Markdown?

```clojure
(ImageIO/read (io/file "claykind.png"))
```

<img src="BufferedImage@633da40b: type = 6 ColorModel: #pixelBits = 32 numComponents = 4 color space = java.awt.color.ICC_ColorSpace@53267b19 transparency = 3 has alpha = true isAlphaPre = false ByteInterleavedRaster: width = 256 height = 256 #numDataElements 4 dataOff[0] = 3" />

Users may benefit from a shorthand helper function `(image "claykind.png")`.

sometimes the filename will be calculated programmatically,
which would be teadious to achieve with markdown.

## Reagent components

A grammar is not possible for functions, or Reagent components.

## Tablecloth datasets

Tablecloth datasets are identifiable by their type.

## Markdown

Markdown comes in different flavours,
for example GitHub Markdown and Quarto (qmd).
Downstream tools may benefit from a hint as to which flavor should be used.
Users probably don't want to have to think too much about this though.

Of particular interest is the display of mathematical equations,
which often require plugin support.

$ f(x) = x^2 $

Should render as an equation.

Somewhat surprisingly, we cannot rely entirely on comments to represent Markdown.
We might need to construct Markdown programmatically.

```clojure
^:kindly/markdown
(str "## This... is... " :Markdown)
```

<div class="printedClojure">

```clojure
"## This... is... :Markdown"
```

</div>

## Data

No grammar is necessary for primitives and collections.

```clojure
{:this #{"is"}
 'data [1 2 3 nil]}
```

<div class="printedClojure">

```clojure
{:this #{"is"}, data [1 2 3 nil]}
```

</div>

One challenge is when the data is very large.
Ideally some preview would be available.
