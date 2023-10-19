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

# Babashka notebooks

Babashka is, by design, as close to Clojure as possible.

## Basic Examples

```clojure
(+ 1 2)
```

3

```clojure
{:x (range 3)}
```

> ```clojure
> {:x (0 1 2)}
> ```

## Hiccup

```clojure
^:kind/hiccup
[:div {:style {:background-color "#effeef"}}
 [:big [:big 3]]]
```

[:div {:style {:background-color "#effeef"}} [:big [:big 3]]]

## Time

```clojure
(def now (java.time.ZonedDateTime/now))
```

#'user/now

```clojure
(def LA-timezone (java.time.ZoneId/of "America/Los_Angeles"))
```

#'user/LA-timezone

```clojure
(def LA-time (.withZoneSameInstant now LA-timezone))
```

#'user/LA-time

```clojure
(def pattern (java.time.format.DateTimeFormatter/ofPattern "HH:mm"))
```

#'user/pattern

```clojure
(.format LA-time pattern)
```

15:49

## Babashka vs clojure

The notable differences are:

* Code is evaluated with [Sci](https://github.com/babashka/SCI)
* Not all of Clojure is available
* Files start with a shell directive instead of a namespace

Did you know that Clojure treats `#!` as a comment?

So you can already create Babashka notebooks if you ignore the differences,
but this project (claykind) will detect Babashka and use Sci,
which will make it more directly compatible.

Would it be interesting thing to try is running claykind from babashka?
What possibilities does that open up?
Faster command-line blog generation?
