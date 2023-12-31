(ns kindly.grammars
  (:require [clojure.java.io :as io]
            [malli.core :as ma])
  (:import (javax.imageio ImageIO)))

;; # Kindly grammars

;; Notebooks and rich REPLs visualize data and objects.
;; Kindly seeks to establish a standard way for users to request visualizations.

;; Nesting is an important consideration.
;; For example a table might contain an image in a cell.

;; ## HTML (hiccup)

;; HTML is the most flexible visualization,
;; as you can represent pretty much anything that can display in a browser.

(def hiccup-table
  ^:kindly/hiccup
  [:table
   [:tr [:td 1] [:td 2]]
   [:tr [:td 3] [:td 4]]])

;; The downside of HTML is that users need to expend considerable effort reshaping their data into views.

;; Hiccup has a Malli schema that can be used to validate and parse:

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

;; Hiccup does not check for the creation of valid HTML; tags and attributes are not checked.

(def parse-hiccup (ma/parser Hiccup))

(parse-hiccup hiccup-table)

;; ## Tables

;; Tables are interesting because one can imagine users with data in many different shapes;
;; vectors of vectors, vectors of maps, Tablecloth datasets, or maybe something else.

^:kind/table
{:headers     []
 :row-vectors []}

^:kind/table
{:headers     []
 :row-maps []}

^:kind/table
[[1 2]
 [3 4]]

;; It would be convenient to allow users several options for table inputs,
;; and some transformations to standardize them for downstream tools.
;; Perhaps this can be achieved with a shorthand helper?

'(tabulate values)
'(tabulate headers values)

;; Possibly a multimethod (with a row multimethod as well).

;; Tools should be encouraged to accept a standardized data shape defined by a schema:

(def Table
  [:schema {:registry {"table" ['...]}}
   "table"])

;; ## Plots

;; Plots have the richest grammar.
;; Two popular grammars for plotting are Vega and ggplot2.

(defn vega-lite-point-plot [data]
  ^:kind/vega-lite
  {:data {:values data},
   :mark "point"
   :encoding
   {:size {:field "w" :type "quantitative"}
    :x    {:field "x", :type "quantitative"},
    :y    {:field "y", :type "quantitative"},
    :fill {:field "z", :type "nominal"}}})

(defn random-data [n]
  (->> (repeatedly n #(- (rand) 0.5))
       (reductions +)
       (map-indexed (fn [x y]
                      {:w (rand-int 9)
                       :z (rand-int 9)
                       :x x
                       :y y}))))

(defn random-vega-lite-plot [n]
  (-> n
      random-data
      vega-lite-point-plot))

^:kind/vega-lite
(random-vega-lite-plot 9)

;; Vega has json-schemas available which are comprehensive.
;; There is not yet a way to create Malli schemas from Vega json-schema.
;; We could use a json-schema library instead,
;; or improve Malli schema conversion.

;; ## Images

;; No grammar is necessary for images.

;; Images can be specified using Markdown syntax:
;;
;;     ![a heart](claykind.png)
;;
;; ![a heart](../../../claykind.png)
;;
;; Managing the path to images can be challenging for users.

;; It is nice to be able to use "send form to Portal" or similar, can this be done with Markdown?

(ImageIO/read (io/file "claykind.png"))

;; Users may benefit from a shorthand helper function `(image "claykind.png")`.

;; sometimes the filename will be calculated programmatically,
;; which would be teadious to achieve with markdown.

;; ## Reagent components

;; A grammar is not possible for functions, or Reagent components.

;; ## Tablecloth datasets

;; Tablecloth datasets are identifiable by their type.

;; ## Markdown

;; Markdown comes in different flavours,
;; for example GitHub Markdown and Quarto (qmd).
;; Downstream tools may benefit from a hint as to which flavor should be used.
;; Users probably don't want to have to think too much about this though.

;; Of particular interest is the display of mathematical equations,
;; which often require plugin support.

;; $ f(x) = x^2 $

;; Should render as an equation.

;; Somewhat surprisingly, we cannot rely entirely on comments to represent Markdown.
;; We might need to construct Markdown programmatically.

^:kindly/markdown
(str "## This... is... " :Markdown)

;; ## Data

;; No grammar is necessary for primitives and collections.

{:this #{"is"}
 'data [1 2 3 nil]}

;; One challenge is when the data is very large.
;; Ideally some preview would be available.
