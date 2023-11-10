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

<pre><code>(require
  '[reagent.core :as r]
  '[reagent.dom :as dom]
  '[clojure.str :as str])
</code></pre>

```clojure
(ns kindly.grammars
  (:require [clojure.java.io :as io]
            [malli.core :as ma])
  (:import (javax.imageio ImageIO)))
```

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

Hiccup does not check for the creation of valid HTML; tags and attributes are not checked.

```clojure
(def parse-hiccup (ma/parser Hiccup))
```

```clojure
(parse-hiccup hiccup-table)
```

<div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:table</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:tr</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:td</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:primitive</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:number</div><div style="border:1px solid grey;padding:2px;">1</div></div></div></div></div></div></div></div></div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:td</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:primitive</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:number</div><div style="border:1px solid grey;padding:2px;">2</div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:tr</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:td</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:primitive</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:number</div><div style="border:1px solid grey;padding:2px;">3</div></div></div></div></div></div></div></div></div></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:node</div><div style="border:1px solid grey;padding:2px;"><div class="kind_map"><div style="border:1px solid grey;padding:2px;">:name</div><div style="border:1px solid grey;padding:2px;">:td</div><div style="border:1px solid grey;padding:2px;">:props</div><div style="border:1px solid grey;padding:2px;"></div><div style="border:1px solid grey;padding:2px;">:children</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:primitive</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:number</div><div style="border:1px solid grey;padding:2px;">4</div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div></div>

## Tables

Tables are interesting because one can imagine users with data in many different shapes;
vectors of vectors, vectors of maps, Tablecloth datasets, or maybe something else.

```clojure
^:kind/table
{:headers     []
 :row-vectors []}
```

|  |
|  |

```clojure
^:kind/table
{:headers     []
 :row-maps []}
```

|  |
|  |

```clojure
^:kind/table
[[1 2]
 [3 4]]
```

|  |
|  |

It would be convenient to allow users several options for table inputs,
and some transformations to standardize them for downstream tools.
Perhaps this can be achieved with a shorthand helper?

```clojure
'(tabulate values)
```

<div class="kind_seq"><div style="border:1px solid grey;padding:2px;">tabulate</div><div style="border:1px solid grey;padding:2px;">values</div></div>

```clojure
'(tabulate headers values)
```

<div class="kind_seq"><div style="border:1px solid grey;padding:2px;">tabulate</div><div style="border:1px solid grey;padding:2px;">headers</div><div style="border:1px solid grey;padding:2px;">values</div></div>

Possibly a multimethod (with a row multimethod as well).

Tools should be encouraged to accept a standardized data shape defined by a schema:

```clojure
(def Table
  [:schema {:registry {"table" ['...]}}
   "table"])
```

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

```clojure
(defn random-vega-lite-plot [n]
  (-> n
      random-data
      vega-lite-point-plot))
```

```clojure
^:kind/vega-lite
(random-vega-lite-plot 9)
```

<div style="width:100%;"><img src="https://placehold.co/600x400" /></div>

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

<div>Image kind not implemented</div>

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

## This... is... :Markdown

## Data

No grammar is necessary for primitives and collections.

```clojure
{:this #{"is"}
 'data [1 2 3 nil]}
```

<div class="kind_map"><div style="border:1px solid grey;padding:2px;">:this</div><div style="border:1px solid grey;padding:2px;"><div class="kind_set"><div style="border:1px solid grey;padding:2px;">is</div></div></div><div style="border:1px solid grey;padding:2px;">data</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">1</div><div style="border:1px solid grey;padding:2px;">2</div><div style="border:1px solid grey;padding:2px;">3</div><div style="border:1px solid grey;padding:2px;"></div></div></div></div>

One challenge is when the data is very large.
Ideally some preview would be available.
