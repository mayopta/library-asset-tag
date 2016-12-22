(ns library-asset-tag.auth
  (:require [buddy.auth.accessrules :refer [error]]
            [clojure.string :as string]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [slingshot.slingshot :as slingshot]))

(defn- token-response [token]
  {:status 200
   :content-type "application/json"
   :body (json/generate-string token)})

(defn get-login [session]
  (if-let [token (:identity session)]
    (token-response token)
    {:status 401}))

(defn login [session token]
  (slingshot/try+
   (let [{:keys [body]} (http/get (str "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" token))]
     (let [desc (json/parse-string body true)]
       (-> (token-response desc)
           (assoc :session (assoc session :identity desc)))))
   (catch Object e
     {:status 400
      :body "Error validating login token"})))

(defn logout [session]
  (if (:identity session)
    {:status 200
     :session (dissoc session :identity)}
    {:status 401}))

(defn authenticated? [req]
  (if (-> req :session :identity)
    true
    (error "User must be authenticated")))

(defn no-restrictions [req] true)
