(ns library-asset-tag.ui.client
  (:require [happy.core :as h :refer [GET PUT POST DELETE]]
            [happy.client.xmlhttprequest :as hc]
            [promesa.core :as p :include-macros true]))

(h/set-default-client! (hc/create))

(defn login! [idtoken]
  (p/do*
   (PUT "/api/v1/login" idtoken)
   true))

(defn create-inventory! []
  (p/promise
   (fn [resolve reject]
     (POST "/api/v1/inventory" {} ""
           {:handler (fn [{:keys [status headers] :as response}]

                       (if (= status 201)
                         (resolve (headers "location"))
                         (reject response)))}))))

(defn alloc! []
  (-> (create-inventory!)
      (p/then #(println "response:" %))))
