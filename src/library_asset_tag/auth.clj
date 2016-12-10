(ns library-asset-tag.auth
  (:require [buddy.auth.accessrules :refer [error]]
            [buddy.sign.jws :as jws]
            [buddy.core.codecs :as codecs]
            [buddy.core.codecs.base64 :as b64]
            [clojure.string :as string]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(defn get-login [session]
  (if-let [token (:identity session)]
    token
    {:status 401}))

(defn decode [s]
  (-> s b64/decode codecs/bytes->str (json/parse-string true)))

(defn login [session token]
  (let [{:keys [status body]} (http/get (str "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" token))]

    (if (= status 200)

      (let [desc (json/parse-string body true)]
        (println "token:" desc)
        {:status 200
         :session (assoc session :identity desc)})

      ;; else
      {:status status
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
