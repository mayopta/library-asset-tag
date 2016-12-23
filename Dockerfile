FROM alpine
RUN apk update && \
    apk --no-cache add openjdk8-jre-base

ENV LAT_HTTP_PORT 3000
ENV LAT_DB_URL "datomic:mem:/library-asset-tag"
ENV LAT_JAVA_OPTS -server -Xmx256m
ENV LAT_DATOMIC_OBJECTCACHE 64m

COPY target/library-asset-tag-standalone.jar /usr/local/bin/library-asset-tag.jar
CMD java $LAT_JAVA_OPTS \
         -Ddatomic.objectCacheMax=$LAT_DATOMIC_OBJECTCACHE \
         -jar /usr/local/bin/library-asset-tag.jar \
              --port $LAT_HTTP_PORT --db-url $LAT_DB_URL
EXPOSE 3000
