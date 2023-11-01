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
<script src="portal-main.js" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script>




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

<div class="kind_map"><div style="border:1px solid grey;padding:2px;">:x</div><div style="border:1px solid grey;padding:2px;"><div class="kind_seq"><div style="border:1px solid grey;padding:2px;">0</div><div style="border:1px solid grey;padding:2px;">1</div><div style="border:1px solid grey;padding:2px;">2</div></div></div></div>



## Hiccup

```clojure
^:kind/hiccup
[:div {:style {:background-color "#effeef"}}
 [:big [:big 3]]]
```

<div style="background-color:#effeef;"><big><big>3</big></big></div>



## Time

```clojure
(def now (java.time.ZonedDateTime/now))
```

> ```clojure {.printedClojure}
> #'user/now
> ```

```clojure
(def LA-timezone (java.time.ZoneId/of "America/Los_Angeles"))
```

> ```clojure {.printedClojure}
> #'user/LA-timezone
> ```

```clojure
(def LA-time (.withZoneSameInstant now LA-timezone))
```

> ```clojure {.printedClojure}
> #'user/LA-time
> ```

```clojure
(def pattern (java.time.format.DateTimeFormatter/ofPattern "HH:mm"))
```

> ```clojure {.printedClojure}
> #'user/pattern
> ```

```clojure
(.format LA-time pattern)
```

22:42



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
