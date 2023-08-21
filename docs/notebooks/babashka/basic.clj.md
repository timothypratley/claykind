#!/usr/bin/env bb

```clojure
(def now (java.time.ZonedDateTime/now))
```
```
=>
#'test.basic/now
```

```clojure
(def LA-timezone (java.time.ZoneId/of "America/Los_Angeles"))
```
```
=>
#'test.basic/LA-timezone
```

```clojure
(def LA-time (.withZoneSameInstant now LA-timezone))
```
```
=>
#'test.basic/LA-time
```

```clojure
(def pattern (java.time.format.DateTimeFormatter/ofPattern "HH:mm"))
```
```
=>
#'test.basic/pattern
```

(println (.format LA-time pattern))