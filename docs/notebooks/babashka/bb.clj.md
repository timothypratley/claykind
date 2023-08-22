# Babashka notebooks

Babashka is, by design, as close to Clojure as possible.

clojure.lang.ExceptionInfo: Could not resolve symbol: java.time.ZonedDateTime/now {:type :sci/error, :line nil, :column nil, :file nil, :phase "analysis"}
clojure.lang.ExceptionInfo: Could not resolve symbol: java.time.ZoneId/of {:type :sci/error, :line nil, :column nil, :file nil, :phase "analysis"}
clojure.lang.ExceptionInfo: Method withZoneSameInstant on class sci.impl.vars.SciUnbound not allowed! {:type :sci/error, :line nil, :column nil, :file nil}
clojure.lang.ExceptionInfo: Could not resolve symbol: java.time.format.DateTimeFormatter/ofPattern {:type :sci/error, :line nil, :column nil, :file nil, :phase "analysis"}
clojure.lang.ExceptionInfo: Method format on class sci.impl.vars.SciUnbound not allowed! {:type :sci/error, :line nil, :column nil, :file nil}
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
