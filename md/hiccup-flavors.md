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
<script src="portal-main.js" type="text/javascript"></script><script src="https://scicloj.github.io/scittle/js/scittle.js" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script><script src="https://unpkg.com/react@18/umd/react.production.min.js" type="text/javascript"></script><script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js" type="text/javascript"></script><script src="https://scicloj.github.io/scittle/js/scittle.reagent.js" type="text/javascript"></script>
<script type="application/x-scittle">(require
  '[reagent.core :as r]
  '[reagent.dom :as dom]
  '[clojure.str :as str])
</script>

# Hiccup flavors

This article examines the performance, error reporting, and output of several Hiccup implementations.

![Hiccup is concise](https://i.redd.it/59i7rh6wt3271.jpg)

```clojure
(ns blog.hiccup-flavors
  "Wherein we explore several flavors of hiccup"
  (:require [hiccup2.core :as hiccup2]
            [lambdaisland.hiccup :as lhiccup]
            [huff.core :as hhiccup]
            [scicloj.kind-hiccup.api :as khiccup]
            [scicloj.kindly.v4.kind :as kind]))
```

> **exception**
> 
> ```
> Could not locate scicloj/kind_hiccup/api__init.class, scicloj/kind_hiccup/api.clj or scicloj/kind_hiccup/api.cljc on classpath. Please check that namespaces with dashes use underscores in the Clojure file name.
> ```

```clojure
(kind/md "hello world")
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

## What is Hiccup?

Hiccup is an approach to creating HTML strings.
Hiccup uses vectors to represent HTML elements,
and maps to represent an element's attributes.

```clojure
(def my-div [:div {:style {:color "green"}}
             "Hello "
             [:em "World"]])
```

This data-structure can be compiled to an HTML string

```clojure
(str (hiccup2/html my-div))
```

<div style="color:green;">Hello <em>World</em></div>

And if we view the HTML in a browser, it will render it like so:

```clojure
(kind/hiccup my-div)
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

The transformation was:

```
input data: [:tag {:attr "value"} ...child-elements...]
output string: <tag attr="value">...child-elements...</tag>
```

Here is a how we can construct a table

```clojure
(def my-table
  [:table
   [:thead
    [:tr [:th "header1"] [:th "header2"]]]
   [:tbody
    [:tr [:td 1] [:td (inc 2)]]
    [:tr [:td (Math/sqrt 3)] [:td (Math/pow 2 2)]]]])
```

```clojure
(str (hiccup2/html my-table))
```

<table><thead><tr><th>header1</th><th>header2</th></tr></thead><tbody><tr><td>1</td><td>3</td></tr><tr><td>1.7320508075688772</td><td>4.0</td></tr></tbody></table>

Notice that the in the string version it is harder to see the closing tags.
Moreover, if you were editing the string, it is difficult to manage the hierarchy.
One of the advantages of using a data-structure is that you can use structural editing to modify it.

Here's what the resulting HTML looks like:

```clojure
(kind/hiccup my-table)
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

## Templating

Did you notice in the table that we inserted some calculations?
That wouldn't be possible when editing a string of HTML.
Using a data-structure to represent HTML allows us to intermix computation.
Doing so is a form of templating.
Hiccup leverages Clojure's data-literals to allow us to mix code and data.

Without Hiccup, the string based approach to templating is to write a string like so:

`"<div>Hello {{name}}<div>"`

Templates are a mix of HTML and code which is easy to get wrong.
And render it using a map of names to values like `{:name "World"}`.
String templates and variable maps are more difficult to manage
than just creating the data-structure you wanted in the first place.

Whereas with hiccup it's impossible to have unbalanced tags,
and the code semantics are clear.
There is no new language, you just use the host language.
So that's another advantage to using Hiccup.

## Flavors

Hiccup quickly became a popular way for creating HTML in the Clojure community,
and several different implementations have sprung forth each with some extra features or goals.

```clojure
(def hiccup-implementations
  [{:name     "Hiccup"
    :author   "James Reeves (weavejester)"
    :id       'hiccup/hiccup
    :url      "https://github.com/weavejester/hiccup"
    :features #{"fragments"}}

   {:name     "LambdaIsland Hiccup"
    :author   "Arne Brasseur (plexus)"
    :id       'com.lambdaisland/hiccup
    :url      "https://github.com/lambdaisland/hiccup"
    :features #{"auto-escape strings"
                "fragments"
                "components"
                "style maps"
                "unsafe strings"
                "kebab-case"}}

   {:name     "Huff"
    :author   "Bryan Maass (escherize)"
    :id       'io.github.escherize/huff
    :url      "https://github.com/escherize/huff"
    :perf     {:runtime  1
               :compiled 1}
    :tests    {}
    :features #{"extendable grammar"
                "unsafe strings"
                "components"
                "style maps"
                "HTML-encoded by default"
                "Parse tags in any order :div#id.c or :div.c#id"
                "Babashka"
                "[:<> ...] fragments"
                "(...) fragments"
                "Extreme shorthand syntax [:. {:color :red}]"}}

   {:name     "Reagent"
    :author   "Dan Holmsand (holmsand)"
    :id       'reagent/reagent
    :url      "https://github.com/reagent-project/reagent"
    :features #{"ClojureScript"}}

   {:name     "kind-hiccup"
    :author   "Timothy Pratley"
    :id       'org.scicloj/kind-hiccup
    :url      "https://github.com/timothypratley/claykind"
    :features #{"Babashka"}}])
```

IDEA: feature matrix instead?

```clojure
(kind/table
  {:column-names ["project" "features"]
   :row-vectors  (for [{:keys [name author id url features]} hiccup-implementations]
                   [[name author (kind/hiccup [:a {:href url} id])] features])})
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

## Error Handling

## Security (avoiding XSS)

### Escaping

Handling of raw strings

## Extensibility

```clojure
[:div "hello world" ['(fn [] [:div [myjscomponent]])]]
```

<div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:div</div><div style="border:1px solid grey;padding:2px;">hello world</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div class="kind_seq"><div style="border:1px solid grey;padding:2px;">fn</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"></div></div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">:div</div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;">myjscomponent</div></div></div></div></div></div></div></div></div></div>

IDEA: something that just expands kinds might be better?

TODO: but why does this even work?
Markdown don't care, it's not escaped (but it could be)
What should this show?
<h3>I'm big</h3>
**should it** work?
Or should it be &lt;h3&gt;
what about 3 > 2?
or
```
3 > 2
```
Probably we don't want `<script>...</script>`,
but maybe we do? (after all we encourage scittle and reagent)

```clojure
(defn html [x]
  [(str (hiccup2/html x))
   (khiccup/html x)
   (lhiccup/render x {:doctype? false})
   (hhiccup/html x)])
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

```clojure
(html [:div "Hello" [:em "World"]])
```

> **exception**
> 
> ```
> Attempting to call unbound fn: #'blog.hiccup-flavors/html
> ```

```clojure
(def tests
  [[:div 'hello " world"]
   [:div :hello " world"]
   [:div #{"hello" "world"}]
   ['("hello" "world")]
   [{:a 1, :b 2}]])
```

```clojure
(str (hiccup2/html [:div 'hello " world"]))
```

<div>hello world</div>

```clojure
(str (hhiccup/html [:div 'hello " world"]))
```

> **exception**
> 
> ```
> Invalid huff form passed to html. See `huff.core/hiccup-schema` for more info
> ```

```clojure
(str (lhiccup/html [:div 'hello " world"]))
```

clojure.lang.LazySeq@19ee3a74

```clojure
(str (khiccup/html [:div 'hello " world"]))
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

```clojure
(str (hiccup2/html [:div :hello " world"]))
```

<div>hello world</div>

```clojure
(str (hhiccup/html [:div :hello " world"]))
```

> **exception**
> 
> ```
> Invalid huff form passed to html. See `huff.core/hiccup-schema` for more info
> ```

```clojure
(str (lhiccup/render [:div :hello " world"] {:doctype? false}))
```

<div>:hello world</div>

```clojure
(str (khiccup/html [:div :hello " world"]))
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

```clojure
(str (hiccup2/html [:div #{"hello" "world"}]))
```

<div>#{&quot;hello&quot; &quot;world&quot;}</div>

```clojure
(str (hhiccup/html [:div #{"hello" "world"}]))
```

> **exception**
> 
> ```
> Invalid huff form passed to html. See `huff.core/hiccup-schema` for more info
> ```

```clojure
(str (lhiccup/render [:div #{"hello" "world"}] {:doctype? false}))
```

<div>#{"hello" "world"}</div>

```clojure
(str (khiccup/html [:div #{"hello" "world"}]))
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

```clojure
(str (hiccup2/html ['(println "hello" "world")]))
```

> **exception**
> 
> ```
> Syntax error macroexpanding hiccup2/html at (0:0).
> ```

```clojure
(str (hhiccup/html ['(println "hello" "world")]))
```

world

```clojure
(str (lhiccup/render ['(println "hello" "world")] {:doctype? false}))
```

> **exception**
> 
> ```
> Not a valid hiccup tag
> ```

```clojure
(str (khiccup/html ['(println "hello" "world")]))
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

**kind-hiccup transformer**
"kind-hiccup" -> "standard hiccup"

```clojure
(str (khiccup/html ['(fn [] [:h1 "it works"])]))
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```

```clojure
(kind/hiccup ['(fn [] [:h1 "it works"])])
```

> **exception**
> 
> ```
> Syntax error compiling at (0:0).
> ```
