---
format:
  html: {toc: true, theme: spacelab}
highlight-style: solarized
---

<style>
.printedClojure .sourceCode {
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

<script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script><script src="portal-main.js" type="text/javascript"></script>

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

<div class="printedClojure">

```clojure
nil
```

</div>

```clojure
(defn pr-str-with-meta [value]
(binding [*print-meta* true]
  (pr-str value)))
```

<div class="printedClojure">

```clojure
"#'user/pr-str-with-meta"
```

</div>

```clojure
(defn portal-widget [value]
(kind/hiccup
 [:div
  [:script (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {\"mime\": \"x-application/edn\",
                 \"text\": (() => " (pr-str (pr-str-with-meta value)) ")}
                , document.currentScript.parentElement);")]]))
```

<div class="printedClojure">

```clojure
"#'user/portal-widget"
```

</div>

## Data preparation

```clojure
(def git-log
(-> (shell/sh "git" "log" "--date=format:%Y-%m-%d")
    :out
    (str/split #"\n")
    kind/pprint))
```

<div class="printedClojure">

```clojure
"#'user/git-log"
```

</div>

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

<div class="printedClojure">

```clojure
"#'user/dates-and-freqs"
```

</div>

## Data exploration

```clojure
(-> dates-and-freqs
    (with-meta {:portal.viewer/default
                :portal.viewer/table})
    portal-widget)
```

<div><script>portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {&quot;mime&quot;: &quot;x-application/edn&quot;,
                 &quot;text&quot;: (() =&gt; &quot;^#:portal.viewer{:default :portal.viewer/table} ({:freq 1, :date \&quot;2023-08-08\&quot;} {:freq 2, :date \&quot;2023-08-14\&quot;} {:freq 1, :date \&quot;2023-08-17\&quot;} {:freq 2, :date \&quot;2023-08-18\&quot;} {:freq 9, :date \&quot;2023-08-19\&quot;} {:freq 1, :date \&quot;2023-08-20\&quot;} {:freq 5, :date \&quot;2023-08-21\&quot;} {:freq 2, :date \&quot;2023-08-22\&quot;} {:freq 1, :date \&quot;2023-08-23\&quot;} {:freq 1, :date \&quot;2023-09-11\&quot;} {:freq 4, :date \&quot;2023-09-12\&quot;} {:freq 3, :date \&quot;2023-09-13\&quot;} {:freq 3, :date \&quot;2023-09-14\&quot;} {:freq 3, :date \&quot;2023-09-15\&quot;} {:freq 9, :date \&quot;2023-09-18\&quot;} {:freq 4, :date \&quot;2023-09-19\&quot;} {:freq 7, :date \&quot;2023-09-20\&quot;} {:freq 4, :date \&quot;2023-09-21\&quot;} {:freq 1, :date \&quot;2023-09-26\&quot;})&quot;)}
                , document.currentScript.parentElement);</script></div>

## Plotting

```clojure
(def freqs-plot
  (kind/vega-lite
   {:data {:values dates-and-freqs}
    :mark :bar
    :encoding {:x {:field :date
                   :type :temporal}
               :y {:field :freq
                   :type :quantitative}}
    :width :container
    :height 200
    :background :floralwhite}))
```

<div class="printedClojure">

```clojure
"#'user/freqs-plot"
```

</div>

```clojure
freqs-plot
```

<div style="width:100%;"><script>vegaEmbed(document.currentScript.parentElement, {&quot;encoding&quot;:{&quot;y&quot;:{&quot;field&quot;:&quot;freq&quot;,&quot;type&quot;:&quot;quantitative&quot;},&quot;x&quot;:{&quot;field&quot;:&quot;date&quot;,&quot;type&quot;:&quot;temporal&quot;}},&quot;mark&quot;:&quot;bar&quot;,&quot;width&quot;:&quot;container&quot;,&quot;background&quot;:&quot;floralwhite&quot;,&quot;height&quot;:200,&quot;data&quot;:{&quot;values&quot;:[{&quot;freq&quot;:1,&quot;date&quot;:&quot;2023-08-08&quot;},{&quot;freq&quot;:2,&quot;date&quot;:&quot;2023-08-14&quot;},{&quot;freq&quot;:1,&quot;date&quot;:&quot;2023-08-17&quot;},{&quot;freq&quot;:2,&quot;date&quot;:&quot;2023-08-18&quot;},{&quot;freq&quot;:9,&quot;date&quot;:&quot;2023-08-19&quot;},{&quot;freq&quot;:1,&quot;date&quot;:&quot;2023-08-20&quot;},{&quot;freq&quot;:5,&quot;date&quot;:&quot;2023-08-21&quot;},{&quot;freq&quot;:2,&quot;date&quot;:&quot;2023-08-22&quot;},{&quot;freq&quot;:1,&quot;date&quot;:&quot;2023-08-23&quot;},{&quot;freq&quot;:1,&quot;date&quot;:&quot;2023-09-11&quot;},{&quot;freq&quot;:4,&quot;date&quot;:&quot;2023-09-12&quot;},{&quot;freq&quot;:3,&quot;date&quot;:&quot;2023-09-13&quot;},{&quot;freq&quot;:3,&quot;date&quot;:&quot;2023-09-14&quot;},{&quot;freq&quot;:3,&quot;date&quot;:&quot;2023-09-15&quot;},{&quot;freq&quot;:9,&quot;date&quot;:&quot;2023-09-18&quot;},{&quot;freq&quot;:4,&quot;date&quot;:&quot;2023-09-19&quot;},{&quot;freq&quot;:7,&quot;date&quot;:&quot;2023-09-20&quot;},{&quot;freq&quot;:4,&quot;date&quot;:&quot;2023-09-21&quot;},{&quot;freq&quot;:1,&quot;date&quot;:&quot;2023-09-26&quot;}]}});</script></div>

```clojure
(portal-widget freqs-plot)
```

<div><script>portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {&quot;mime&quot;: &quot;x-application/edn&quot;,
                 &quot;text&quot;: (() =&gt; &quot;^#:kindly{:kind :kind/vega-lite} {:encoding {:y {:field :freq, :type :quantitative}, :x {:field :date, :type :temporal}}, :mark :bar, :width :container, :background :floralwhite, :height 200, :data {:values ({:freq 1, :date \&quot;2023-08-08\&quot;} {:freq 2, :date \&quot;2023-08-14\&quot;} {:freq 1, :date \&quot;2023-08-17\&quot;} {:freq 2, :date \&quot;2023-08-18\&quot;} {:freq 9, :date \&quot;2023-08-19\&quot;} {:freq 1, :date \&quot;2023-08-20\&quot;} {:freq 5, :date \&quot;2023-08-21\&quot;} {:freq 2, :date \&quot;2023-08-22\&quot;} {:freq 1, :date \&quot;2023-08-23\&quot;} {:freq 1, :date \&quot;2023-09-11\&quot;} {:freq 4, :date \&quot;2023-09-12\&quot;} {:freq 3, :date \&quot;2023-09-13\&quot;} {:freq 3, :date \&quot;2023-09-14\&quot;} {:freq 3, :date \&quot;2023-09-15\&quot;} {:freq 9, :date \&quot;2023-09-18\&quot;} {:freq 4, :date \&quot;2023-09-19\&quot;} {:freq 7, :date \&quot;2023-09-20\&quot;} {:freq 4, :date \&quot;2023-09-21\&quot;} {:freq 1, :date \&quot;2023-09-26\&quot;})}}&quot;)}
                , document.currentScript.parentElement);</script></div>

--------------------------------------------------
![babashka](https://avatars.githubusercontent.com/u/64927540?s=200&v=4){height=128}
![claykind](https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg){height=128}
![portal](https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg){height=128}
![quarto](https://avatars.githubusercontent.com/u/67437475?s=200&v=4){height=128}
