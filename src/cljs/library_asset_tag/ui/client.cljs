(ns library-asset-tag.ui.client
  (:require [library-asset-tag.ui.core :as core]
            [om.next :as om]
            [happy.core :as h :refer [GET PUT POST DELETE]]
            [happy.client.xmlhttprequest :as hc]
            [promesa.core :as p :include-macros true]
            [cognitect.transit :as t]))

(h/set-default-client! (hc/create))

(def tr (t/reader :json))
(defn- json-read [str] (t/read tr str))

(defn login! [idtoken]
  (p/promise
   (fn [resolve reject]
     (PUT "/api/v1/login" {} idtoken
          {:handler
           (fn [{:keys [status body] :as response}]
             (if (= status 200)
               (-> body json-read resolve)
               (reject response)))}))))

(defn logout! []
  (p/promise
   (fn [resolve reject]
     (DELETE "/api/v1/login" {}
          {:handler
           (fn [{:keys [status] :as response}]
             (if (= status 200)
               (resolve true)
               (reject response)))}))))

(defn get-login []
  (p/promise
   (fn [resolve reject]
     (GET "/api/v1/login" {}
          {:handler
           (fn [{:keys [body status] :as response}]
             (case status
               200 (-> body json-read resolve)
               401 (resolve false)
               (reject response)))}))))

(defn create-inventory! []
  (p/promise
   (fn [resolve reject]
     (POST "/api/v1/inventory" {} ""
           {:handler
            (fn [{:keys [status body] :as response}]
              (if (= status 201)
                (-> body json-read resolve)
                (reject response)))}))))

(defn alloc! []
  (-> (create-inventory!)
      (p/then
       (fn [{:strs [assetid] :as body}]
         (om/transact! core/reconciler `[(session/add-asset {:id ~assetid})])))))
