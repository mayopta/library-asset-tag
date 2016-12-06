(ns library-asset-tag.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.session :refer [wrap-session]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(defn- get-inventory-summary []
  (str "Summary:"))

(defn- get-inventory-range [start end]
  (str "Returning range " start (when end (str "-" end))))

(defn- get-inventory [{:keys [summary start end] :as params}]
  (if (= summary "true")
    (get-inventory-summary)
    (get-inventory-range start end)))

(defn- get-login [session]
  (println "get-login:" session)
  (if-let [token (:identity session)]
    token
    {:status 401}))

(defn- login [session token]
  (println "login:" session)
  {:status 200
   :session (assoc session :identity token)})

(defn- logout [session]
  (println "logout:" session)
  (if (:identity session)
    {:status 200
     :session (dissoc session :identity)}
    {:status 401}))

(defroutes secure-api-routes
  (context "/inventory" []
           (GET "/" [& params] (get-inventory params))
           (GET "/:id" [id] (str "Hello " id))))

(defroutes api-routes
  (context "/api/v1" []
           secure-api-routes
           (context "/login" []
                    (GET "/" {session :session} (get-login session))
                    (PUT "/" [token :as {session :session}] (login session token))
                    (DELETE "/" {session :session} (logout session)))
           (route/not-found "Not Found")))

;; Create an instance
(def backend (backends/session))

(def app
  (-> (wrap-defaults api-routes api-defaults)
      wrap-session
      (wrap-authentication backend)))
