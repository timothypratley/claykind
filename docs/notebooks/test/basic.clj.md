(ns test.basic)

```clojure
(+ 1 2 3)
```
```
=>
6
```

# section 1

 asss


```clojure
(defn f [x]
  (+ x ; hmm
     ;; hmmm
     9))
```
```
=>
#'test.basic/f
```

```clojure
(f 20)
```
```
=>
29
```

# section 2


       ad
a


```clojure
^:kindly/table
{:rows ["a" "b" "c"]}
```
```
=>
{:rows ["a" "b" "c"]}
```

```clojure
^:kindly/table
{:headers [:a :b]
 :rows    [{:a "a" :b "b"}]}
```
```
=>
{:headers [:a :b], :rows [{:b "b", :a "a"}]}
```

```clojure
^:kindly/table-matrix
[["a" "b" "c"]]
```
```
=>
[["a" "b" "c"]]
```

add things that translate to the existing kindly specs
BUT! I have this different thing
SVG images, tables that have double rows
Pushing handling code out the display tools
The categories of features


Possible feature: Order of evaluation
present the last form first!
Just reverse the contexts.

|  |  |  |
|--|--|--|
| "a" | "b" | "c" |

standards to abide by for tool creators

* unknown tags render the content of the tags
* noscript

css allows fallback tags
anomalies library is interesting
ring was enough and won
adapter to portal
portal is not only a tool, it is a specification (could be a kind)
configure which viewer to render with?
tim: make example to portal
