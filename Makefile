LEIN = ./lein

all:

target/library-asset-tag-standalone.jar:
	$(LEIN) uberjar

docker: target/library-asset-tag-standalone.jar
	docker build -t mayopta/library-asset-tag .

clean:
	$(LEIN) clean
