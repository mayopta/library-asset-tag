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

(defn allocate []
  (let [conn (database/get-connection)
        db (datomic/db conn)
        id (tempid :db.part/user)
        result (datomic/transact conn [[:alloc-assetid id {}]])]

    (println "result:" result)
    (let [assetid (-> (datomic/pull db [:inventory/assetid] id)
                      :inventory/assetid)]
      (println "assetid:" assetid)
      (str assetid))))

(defn get-by-id [id]
  (str "Hello " id))
