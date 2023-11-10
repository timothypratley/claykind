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
<script src="https://scicloj.github.io/scittle/js/scittle.js" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-lite@5" type="text/javascript"></script><script src="https://cdn.jsdelivr.net/npm/vega-embed@6" type="text/javascript"></script>
<script type="application/x-scittle">(require
  '[reagent.core :as r]
  '[reagent.dom :as dom]
  '[clojure.str :as str])
</script>

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

```clojure
(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))
```

TODO: do we need a portal kind?
TODO: scripts should be raw

```clojure
(defn portal-widget [value]
  (kind/hiccup
    [:div
     [:script
      (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {\"mime\": \"x-application/edn\",
                 \"text\": (() => " (pr-str (pr-str-with-meta value)) ")}
                , document.currentScript.parentElement);")]]))
```

## Data preparation

```clojure
(def git-log
  (-> (shell/sh "git" "log" "--date=format:%Y-%m-%d")
      :out
      (str/split #"\n")
      kind/pprint))
```

> ```clojure {.printedClojure}
> ["commit c58b1303881b3ad1a1586c1970245597f9ab770a"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-31"
>  ""
>  "    refactoring to kindly-render"
>  ""
>  "commit 561c24d022a9c2967bc3bd4c527e29755473d504"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-23"
>  ""
>  "    removing unnecessary helpers"
>  ""
>  "commit b5722f44d7a0fe28907ac592996666aa68d81246"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-23"
>  ""
>  "    better exception handling"
>  ""
>  "commit 5c177335cba48e0b5d24c772d385f8ef06ff3344"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-22"
>  ""
>  "    update build version"
>  ""
>  "commit b2d12cd48c3897a1675b5558af5f9bea0df79da1"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-22"
>  ""
>  "    add example usage of reagent"
>  ""
>  "commit 537dd00554a32f3899fe01d2fe84fcff128ec6d7"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-22"
>  ""
>  "    switched to using simpler reagent/scittle"
>  ""
>  "commit 82ab9c84a5a243fccba69bd7acc30a1edf7b2ae6"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-22"
>  ""
>  "    initial expanding hiccup instead of compiling"
>  ""
>  "commit 9baede1cae4578a20de9f0f5e8a2667f17c613e0"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-20"
>  ""
>  "    make targets a map of target-dir to option"
>  ""
>  "commit 944f9927932fdc17e6a52598a4b0fb3fb92a6204"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-20"
>  ""
>  "    improving options"
>  ""
>  "commit 2f2d43a2473e968722c909583276bda3ea64fd5b"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-19"
>  ""
>  "    fix markdown should show html sometimes"
>  ""
>  "commit a19a9a4d9d1b55abc900f3926a956d58f047695a"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-18"
>  ""
>  "    fix stackoverflow on ^:kind/md [\"string\"]"
>  ""
>  "commit c40e0c7da34bf89bc0ad7a2c5946b06499de9b02"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-14"
>  ""
>  "    nest html inside markdown tables"
>  ""
>  "commit 6dd52a8d9891229c8dcc4a2980baec1e4022fcd9"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-13"
>  ""
>  "    cleanup changes"
>  ""
>  "commit b1748b4a30596a5ec1277e27c23136ca390e20a7"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-12"
>  ""
>  "    initial idea for kind-hiccup"
>  ""
>  "commit c9deb7c4721b6bb015a27828991576e75c5d7f71"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-04"
>  ""
>  "    try a different result style"
>  ""
>  "commit 9f80a6cd736dfc45beefdeaaef90d47d0b8065af"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-04"
>  ""
>  "    note test #2"
>  ""
>  "commit 27764309245508db483c3efa2f87f23aa59c4361"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-04"
>  ""
>  "    try note"
>  ""
>  "commit 3421fe5f16ad59ee5a821bbed13137206076883d"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-03"
>  ""
>  "    use {.printedClojure} for better compatibility"
>  ""
>  "commit 9c1be31a69e3555c590b53cbd87d7f5466f205bf"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-03"
>  ""
>  "    allow notebook markdown to generate docs"
>  ""
>  "commit eebd1447fd7c2b065f014a9c3e122a75faff371e"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-03"
>  ""
>  "    fix error handler for safe-read"
>  ""
>  "commit d8f591e1b2bd387fd575d9c63649748f9b25e8ff"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-10-01"
>  ""
>  "    recursive hiccup/kinds"
>  ""
>  "commit db71566411d178dbbbae69ae2781aebc331edfe5"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-27"
>  ""
>  "    fix front-matter formatting"
>  ""
>  "commit f53486c74eac87c801fcd5e9ee4f4144df9bb951"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-26"
>  ""
>  "    Defined options and 3 ways (file, cli, args)"
>  ""
>  "commit 76876255e3837fe5f6e35053d97343fba423ed1d"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-21"
>  ""
>  "    Include vector/map/set styles for quarto"
>  ""
>  "commit e30f17eae4e6240ceecf8e40db91cfa67a9ee619"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-21"
>  ""
>  "    updated babashka examples"
>  ""
>  "commit 187816f8d76eb47a37c29767d088845ac9d7c098"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-21"
>  ""
>  "    updated babashka example"
>  ""
>  "commit e5feac10b9c71bebc9a358048ab7eb7dd354165b"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-21"
>  ""
>  "    updated babashka examples"
>  ""
>  "commit 8d16573ae26e94d0ce193d0e5875310b61c7e99b"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    updated babashka notebook examples"
>  ""
>  "commit eaf9046b27689d21e0d7a9e1ffcaa8a91bba7533"
>  "Merge: 056caf5 34da2a5"
>  "Author: Daniel Slutsky <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    Merge pull request #6 from timothypratley/bb-eval-fix"
>  "    "
>  "    Bb eval fix"
>  ""
>  "commit 34da2a517c9bb382ff4b53d5373ba00b514e9c6f"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    updating the babashka notebook example"
>  ""
>  "commit e5017c4dddf0c3cceb4cb50f37c19b3f87cdf793"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    making sure we eval things in babashka correctly"
>  ""
>  "commit 056caf5e63016d8bd8300990e3584b7251aa6fad"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    babashka working!!! :) yay"
>  ""
>  "commit 055ac7f44e82e2f008dec7673bf4a5488b968d82"
>  "Merge: 5579442 dbf40ee"
>  "Author: Daniel Slutsky <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    Merge pull request #5 from timothypratley/add-dot-nojekyll"
>  "    "
>  "    adding .nojekyll -- see https://stackoverflow.com/a/6398875"
>  ""
>  "commit dbf40eebda64d487aee3be8a841971febee76898"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-20"
>  ""
>  "    adding .nojekyll -- see https://stackoverflow.com/a/6398875"
>  ""
>  "commit 557944280d14c904e69f0aa7192d3eb721ddacc8"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-19"
>  ""
>  "    adds the generated book"
>  ""
>  "commit 1d0cedd24af7b4f519093f616123fe67a7527b24"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-19"
>  ""
>  "    initial babashka-example-book"
>  ""
>  "commit 99240abae3a5cf7779caba8f54ab619c687bc12d"
>  "Merge: 32e7ac8 5fa8ef7"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-19"
>  ""
>  "    Merge pull request #4 from daslu/added-chapter"
>  "    "
>  "    added a chapter to the book example"
>  ""
>  "commit 5fa8ef7e1845f76214f7678f59feedcec642b593"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-19"
>  ""
>  "    added a chapter to the book example"
>  ""
>  "commit 32e7ac8c284ea8f2ff55e016bba13877b6b9cc94"
>  "Merge: 6925672 ec0db27"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    Merge pull request #3 from daslu/qmd-adapters"
>  "    "
>  "    progress with qmd adapters"
>  ""
>  "commit ec0db2778d3bbdaf6c51b1ba92b186d8e3a0c5e3"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    progress with qmd adapters"
>  ""
>  "commit 6925672480e8fd5ec67d130dfe7ce6a239691950"
>  "Merge: 1fbd136 1ff9856"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    Merge pull request #2 from daslu/qmd-bb-poc"
>  "    "
>  "    poc: qmd books for babashka"
>  ""
>  "commit 1ff9856d7e71f17abb32ea3e5de1e633be523191"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    book setup (WIP)"
>  ""
>  "commit 526ffb55e215de5073b307675f30d8cc23913eca"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    added some structure to the bb example notebook"
>  ""
>  "commit b5be17cb2032c1403d3388f8e52b1091b928c46f"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    added a (WIP) function to render qmd for quarto books"
>  ""
>  "commit e6c9efaca6ebb3651c26fcaa42e28e59aaf4c7e2"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    updated deps, added clj-yaml"
>  ""
>  "commit 1e53100c0092a19234ffa81e1d001f72da01cde6"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    adding yaml header to qmd files (generated from edn)"
>  ""
>  "commit 1fbd136bf2975be876838c0c7f2aa64b7fe5ac5a"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-18"
>  ""
>  "    styling data structures"
>  ""
>  "commit a1941d9c80b3e26dcd9dfcbed348e3f371837de6"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-15"
>  ""
>  "    initial html data viewer"
>  ""
>  "commit ecefd3b37ff8127e51b4f8cd523cdd60daf2c445"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-15"
>  ""
>  "    Add initial markdown handling"
>  ""
>  "commit 4c63b66119d4bad6d84e77d4561e11c8dd564d12"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-15"
>  ""
>  "    use raw JavaScript instead of skittle"
>  ""
>  "commit c3bf7bab0e2c1c744604b6828d7e652b7162b81e"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-14"
>  ""
>  "    inital javascript only vega"
>  ""
>  "commit a599c663b4663f809197b63958b57c3e564a579b"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-14"
>  ""
>  "    fix interface for format notebook, add vega in plain"
>  ""
>  "commit 0d61b9c1e4f3a222ce54b2d09c11a527e67700c3"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-14"
>  ""
>  "    fix portal widget"
>  ""
>  "commit 019b606d254f19dd7282c8c8aaac8524ff66f9c3"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-13"
>  ""
>  "    attempt to use emmy"
>  ""
>  "commit 97fcb94e5803ffea6f24ebaa7c96b87d7ca9bf59"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-13"
>  ""
>  "    trying to capture stdout stderr"
>  ""
>  "commit 381b2dbeec96ca26bf5de26b5269a14cd5299cdb"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-13"
>  ""
>  "    fix generate errors"
>  ""
>  "commit d77fbe3eae848ed270c4009ce1cce2526742d7aa"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-12"
>  ""
>  "    improve tests"
>  ""
>  "commit 4212e502843a6f9e6f94aa4af0784dfea7f79231"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-12"
>  ""
>  "    fix publish/main"
>  ""
>  "commit b03d09ec46483ca860c765f39eddde03d32b3ce1"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-12"
>  ""
>  "    colocate target page output code"
>  ""
>  "commit b21da08ca419929dbcaed093bbbbb374b245a458"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-12"
>  ""
>  "    reorganize around adapters"
>  ""
>  "commit 8eec37cf5128581c7c1eaded1b323af5e5afd5c0"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-09-11"
>  ""
>  "    initial idea for preparing values"
>  ""
>  "commit 2f2c926a7e13375eb7d24c657d82a11ac4345c2b"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-23"
>  ""
>  "    improve markdown produced"
>  ""
>  "commit 1702cd220f63726d4a525d416a42f24e183f34f1"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-22"
>  ""
>  "    fix portal demo"
>  ""
>  "commit 954d05a0ca24b16d455b89ad8579c0f7a0645521"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-22"
>  ""
>  "    version bump kind-portal"
>  ""
>  "commit f1dcc1294ebb76363d24d5876c942654b24a85a1"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-21"
>  ""
>  "    show errors when present"
>  ""
>  "commit 6024b37ef82e66a49846496f5673ee40db171f33"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-21"
>  ""
>  "    remove first line of babashka notebooks"
>  ""
>  "commit c4d26c9d7185435ba3a1d6e4832df3dfdb06ede5"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-21"
>  ""
>  "    use kindly to infer kinds"
>  ""
>  "commit bd521a3e21c72a53f1a4f24bd999cc0bccd9527f"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-21"
>  ""
>  "    fix parse-forms self recursion"
>  ""
>  "commit b1803f2eec00857f44438d15d0654450a077d568"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-21"
>  ""
>  "    initial idea for babashka support"
>  ""
>  "commit 54f0c3ca65b93da0228e95df168759188ed2437e"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-20"
>  ""
>  "    add grammar notebook"
>  ""
>  "commit 35982c7312305c55c7b3161adc9e96697f84b502"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    add initial portal example"
>  ""
>  "commit 1879bc4cc62281da7000cd8d3f39b91280509644"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    adds ci build"
>  ""
>  "commit 2796d8feca9e59c32f8374a603dd5eaeb5afba95"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    publish example only for markdown"
>  ""
>  "commit e247db9d4579af63d61a09b7c64242d3ef48d107"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    incorporate some ideas from chouser"
>  ""
>  "commit 2ab3a332124919e0fddabc68d58f2b61ed201a2e"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    improve publish example whitespace handling"
>  ""
>  "commit 1911d8493a797a8822e0a3959c6b5e6d1d027b15"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    fix markdown generation"
>  ""
>  "commit 33ffec433de9780da106d4ab316eac6aa7f8c464"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    handle functions as values"
>  ""
>  "commit cc92f8b149076ef957ab95abcf2c7486ab74188f"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    use \"advice\" and \"context\""
>  ""
>  "commit 2ae05874ac3cc4cea85c51f0f7cbd13642b3fd50"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-19"
>  ""
>  "    break the steps of the pipeline into namespaces"
>  ""
>  "commit 2004b1e5fd735ded340974cf58e68d23b7193acc"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-18"
>  ""
>  "    add initial incomplete publish example"
>  ""
>  "commit 9f44627a54b818cb42717ba77f2ce0c8800603aa"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-18"
>  ""
>  "    add example usage"
>  ""
>  "commit ea37c8f9b55429edb91ad0211fdcbf4ceaad7522"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-17"
>  ""
>  "    minor fixes"
>  ""
>  "commit 2a5dc77bc7bee22343c29d2120e4aa7dbbcb6d97"
>  "Merge: a0e191b a7c89c8"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-14"
>  ""
>  "    Merge pull request #1 from daslu/main"
>  "    "
>  "    reader impl using rewrite-clj"
>  ""
>  "commit a7c89c8dc93c217892ded3d90cb2a2fe7c5dfc4b"
>  "Author: daslu <daniel.slutsky@gmail.com>"
>  "Date:   2023-08-14"
>  ""
>  "    reader impl using rewrite-clj"
>  ""
>  "commit a0e191b019e7a4d8c9d779c63e840575f553a045"
>  "Author: Timothy Pratley <timothypratley@gmail.com>"
>  "Date:   2023-08-08"
>  ""
>  "    initial project creation"]
> ```

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

## Data exploration

```clojure
(-> dates-and-freqs
    (with-meta {:portal.viewer/default
                :portal.viewer/table})
    portal-widget)
```

<div><script>portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {"mime": "x-application/edn",
                 "text": (() => "^#:portal.viewer{:default :portal.viewer/table} ({:freq 1, :date \"2023-08-08\"} {:freq 2, :date \"2023-08-14\"} {:freq 1, :date \"2023-08-17\"} {:freq 2, :date \"2023-08-18\"} {:freq 9, :date \"2023-08-19\"} {:freq 1, :date \"2023-08-20\"} {:freq 5, :date \"2023-08-21\"} {:freq 2, :date \"2023-08-22\"} {:freq 1, :date \"2023-08-23\"} {:freq 1, :date \"2023-09-11\"} {:freq 4, :date \"2023-09-12\"} {:freq 3, :date \"2023-09-13\"} {:freq 3, :date \"2023-09-14\"} {:freq 3, :date \"2023-09-15\"} {:freq 9, :date \"2023-09-18\"} {:freq 4, :date \"2023-09-19\"} {:freq 7, :date \"2023-09-20\"} {:freq 4, :date \"2023-09-21\"} {:freq 1, :date \"2023-09-26\"} {:freq 1, :date \"2023-09-27\"} {:freq 1, :date \"2023-10-01\"} {:freq 3, :date \"2023-10-03\"} {:freq 3, :date \"2023-10-04\"} {:freq 1, :date \"2023-10-12\"} {:freq 1, :date \"2023-10-13\"} {:freq 1, :date \"2023-10-14\"} {:freq 1, :date \"2023-10-18\"} {:freq 1, :date \"2023-10-19\"} {:freq 2, :date \"2023-10-20\"} {:freq 4, :date \"2023-10-22\"} {:freq 2, :date \"2023-10-23\"} {:freq 1, :date \"2023-10-31\"})")}
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

<div style="width:100%;"><script>vegaEmbed(document.currentScript.parentElement, {"encoding":{"y":{"field":"freq","type":"quantitative"},"x":{"field":"date","type":"temporal"}},"mark":"bar","width":"container","background":"floralwhite","height":200,"data":{"values":[{"freq":1,"date":"2023-10-31"},{"freq":2,"date":"2023-10-23"},{"freq":4,"date":"2023-10-22"},{"freq":2,"date":"2023-10-20"},{"freq":1,"date":"2023-10-19"},{"freq":1,"date":"2023-10-18"},{"freq":1,"date":"2023-10-14"},{"freq":1,"date":"2023-10-13"},{"freq":1,"date":"2023-10-12"},{"freq":3,"date":"2023-10-04"},{"freq":3,"date":"2023-10-03"},{"freq":1,"date":"2023-10-01"},{"freq":1,"date":"2023-09-27"},{"freq":1,"date":"2023-09-26"},{"freq":4,"date":"2023-09-21"},{"freq":7,"date":"2023-09-20"},{"freq":4,"date":"2023-09-19"},{"freq":9,"date":"2023-09-18"},{"freq":3,"date":"2023-09-15"},{"freq":3,"date":"2023-09-14"},{"freq":3,"date":"2023-09-13"},{"freq":4,"date":"2023-09-12"},{"freq":1,"date":"2023-09-11"},{"freq":1,"date":"2023-08-23"},{"freq":2,"date":"2023-08-22"},{"freq":5,"date":"2023-08-21"},{"freq":1,"date":"2023-08-20"},{"freq":9,"date":"2023-08-19"},{"freq":2,"date":"2023-08-18"},{"freq":1,"date":"2023-08-17"},{"freq":2,"date":"2023-08-14"},{"freq":1,"date":"2023-08-08"}]}});</script></div>

```clojure
freqs-plot
```

<div style="width:100%;"><script>vegaEmbed(document.currentScript.parentElement, {"encoding":{"y":{"field":"freq","type":"quantitative"},"x":{"field":"date","type":"temporal"}},"mark":"bar","width":"container","background":"floralwhite","height":200,"data":{"values":[{"freq":1,"date":"2023-10-31"},{"freq":2,"date":"2023-10-23"},{"freq":4,"date":"2023-10-22"},{"freq":2,"date":"2023-10-20"},{"freq":1,"date":"2023-10-19"},{"freq":1,"date":"2023-10-18"},{"freq":1,"date":"2023-10-14"},{"freq":1,"date":"2023-10-13"},{"freq":1,"date":"2023-10-12"},{"freq":3,"date":"2023-10-04"},{"freq":3,"date":"2023-10-03"},{"freq":1,"date":"2023-10-01"},{"freq":1,"date":"2023-09-27"},{"freq":1,"date":"2023-09-26"},{"freq":4,"date":"2023-09-21"},{"freq":7,"date":"2023-09-20"},{"freq":4,"date":"2023-09-19"},{"freq":9,"date":"2023-09-18"},{"freq":3,"date":"2023-09-15"},{"freq":3,"date":"2023-09-14"},{"freq":3,"date":"2023-09-13"},{"freq":4,"date":"2023-09-12"},{"freq":1,"date":"2023-09-11"},{"freq":1,"date":"2023-08-23"},{"freq":2,"date":"2023-08-22"},{"freq":5,"date":"2023-08-21"},{"freq":1,"date":"2023-08-20"},{"freq":9,"date":"2023-08-19"},{"freq":2,"date":"2023-08-18"},{"freq":1,"date":"2023-08-17"},{"freq":2,"date":"2023-08-14"},{"freq":1,"date":"2023-08-08"}]}});</script></div>

```clojure
(portal-widget freqs-plot)
```

<div><script>portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {"mime": "x-application/edn",
                 "text": (() => "^#:kindly{:kind :kind/vega-lite} {:encoding {:y {:field :freq, :type :quantitative}, :x {:field :date, :type :temporal}}, :mark :bar, :width :container, :background :floralwhite, :height 200, :data {:values ({:freq 1, :date \"2023-08-08\"} {:freq 2, :date \"2023-08-14\"} {:freq 1, :date \"2023-08-17\"} {:freq 2, :date \"2023-08-18\"} {:freq 9, :date \"2023-08-19\"} {:freq 1, :date \"2023-08-20\"} {:freq 5, :date \"2023-08-21\"} {:freq 2, :date \"2023-08-22\"} {:freq 1, :date \"2023-08-23\"} {:freq 1, :date \"2023-09-11\"} {:freq 4, :date \"2023-09-12\"} {:freq 3, :date \"2023-09-13\"} {:freq 3, :date \"2023-09-14\"} {:freq 3, :date \"2023-09-15\"} {:freq 9, :date \"2023-09-18\"} {:freq 4, :date \"2023-09-19\"} {:freq 7, :date \"2023-09-20\"} {:freq 4, :date \"2023-09-21\"} {:freq 1, :date \"2023-09-26\"} {:freq 1, :date \"2023-09-27\"} {:freq 1, :date \"2023-10-01\"} {:freq 3, :date \"2023-10-03\"} {:freq 3, :date \"2023-10-04\"} {:freq 1, :date \"2023-10-12\"} {:freq 1, :date \"2023-10-13\"} {:freq 1, :date \"2023-10-14\"} {:freq 1, :date \"2023-10-18\"} {:freq 1, :date \"2023-10-19\"} {:freq 2, :date \"2023-10-20\"} {:freq 4, :date \"2023-10-22\"} {:freq 2, :date \"2023-10-23\"} {:freq 1, :date \"2023-10-31\"})}}")}
                , document.currentScript.parentElement);</script></div>

--------------------------------------------------
![babashka](https://avatars.githubusercontent.com/u/64927540?s=200&v=4){height=128}
![claykind](https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg){height=128}
![portal](https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg){height=128}
![quarto](https://avatars.githubusercontent.com/u/67437475?s=200&v=4){height=128}
