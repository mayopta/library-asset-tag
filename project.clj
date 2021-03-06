(defproject library-asset-tag "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.6.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/core.async "0.2.395"]
                 [compojure "1.5.1"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [http-kit "2.2.0"]
                 [beckon "0.1.1"]
                 [buddy/buddy-auth "1.3.0"]
                 [com.datomic/datomic-free "0.9.5407"
                  :exclusions [com.google.guava/guava]]
                 [org.omcljs/om "1.0.0-alpha47"
                  :exclusions [cljsjs/react cljsjs/react-dom]]
                 [cljs-react-material-ui "0.2.33"]
                 [clj-http "3.4.1"]
                 [happy "0.5.2"]
                 [cheshire "5.6.3"]
                 [slingshot "0.12.2"]
                 [cljsjs/google-platformjs-extern "1.0.0-0"]
                 [funcool/promesa "1.7.0"]
                 [bk/ring-gzip "0.1.1"]
                 [clj-time "0.12.2"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [com.taoensso/timbre "4.8.0"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-environ "1.0.3"]]

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"]
  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :main ^:skip-aot library-asset-tag.main

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (run) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc"]

                :figwheel true
                ;; Alternatively, you can configure a function to run every time figwheel reloads.
                ;; :figwheel {:on-jsload "library-asset-tag.core/on-figwheel-reload"}

                :compiler {:main library-asset-tag.ui.init
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/library-asset-tag.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main library-asset-tag.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main library-asset-tag.ui.init
                           :output-to "resources/public/js/compiled/library-asset-tag.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             :server-port 3000
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Instead of booting a separate server on its own port, we embed
             ;; the server ring handler inside figwheel's http-kit server, so
             ;; assets and API endpoints can all be accessed on the same host
             ;; and port. If you prefer a separate server process then take this
             ;; out and start the server with `lein run`.
             :ring-handler user/http-handler

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile false}

  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]
                                  [figwheel "0.5.8"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.12"]]
                   :plugins [[lein-figwheel "0.5.4-4"]
                             [lein-doo "0.1.6"]]

                   :source-paths ["dev"]
                   :repl-options {:nrepl-middleware
                                  [cemerick.piggieback/wrap-cljs-repl]}}
             :uberjar {:source-paths ^:replace ["src/clj" "src/cljc"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :hooks []
                       :omit-source true
                       :uberjar-name "library-asset-tag-standalone.jar"
                       :aot :all}})
