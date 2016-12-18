(ns library-asset-tag.ui.auth
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.client :as client]
            [om.next :as om]
            [promesa.core :as p :include-macros true]))

(def client_id
  "20638887916-bqrsujb3thodlq4pno9l1kukcrnmlr4b.apps.googleusercontent.com")

(defn inst [] (.. js/gapi.auth2 (getAuthInstance)))

(defn- update [signedin]
  (om/transact! core/reconciler (if signedin
                                  '[(login {:status :logged-in})]
                                  '[(login {:status :logged-out})])))

(defn refresh []
  (let [auth2 (inst)]
    (if-let [signedin (.get auth2.isSignedIn)]
      (let [idtoken (-> auth2.currentUser
                        .get
                        .getAuthResponse
                        (js->clj :keywordize-keys true)
                        :id_token)]
        (-> (client/login! idtoken)
            (p/then update)))

      ;; else
      (update false))))

(defn login []
  (let [auth2 (inst)]
    (.. auth2 (signIn) (then refresh))))

(defn logout []
  (let [auth2 (inst)]
    (.. auth2 (signOut) (then refresh))))

(defn- _init []
  (.. js/gapi.auth2 (init #js {:client_id client_id
                               :scope "profile email"
                               :hosted_domain "mayopta.com"}))
  (let [auth2 (inst)]
    (.then auth2 refresh)))

(defn init []
  (.. js/gapi (load "auth2" _init)))
