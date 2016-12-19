(ns user
  (:require [library-asset-tag.core :as core]
            [library-asset-tag.handler :as handler]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]))

;; Let Clojure warn you when it needs to reflect on types, or when it does math
;; on unboxed numbers. In both cases you should add type annotations to prevent
;; degraded performance.
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(def http-handler
  (wrap-reload #'library-asset-tag.handler/app))

(defn run []
  (core/init)
  (handler/mock-init)
  (figwheel/start-figwheel!))

(def browser-repl figwheel/cljs-repl)
