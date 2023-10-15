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
(ns blog.hiccup-flavors
  "Wherein we explore several flavors of hiccup"
  (:require [hiccup2.core :as hiccup2]
            [lambdaisland.hiccup :as lhiccup]
            [huff.core :as hhiccup]
            [scicloj.kind-hiccup.api :as khiccup]
            [scicloj.kindly.v4.kind :as kind]))
```

|test|
|----|
|<h2>foo</h2><img src="https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg">|
|test|

```clojure
(def hiccup-implementations
  [{:name   "Hiccup"
    :author "James Reeves (weavejester)"
    :id     'hiccup/hiccup
    :url    "https://github.com/weavejester/hiccup"
    :features #{"fragments"}}

   {:name   "LambdaIsland Hiccup"
    :author "Arne Brasseur (plexus)"
    :id     'com.lambdaisland/hiccup
    :url    "https://github.com/lambdaisland/hiccup"
    :platforms #{"Clojure"}
    :features #{"auto-escape strings"
                "fragments"
                "components"
                "style maps"
                "unsafe strings"
                "kebab-case"}}

   {:name   "Huff"
    :author "Bryan Maass (escherize)"
    :id     'io.github.escherize/huff
    :url    "https://github.com/escherize/huff"
    :perf {:runtime 1
           :compiled 1}
    :tests {}
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

   {:name   "Reagent"
    :author "Dan Holmsand (holmsand)"
    :id     'reagent/reagent
    :url    "https://github.com/reagent-project/reagent"
    :features #{"ClojureScript"}}

   {:name   "kind-hiccup"
    :author "Timothy Pratley"
    :id     'org.scicloj/kind-hiccup
    :url    "https://github.com/timothypratley/claykind"
    :features #{"Babashka"}}])
```

#'blog.hiccup-flavors/hiccup-implementations

```clojure
(kind/table
  {:column-names ["name" "author" "project" "features"]
   :row-vectors  (for [{:keys [name author id url features]} hiccup-implementations]
                   [name author (kind/hiccup [:a {:href url} id]) features])})
```

| name | author | project | features |
| ---- | ---- | ---- | ---- |
| Hiccup | James Reeves (weavejester) | <a href="https://github.com/weavejester/hiccup">hiccup/hiccup</a> | <div class="kind_set"><div style="border:1px solid grey;padding:2px;">fragments</div></div> |
| LambdaIsland Hiccup | Arne Brasseur (plexus) | <a href="https://github.com/lambdaisland/hiccup">com.lambdaisland/hiccup</a> | <div class="kind_set"><div style="border:1px solid grey;padding:2px;">auto-escape strings</div><div style="border:1px solid grey;padding:2px;">kebab-case</div><div style="border:1px solid grey;padding:2px;">fragments</div><div style="border:1px solid grey;padding:2px;">components</div><div style="border:1px solid grey;padding:2px;">style maps</div><div style="border:1px solid grey;padding:2px;">unsafe strings</div></div> |
| Huff | Bryan Maass (escherize) | <a href="https://github.com/escherize/huff">io.github.escherize/huff</a> | <div class="kind_set"><div style="border:1px solid grey;padding:2px;">(...) fragments</div><div style="border:1px solid grey;padding:2px;">Extreme shorthand syntax [:. {:color :red}]</div><div style="border:1px solid grey;padding:2px;">Parse tags in any order :div#id.c or :div.c#id</div><div style="border:1px solid grey;padding:2px;">extendable grammar</div><div style="border:1px solid grey;padding:2px;">components</div><div style="border:1px solid grey;padding:2px;">[:&lt;&gt; ...] fragments</div><div style="border:1px solid grey;padding:2px;">HTML-encoded by default</div><div style="border:1px solid grey;padding:2px;">Babashka</div><div style="border:1px solid grey;padding:2px;">style maps</div><div style="border:1px solid grey;padding:2px;">unsafe strings</div></div> |
| Reagent | Dan Holmsand (holmsand) | <a href="https://github.com/reagent-project/reagent">reagent/reagent</a> | <div class="kind_set"><div style="border:1px solid grey;padding:2px;">ClojureScript</div></div> |
| kind-hiccup | Timothy Pratley | <a href="https://github.com/timothypratley/claykind">org.scicloj/kind-hiccup</a> | <div class="kind_set"><div style="border:1px solid grey;padding:2px;">Babashka</div></div> |

```clojure
[:div "hello world" ['(fn [] [:div [myjscomponent]])]]
```

[:div "hello world" [(fn [] [:div [myjscomponent]])]]

TODO: these should just be data!

```clojure
^:kind/table
{:row []}
```

|  |
|  |

| Library | Platforms | Features
|---
| Hiccup | Clojure |
| LambdaIsland Hiccup | Clojure
| Huff | Clojure, Babashka
| kind-hiccup | Clojure, Babashka | |
| Reagent | ClojureScript |

ESCAPING and security
Handling of raw strings

something that just expands kinds might be better?

```clojure
(defn html [x]
  [(str (hiccup2/html x))
   (khiccup/html x)
   (lhiccup/render x {:doctype? false})
   (hhiccup/html x)])
```

#'blog.hiccup-flavors/html

```clojure
(html [:div "Hello" [:em "World"]])
```

["<div>Hello<em>World</em></div>" "<div>Hello<em>World</em></div>" "<div>Hello<em>World</em></div>" "<div>Hello<em>World</em></div>"]
