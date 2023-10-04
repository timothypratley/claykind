---
format:
  html: {toc: true, theme: spacelab}
highlight-style: solarized
---

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
</style>

<script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script><script src="https://unpkg.com/react@18/umd/react.production.min.js" type="text/javascript"></script><script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js" type="text/javascript"></script><script src="https://scicloj.github.io/scittle/js/scittle.js" type="text/javascript"></script><script src="https://scicloj.github.io/scittle/js/scittle.reagent.js" type="text/javascript"></script><script src="/js/portal-main.js" type="text/javascript"></script>
<script type="application/x-scittle">(ns main
                      (:require [reagent.core :as r]
                                [reagent.dom :as dom]))</script>

# Analysing git logs in babashka

In this notebook, we will analyze our git log.
We will check how many commits we have had on each day
and will plot the results as a time series.

![babashka](https://avatars.githubusercontent.com/u/64927540?s=200&v=4){height=128}
![claykind](https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg){height=128}
![portal](https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg){height=128}
![quarto](https://avatars.githubusercontent.com/u/67437475?s=200&v=4){height=128}

## Setup

This is a Babashka Kindly notebook

that gets rendered in Claykind

as a Quarto document

with embedded Portal viewers.

```clojure
(require '[scicloj.kindly.v4.kind :as kind])
```

```clojure {.printedClojure}
nil
```

```clojure
(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))
```

```clojure {.printedClojure}
"#'user/pr-str-with-meta"
```

TODO: do we need a portal kind?
TODO: scripts should be raw

```clojure
(defn portal-widget [value]
  (kind/hiccup
    [:div
     [:script
      [:hiccup/raw-html
       (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {\"mime\": \"x-application/edn\",
                 \"text\": (() => " (pr-str (pr-str-with-meta value)) ")}
                , document.currentScript.parentElement);")]]]))
```

```clojure {.printedClojure}
"#'user/portal-widget"
```

## Data preparation

```clojure
(def git-log
  (-> (shell/sh "git" "log" "--date=format:%Y-%m-%d")
      :out
      (str/split #"\n")
      kind/pprint))
```

```clojure {.printedClojure}
"#'user/git-log"
```

```clojure
(def dates-and-freqs
  (->> git-log
       (filter (partial re-matches #"^Date:.*"))
       (map (fn [line]
              (-> line
                  (str/replace #"Date:   " ""))))
       frequencies
       (map (fn [[date freq]]
              {:date date
               :freq freq}))
       (sort-by :date)))
```

```clojure {.printedClojure}
"#'user/dates-and-freqs"
```

## Data exploration

```clojure
(-> dates-and-freqs
    (with-meta {:portal.viewer/default
                :portal.viewer/table})
    portal-widget)
```

<div><script>portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {"mime": "x-application/edn",
                 "text": (() => "^#:portal.viewer{:default :portal.viewer/table} ({:freq 1, :date \"2023-08-08\"} {:freq 2, :date \"2023-08-14\"} {:freq 1, :date \"2023-08-17\"} {:freq 2, :date \"2023-08-18\"} {:freq 9, :date \"2023-08-19\"} {:freq 1, :date \"2023-08-20\"} {:freq 5, :date \"2023-08-21\"} {:freq 2, :date \"2023-08-22\"} {:freq 1, :date \"2023-08-23\"} {:freq 1, :date \"2023-09-11\"} {:freq 4, :date \"2023-09-12\"} {:freq 3, :date \"2023-09-13\"} {:freq 3, :date \"2023-09-14\"} {:freq 3, :date \"2023-09-15\"} {:freq 9, :date \"2023-09-18\"} {:freq 4, :date \"2023-09-19\"} {:freq 7, :date \"2023-09-20\"} {:freq 4, :date \"2023-09-21\"} {:freq 1, :date \"2023-09-26\"} {:freq 1, :date \"2023-09-27\"} {:freq 1, :date \"2023-10-01\"} {:freq 2, :date \"2023-10-03\"})")}
                , document.currentScript.parentElement);</script></div>

## Plotting

```clojure
(def freqs-plot
  (kind/vega-lite
    {:data       {:values dates-and-freqs}
     :mark       :bar
     :encoding   {:x {:field :date
                      :type  :temporal}
                  :y {:field :freq
                      :type  :quantitative}}
     :width      :container
     :height     200
     :background :floralwhite}))
```

```clojure {.printedClojure}
"#'user/freqs-plot"
```

```clojure
freqs-plot
```

<div style="width:100%;"><script>vegaEmbed(document.currentScript.parentElement, {"encoding":{"y":{"field":"freq","type":"quantitative"},"x":{"field":"date","type":"temporal"}},"mark":"bar","width":"container","background":"floralwhite","height":200,"data":{"values":[{"freq":1,"date":"2023-08-08"},{"freq":2,"date":"2023-08-14"},{"freq":1,"date":"2023-08-17"},{"freq":2,"date":"2023-08-18"},{"freq":9,"date":"2023-08-19"},{"freq":1,"date":"2023-08-20"},{"freq":5,"date":"2023-08-21"},{"freq":2,"date":"2023-08-22"},{"freq":1,"date":"2023-08-23"},{"freq":1,"date":"2023-09-11"},{"freq":4,"date":"2023-09-12"},{"freq":3,"date":"2023-09-13"},{"freq":3,"date":"2023-09-14"},{"freq":3,"date":"2023-09-15"},{"freq":9,"date":"2023-09-18"},{"freq":4,"date":"2023-09-19"},{"freq":7,"date":"2023-09-20"},{"freq":4,"date":"2023-09-21"},{"freq":1,"date":"2023-09-26"},{"freq":1,"date":"2023-09-27"},{"freq":1,"date":"2023-10-01"},{"freq":2,"date":"2023-10-03"}]}});</script></div>

```clojure
(portal-widget freqs-plot)
```

<div><script>portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {"mime": "x-application/edn",
                 "text": (() => "^#:kindly{:kind :kind/vega-lite} {:encoding {:y {:field :freq, :type :quantitative}, :x {:field :date, :type :temporal}}, :mark :bar, :width :container, :background :floralwhite, :height 200, :data {:values ({:freq 1, :date \"2023-08-08\"} {:freq 2, :date \"2023-08-14\"} {:freq 1, :date \"2023-08-17\"} {:freq 2, :date \"2023-08-18\"} {:freq 9, :date \"2023-08-19\"} {:freq 1, :date \"2023-08-20\"} {:freq 5, :date \"2023-08-21\"} {:freq 2, :date \"2023-08-22\"} {:freq 1, :date \"2023-08-23\"} {:freq 1, :date \"2023-09-11\"} {:freq 4, :date \"2023-09-12\"} {:freq 3, :date \"2023-09-13\"} {:freq 3, :date \"2023-09-14\"} {:freq 3, :date \"2023-09-15\"} {:freq 9, :date \"2023-09-18\"} {:freq 4, :date \"2023-09-19\"} {:freq 7, :date \"2023-09-20\"} {:freq 4, :date \"2023-09-21\"} {:freq 1, :date \"2023-09-26\"} {:freq 1, :date \"2023-09-27\"} {:freq 1, :date \"2023-10-01\"} {:freq 2, :date \"2023-10-03\"})}}")}
                , document.currentScript.parentElement);</script></div>

--------------------------------------------------
![babashka](https://avatars.githubusercontent.com/u/64927540?s=200&v=4){height=128}
![claykind](https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg){height=128}
![portal](https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg){height=128}
![quarto](https://avatars.githubusercontent.com/u/67437475?s=200&v=4){height=128}
