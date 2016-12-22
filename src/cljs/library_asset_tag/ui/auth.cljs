(ns library-asset-tag.ui.auth
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.client :as client]
            [om.next :as om]
            [promesa.core :as p :include-macros true]))

(def client_id
  "20638887916-bqrsujb3thodlq4pno9l1kukcrnmlr4b.apps.googleusercontent.com")

(defn inst [] (.. js/gapi.auth2 (getAuthInstance)))

(defn- set-state! [user]
  (println "set-state:" user)
  (om/transact! core/reconciler (if user
                                  `[(auth/login {:status :logged-in :user ~user})]
                                  '[(auth/login {:status :logged-out :user nil})])))

(defn- google-signedin? []
  (let [auth2 (inst)]
    (.get auth2.isSignedIn)))

(defn- google-signin! []
  (let [auth2 (inst)]
    (p/promise
     (fn [resolve reject]
       (.. auth2 (signIn) (then #(resolve true)))))))

(defn- google-signout! []
  (let [auth2 (inst)]
    (p/promise
     (fn [resolve reject]
       (.. auth2 (signOut) (then #(resolve true)))))))

(defn- get-google-token []
  (let [auth2 (inst)]
    (p/do* (-> auth2.currentUser
               .get
               .getAuthResponse
               (js->clj :keywordize-keys true)
               :id_token))))

(defn- app-signin! [& args]
  (println "app-signin")
  (if (google-signedin?)
    (-> (get-google-token)
        (p/then client/login!))
    ;;else
    (p/promise nil)))

(defn login []
  (-> (google-signin!)
      (p/then app-signin!)
      (p/then set-state!)))

(defn logout []
  (set-state! nil)
  (p/all [(google-signout!)
          (client/logout!)]))

(defn- auth2-init []
  (p/promise
   (fn [resolve reject]
     (.. js/gapi.auth2 (init #js {:client_id client_id
                                  :scope "profile email"
                                  :hosted_domain "mayopta.com"}))
     (let [auth2 (inst)]
       (.then auth2 #(resolve true))))))

(defn- gapi-load [libname]
  (p/promise
   (fn [resolve reject]
     (.. js/gapi (load libname #(resolve true))))))

(defn init []
  (-> (gapi-load "auth2")
      (p/then auth2-init)
      (p/then client/get-login)
      (p/then set-state!)
      (p/catch #(set-state! nil))))
