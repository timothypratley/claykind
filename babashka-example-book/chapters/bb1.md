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
<script src="https://scicloj.github.io/scittle/js/scittle.js" type="text/javascript"></script>
<script type="application/x-scittle">(require
  '[reagent.core :as r]
  '[reagent.dom :as dom]
  '[clojure.str :as str])
</script>

# More experiments

```clojure
(range 9)
```

<div class="kind_seq"><div style="border:1px solid grey;padding:2px;">8</div><div style="border:1px solid grey;padding:2px;">7</div><div style="border:1px solid grey;padding:2px;">6</div><div style="border:1px solid grey;padding:2px;">5</div><div style="border:1px solid grey;padding:2px;">4</div><div style="border:1px solid grey;padding:2px;">3</div><div style="border:1px solid grey;padding:2px;">2</div><div style="border:1px solid grey;padding:2px;">1</div><div style="border:1px solid grey;padding:2px;">0</div></div>

```clojure
{:x (range 3)}
```

<div class="kind_map"><div style="border:1px solid grey;padding:2px;">:x</div><div style="border:1px solid grey;padding:2px;"><div class="kind_seq"><div style="border:1px solid grey;padding:2px;">2</div><div style="border:1px solid grey;padding:2px;">1</div><div style="border:1px solid grey;padding:2px;">0</div></div></div></div>
