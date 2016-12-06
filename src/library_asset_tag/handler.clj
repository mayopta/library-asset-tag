(ns library-asset-tag.handler
  (:require [library-asset-tag.auth :as auth]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.session :refer [wrap-session]]
            [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [restrict success error]]))

(defn- get-inventory-summary []
  (str "Summary:"))

(defn- get-inventory-range [start end]
  (str "Returning range " start (when end (str "-" end))))

(defn- get-inventory [{:keys [summary start end] :as params}]
  (if (= summary "true")
    (get-inventory-summary)
    (get-inventory-range start end)))

(defroutes secure-api-routes
  (context "/inventory" []
           (GET "/" [& params] (get-inventory params))
           (GET "/:id" [id] (str "Hello " id))))

(defroutes login-api-routes
  (context "/login" []
           (GET "/" {session :session} (auth/get-login session))
           (PUT "/" [token :as {session :session}] (auth/login session token))
           (DELETE "/" {session :session} (auth/logout session))))

(defroutes api-routes
  (context "/api/v1" []
           (restrict login-api-routes {:handler auth/no-restrictions})
           (restrict secure-api-routes {:handler auth/authenticated?})
           (route/not-found "Not Found")))

;; Create an instance
(def backend (backends/session))

(def app
  (-> (wrap-defaults api-routes api-defaults)
      wrap-session
      (wrap-authentication backend)
      (wrap-authorization backend)))
