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
            [scicloj.kind-hiccup.api :as khiccup]))
```

> **stderr**
> 
> WARNING: abs already refers to: #'clojure.core/abs in namespace: garden.color, being replaced by: #'garden.color/abs

> ```clojure
> nil
> ```

| Library | Author | project |
|---
| Hiccup | James Reeves (weavejester) | [hiccup/hiccup](https://github.com/weavejester/hiccup) |
| LambdaIsland Hiccup | Arne Brasseur (plexus) | [com.lambdaisland/hiccup](https://github.com/lambdaisland/hiccup) |
| Huff | Bryan (escherize) | [io.github.escherize/huff](https://github.com/escherize/huff) |
| kind-hiccup | | |
| Reagent | Dan Holmsand (holmsand) | https://github.com/reagent-project/reagent |

```clojure
[:div "hello world" ['(fn [] [:div [myjscomponent]])]]
```

<div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><code>:div</code></div><div style="border:1px solid grey;padding:2px;"><code>&quot;hello world&quot;</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><div><code>fn</code><div class="kind_vector"></div><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><code>:div</code></div><div style="border:1px solid grey;padding:2px;"><div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><code>myjscomponent</code></div></div></div></div></div></div></div></div></div>

TODO: these should just be data!

```clojure
^:kind/table
{:row []}
```

----

| Library | Platforms | Features
|---
| Hiccup | Clojure |
| LambdaIsland Hiccup | Clojure
| Huff | Clojure, Babashka
| kind-hiccup | Clojure, Babashka | |
| Reagent | ClojureScript |

ESCAPING and security

something that just expands kinds might be better?

```clojure
(defn html [x]
  [(str (hiccup2/html x))
   (khiccup/html x)
   (lhiccup/render x {:doctype? false})
   (hhiccup/html x)])
```

> ```clojure
> "#'blog.hiccup-flavors/html"
> ```

```clojure
(html [:div "Hello" [:em "World"]])
```

<div class="kind_vector"><div style="border:1px solid grey;padding:2px;"><code>&quot;&lt;div&gt;Hello&lt;em&gt;World&lt;/em&gt;&lt;/div&gt;&quot;</code></div><div style="border:1px solid grey;padding:2px;"><code>&quot;&lt;div&gt;Hello&lt;em&gt;World&lt;/em&gt;&lt;/div&gt;&quot;</code></div><div style="border:1px solid grey;padding:2px;"><code>&quot;&lt;div&gt;Hello&lt;em&gt;World&lt;/em&gt;&lt;/div&gt;&quot;</code></div><div style="border:1px solid grey;padding:2px;"><code>&quot;&lt;div&gt;Hello&lt;em&gt;World&lt;/em&gt;&lt;/div&gt;&quot;</code></div></div>
