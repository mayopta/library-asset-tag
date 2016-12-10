(defproject library-asset-tag "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [compojure "1.5.1"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [buddy/buddy-auth "1.3.0"]
                 [com.datomic/datomic-free "0.9.5407"]
                 [clj-http "3.4.1"]
                 [cheshire "5.6.3"]
                 [slingshot "0.12.2"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:init library-asset-tag.handler/mock-init
         :handler library-asset-tag.handler/app}
  :main ^:skip-aot library-asset-tag.main
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]]}
             :uberjar {:aot :all}})
