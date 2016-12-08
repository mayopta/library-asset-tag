(ns library-asset-tag.handler
  (:require [library-asset-tag.auth :as auth]
            [library-asset-tag.inventory :as inventory]
            [library-asset-tag.sequence :as sequence]
            [library-asset-tag.db :as db]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.coercions :refer [as-int]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.session :refer [wrap-session]]
            [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [restrict success error]]))

(defroutes secure-api-routes
  (context "/inventory" []
           (GET "/" [& params] (inventory/get params))
           (POST "/" {uri :uri} (inventory/allocate uri))
           (GET "/:id" [id :<< as-int] (inventory/get-by-id id)))
  (context "/config" []
           (context "/next" []
                    (GET "/" [] (sequence/get))
                    (POST "/" [value :<< as-int] (sequence/set value)))))

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

(defroutes main-routes
  api-routes
  (route/resources "/")
  (route/not-found "Page not found"))

(def backend (backends/session))

(def app
  (-> (wrap-defaults main-routes api-defaults)
      wrap-session
      (wrap-authentication backend)
      (wrap-authorization backend)))

(defn mock-init []
  (db/connect "datomic:mem:/library-asset-tag"))
