(ns library-asset-tag.db
  (:refer-clojure :exclude [print update get ])
  (:require  [datomic.api :refer [q db] :as d]
             [clojure.pprint :refer :all]))

(def handle (atom {}))

;;------------------------------------------------------
;; define our transaction functions
;;------------------------------------------------------
(def inc-version
  "Atomically increment the document version (or initialize to '1')"
  #db/fn {:lang :clojure
          :params [db id docid]
          :code (let [doc (d/entity db [:document/id docid])]
                  [{:db/id id
                    :document/id docid
                    :document/version (if-let [version (:document/version doc)]
                                        (inc version)
                                        1)}])})

(def update-entry
  "Update an entry in the document, inserting it if it doesn't already exist"
  #db/fn {:lang :clojure
          :params [db id docid name value]
          :code (if-let [entryid (q '[:find ?entry .
                                      :in $ ?docid ?name
                                      :where
                                      [?doc :document/id ?docid]
                                      [?doc :document/entries ?entry]
                                      [?entry :entry/name ?name]]
                                    db docid name)]
                  ;; the entry already exists, so simply update its value
                  [{:db/id entryid,
                    :entry/value value}]
                  ;; the entry doesn't exist, so we need to fully insert it
                  (let [entryid (d/tempid :db.part/user)]
                    [{:db/id entryid,
                      :entry/name name
                      :entry/value value}
                     {:db/id id
                      :document/id docid
                      :document/entries [entryid]
                      }]))})

(def remove-entry
  "Remove an entry from the document"
  #db/fn {:lang :clojure
          :params [db id docid name]
          :code (if-let [entryid (q '[:find ?entry .
                                      :in $ ?docid ?name
                                      :where
                                      [?doc :document/id ?docid]
                                      [?doc :document/entries ?entry]
                                      [?entry :entry/name ?name]]
                                    db docid name)]
                  ;; the entry exists, so go ahead and remove it
                  [[:db.fn/retractEntity entryid]
                   [:db/retract id :document/entries entryid]]
                  ;; the entry doesn't exist, do nothing
                  []
                  )})

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
               ;; document schema
               {:db/id (d/tempid :db.part/db)
                :db/ident :document/id
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/unique :db.unique/identity
                :db/index true
                :db/doc "A unique identifier for this document"
                :db.install/_attribute :db.part/db}
               {:db/id (d/tempid :db.part/db)
                :db/ident :document/version
                :db/valueType :db.type/long
                :db/cardinality :db.cardinality/one
                :db/doc "The version of this document after commit"
                :db.install/_attribute :db.part/db}
               {:db/id (d/tempid :db.part/db)
                :db/ident :document/entries
                :db/valueType :db.type/ref
                :db/cardinality :db.cardinality/many
                :db/isComponent true
                :db/doc "Name/value pair entries attached to this document"
                :db.install/_attribute :db.part/db}

               ;; entry (name/value pair) schema
               {:db/id (d/tempid :db.part/db)
                :db/ident :entry/name
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/index true
                :db/doc "Name (key) of this entry"
                :db.install/_attribute :db.part/db}
               {:db/id (d/tempid :db.part/db)
                :db/ident :entry/value
                :db/valueType :db.type/bytes
                :db/cardinality :db.cardinality/one
                :db/doc "(Opaque) value of this entry"
                :db.install/_attribute :db.part/db}

               ;;------------------------------------------------------
               ;; install our transaction-functions
               ;;------------------------------------------------------
               {:db/id (d/tempid :db.part/user)
                :db/ident :inc-version
                :db/fn inc-version}
               {:db/id (d/tempid :db.part/user)
                :db/ident :update-entry
                :db/fn update-entry}
               {:db/id (d/tempid :db.part/user)
                :db/ident :remove-entry
                :db/fn remove-entry}]))

(defn connect [url]
  (let [init (d/create-database url)
        conn (d/connect url)]
    (when init
      (install-schema conn))
    (swap! handle assoc :url url :conn conn)))
