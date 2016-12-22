(ns library-asset-tag.ui.auth
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.client :as client]
            [om.next :as om]
            [promesa.core :as p :include-macros true]))

(def client_id
  "20638887916-bqrsujb3thodlq4pno9l1kukcrnmlr4b.apps.googleusercontent.com")

(defn inst [] (.. js/gapi.auth2 (getAuthInstance)))

(defn- update [user]
  (om/transact! core/reconciler (if user
                                  `[(auth/login {:status :logged-in :user ~user})]
                                  '[(auth/login {:status :logged-out :user nil})])))

(defn- google-signedin? []
  (let [auth2 (inst)]
    (.get auth2.isSignedIn)))

(defn- google-signout! []
  (let [auth2 (inst)]
    (p/promise
     (fn [resolve reject]
       (.. auth2 (signOut) (then (resolve true)))))))

(defn- get-google-token []
  (let [auth2 (inst)]
    (p/do*
     (-> auth2.currentUser
         .get
         .getAuthResponse
         (js->clj :keywordize-keys true)
         :id_token))))

(defn refresh []
  (-> (client/get-login)
      (p/then (fn [response]

                (if (not= response false)
                  (update response)

                  ;;else
                  (if (google-signedin?)
                    (-> (get-google-token)
                        (p/then client/login!)
                        (p/then update))

                    ;;else
                    (update nil)))))))

(defn login []
  (let [auth2 (inst)]
    (.. auth2 (signIn) (then refresh))))

(defn logout []
  (-> (google-signout!)
      (p/then client/logout!)
      (p/then refresh)))

(defn- _init []
  (.. js/gapi.auth2 (init #js {:client_id client_id
                               :scope "profile email"
                               :hosted_domain "mayopta.com"}))
  (let [auth2 (inst)]
    (.then auth2 refresh)))

(defn init []
  (.. js/gapi (load "auth2" _init)))
