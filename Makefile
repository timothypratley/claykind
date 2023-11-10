.PHONY: all notebooks babashka-example-book how-to-code-a-book test build deploy clean

all: test notebooks babashka-example-book how-to-code-a-book build

test:
	clojure -M:dev:test -m cognitect.test-runner

notebooks:
	clojure -M:dev -m scicloj.claykind.main

babashka-example-book:
	cd babashka-example-book && $(MAKE)

how-to-code-a-book:
	cd how-to-code-a-book && $(MAKE)

build:
	clojure -T:dev build/jar

deploy:
	clojure -T:dev build/deploy

clean:
	rm -fr target
