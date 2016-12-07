(ns library-asset-tag.db
  (:refer-clojure :exclude [print update get ])
  (:require  [datomic.api :refer [q db] :as d]
             [clojure.pprint :refer :all]))

(def handle (atom {}))

;;------------------------------------------------------
;; define our transaction functions
;;------------------------------------------------------
(def alloc-assetid
  "Atomically increment the next sequence and simultaneously create an inventory record"
  #db/fn {:lang :clojure
          :params [db id context]
          :code (let [entity (d/entity db :sequence/id)
                      assetid (or (:sequence/next entity) 1)]
                  [{:db/id :sequence/id
                    :sequence/next (inc assetid)}
                   {:db/id id
                    :inventory/assetid assetid}])})

;;------------------------------------------------------
;; install-schema - initializes a new database by installing
;;                  our schema and transaction functions
;;------------------------------------------------------
(defn- install-schema [conn]
  (d/transact conn
              [
               ;;------------------------------------------------------
               ;; install our schema
               ;;------------------------------------------------------

               ;; inventory schema
               {:db/id (d/tempid :db.part/db)
                :db/ident :inventory/assetid
                :db/valueType :db.type/long
                :db/cardinality :db.cardinality/one
                :db/unique :db.unique/identity
                :db/index true
                :db/doc "A unique identifier for this asset in inventory"
                :db.install/_attribute :db.part/db}
               {:db/id (d/tempid :db.part/db)
                :db/ident :inventory/creator
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/doc "The principal that created the asset tag"
                :db.install/_attribute :db.part/db}
               {:db/id (d/tempid :db.part/db)
                :db/ident :inventory/notes
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/doc "Optional notes attached to the allocation"
                :db.install/_attribute :db.part/db}

               ;; sequence allocation for assets
               {:db/id (d/tempid :db.part/db)
                :db/ident :sequence/id
                :db/doc "The identity of our sequence records"}
               {:db/id (d/tempid :db.part/db)
                :db/ident :sequence/next
                :db/valueType :db.type/long
                :db/cardinality :db.cardinality/one
                :db/doc "The value of our next sequence to allocate"
                :db.install/_attribute :db.part/db}

               ;;------------------------------------------------------
               ;; install our transaction-functions
               ;;------------------------------------------------------
               {:db/id (d/tempid :db.part/user)
                :db/ident :alloc-assetid
                :db/fn alloc-assetid}]))

(defn connect [url]
  (let [init (d/create-database url)
        conn (d/connect url)]
    (when init
      (install-schema conn))
    (swap! handle assoc :url url :conn conn)))

(defn get-connection []
  (:conn @handle))
