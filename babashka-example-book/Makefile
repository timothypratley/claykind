.PHONY: all clean

all:
	clojure -M:dev -m scicloj.claykind.main
	quarto render

clean:
	rm -fr _book
