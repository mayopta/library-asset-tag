(ns library-asset-tag.inventory
  (:require [library-asset-tag.db :as database]
            [datomic.api :refer [tempid] :as datomic]))

(defn- get-summary []
  (str "Summary:"))

(defn- get-range [start end]
  (str "Returning range " start (when end (str "-" end))))

(defn get [{:keys [summary start end] :as params}]
  (if (= summary "true")
    (get-summary)
    (get-range start end)))

(defn allocate [uri]
  (let [conn (database/get-connection)
        invid (tempid :db.part/user)
        {:keys [db-after tempids]} (->> [[:alloc-assetid invid {}]]
                                        (datomic/transact conn)
                                        deref)
        assetid (->> invid
                     (datomic/resolve-tempid db-after tempids)
                     (datomic/entity db-after)
                     :inventory/assetid)]

    {:status 201
     :headers {"Location" (str uri "/" assetid)}}))

(defn get-by-id [id]
  (if-let [entity (-> (database/get-connection)
                      datomic/db
                      (datomic/entity [:inventory/assetid id]))]
    {:status 200}
    {:status 404}))
