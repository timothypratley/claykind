# Babashka notebooks

Babashka is, by design, as close to Clojure as possible.

```clojure
(def now (java.time.ZonedDateTime/now))

;=> Unimplemented: :kind/var
;   #'scicloj.clay.publish/now
```

```clojure
(def LA-timezone (java.time.ZoneId/of "America/Los_Angeles"))

;=> Unimplemented: :kind/var
;   #'scicloj.clay.publish/LA-timezone
```

```clojure
(def LA-time (.withZoneSameInstant now LA-timezone))

;=> Unimplemented: :kind/var
;   #'scicloj.clay.publish/LA-time
```

```clojure
(def pattern (java.time.format.DateTimeFormatter/ofPattern "HH:mm"))

;=> Unimplemented: :kind/var
;   #'scicloj.clay.publish/pattern
```

```clojure
(println (.format LA-time pattern))

;OUT 21:37

;=> nil
```

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
