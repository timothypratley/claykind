```clojure
(ns kindly.grammars
  (:require [clojure.java.io :as io]
            [malli.core :as ma])
  (:import (javax.imageio ImageIO)))

;=> nil
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

;=> Unimplemented: :kind/var
;   #'kindly.grammars/hiccup-table
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

;=> Unimplemented: :kind/var
;   #'kindly.grammars/Hiccup
```

Hiccup does not check for the creation of valid HTML; tags and attributes are not checked.

```clojure
(def parse-hiccup (ma/parser Hiccup))

;=> Unimplemented: :kind/var
;   #'kindly.grammars/parse-hiccup
```

```clojure
(parse-hiccup hiccup-table)

;=> Unimplemented: :kind/vector
;   [:node
;    {:name :table,
;     :props nil,
;     :children
;     [[:node
;       {:name :tr,
;        :props nil,
;        :children
;        [[:node
;          {:name :td, :props nil, :children [[:primitive [:number 1]]]}]
;         [:node
;          {:name :td,
;           :props nil,
;           :children [[:primitive [:number 2]]]}]]}]
;      [:node
;       {:name :tr,
;        :props nil,
;        :children
;        [[:node
;          {:name :td, :props nil, :children [[:primitive [:number 3]]]}]
;         [:node
;          {:name :td,
;           :props nil,
;           :children [[:primitive [:number 4]]]}]]}]]}]
```

## Tables

Tables are interesting because one can imagine users with data in many different shapes;
vectors of vectors, vectors of maps, Tablecloth datasets, or maybe something else.

```clojure
^:kind/table
{:headers     []
 :row-vectors []}

;=> Unimplemented: :kind/table
;   {:headers [], :row-vectors []}
```

```clojure
^:kind/table
{:headers     []
 :row-maps []}

;=> Unimplemented: :kind/table
;   {:row-maps [], :headers []}
```

```clojure
^:kind/table
[[1 2]
 [3 4]]

;=> Unimplemented: :kind/table
;   [[1 2] [3 4]]
```

It would be convenient to allow users several options for table inputs,
and some transformations to standardize them for downstream tools.
Perhaps this can be achieved with a shorthand helper?

```clojure
'(tabulate values)

;=> Unimplemented: :kind/seq
;   (tabulate values)
```

```clojure
'(tabulate headers values)

;=> Unimplemented: :kind/seq
;   (tabulate headers values)
```

Possibly a multimethod (with a row multimethod as well).

Tools should be encouraged to accept a standardized data shape defined by a schema:

```clojure
(def Table
  [:schema {:registry {"table" ['...]}}
   "table"])

;=> Unimplemented: :kind/var
;   #'kindly.grammars/Table
```

## Plots

Plots have the richest grammar.
Two popular grammars for plotting are Vega and ggplot2.

```clojure
^:kind/vega
{}

;=> Unimplemented: :kind/vega
;   {}
```

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

;=> Unimplemented: :kind/buffered-image
;   #object[java.awt.image.BufferedImage 0x10378c35 "BufferedImage@10378c35: type = 6 ColorModel: #pixelBits = 32 numComponents = 4 color space = java.awt.color.ICC_ColorSpace@480e8a7a transparency = 3 has alpha = true isAlphaPre = false ByteInterleavedRaster: width = 256 height = 256 #numDataElements 4 dataOff[0] = 3"]
```

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

;=> "## This... is... :Markdown"
```

## Data

No grammar is necessary for primitives and collections.

```clojure
{:this #{"is"}
 'data [1 2 3 nil]}

;=> Unimplemented: :kind/map
;   {:this #{"is"}, data [1 2 3 nil]}
```

One challenge is when the data is very large.
Ideally some preview would be available.
