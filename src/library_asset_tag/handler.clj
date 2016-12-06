(ns library-asset-tag.handler
  (:require [library-asset-tag.auth :as auth]
            [library-asset-tag.inventory :as inventory]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.session :refer [wrap-session]]
            [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [restrict success error]]))

(defroutes secure-api-routes
  (context "/inventory" []
           (GET "/" [& params] (inventory/get params))
           (GET "/:id" [id] (inventory/get-by-id id))))

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
