(ns library-asset-tag.auth
  (:require [buddy.auth.accessrules :refer [error]]))

(defn get-login [session]
  (println "get-login:" session)
  (if-let [token (:identity session)]
    token
    {:status 401}))

(defn login [session token]
  (println "login:" session)
  {:status 200
   :session (assoc session :identity token)})

(defn logout [session]
  (println "logout:" session)
  (if (:identity session)
    {:status 200
     :session (dissoc session :identity)}
    {:status 401}))

(defn authenticated? [req]
  (if (-> req :session :identity)
    true
    (error "User must be authenticated")))

(defn no-restrictions [req] true)
