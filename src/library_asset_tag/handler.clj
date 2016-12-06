(ns library-asset-tag.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- get-inventory-summary []
  (str "Summary:"))

(defn- get-inventory-range [start end]
  (str "Returning range " start (when end (str "-" end))))

(defn- get-inventory [{:keys [summary start end] :as params}]
  (if (= summary "true")
    (get-inventory-summary)
    (get-inventory-range start end)))

(defroutes api-routes
  (context "/api/v1" []
           (context "/inventory" []
                    (GET "/" [& params] (get-inventory params))
                    (GET "/:id" [id] (str "Hello " id)))
           (route/not-found "Not Found")))

(def app
  (wrap-defaults api-routes site-defaults))
