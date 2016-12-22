(ns library-asset-tag.inventory
  (:require [library-asset-tag.db :as database]
            [clojure.string :as string]
            [datomic.api :refer [tempid] :as datomic]
            [cheshire.core :as json]
            [slingshot.slingshot :as slingshot]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.local :as l]))

(def text-content {"Content-Type" "text/plain"})
(def json-content {"Content-Type" "application/json"})

(defn- filter-range [start end coll]
  (cond
    (and (some? start) (some? end))
    (let [count (- end start)]
      (->> coll (drop start) (take count)))

    (some? start)
    (drop start coll)

    :else
    coll))

(defn- parse-int [val]
  (if val
    (slingshot/try+
     (Integer/parseInt val)
     (catch Object _
       (slingshot/throw+ {:type ::param-validation})))
    nil))

;;---------------------------------------------------------------------------
;; get-summary - get a summary of our available inventory
;;---------------------------------------------------------------------------
(defn- get-summary []
  (let [db (-> (database/get-connection) datomic/db)
        count (-> (datomic/q '[:find (count ?eid)
                               :in $
                               :where [?eid :inventory/assetid]]
                             db)
                  flatten
                  first)]
    {:status 200
     :header json-content
     :body (json/generate-string {:count count})}))

;;---------------------------------------------------------------------------
;; get-range(start, end) - Returns a range of assetids with optional paging
;;                         (via 'start' and 'end' paramters).  The values
;;                         are emitted as newline delimited plaintext values
;;---------------------------------------------------------------------------
(defn- get-range [start end]
  ;; Our range is only valid if it consists of positive integers, end cannot
  ;; be specified without start, and end is larger than start
  (if-not (or (and (some? start) (neg? start))
              (and (some? end) (nil? start))
              (and (some? start) (some? end) (>= start end )))

    ;; FIXME: We will need to support large data sets eventually,
    ;; but for now, we just return the entire set in memory
    (let [db (-> (database/get-connection) datomic/db)
          data (->> (datomic/q '[:find [?i ...]
                                 :in $
                                 :where [_ :inventory/assetid ?i]]
                               db)
                    (filter-range start end)
                    (string/join \newline))]
      {:status 200
       :headers text-content
       :body data})

    ;; else
    {:status 400
     :headers text-content
     :body (str "Bad start/end parameters")}))

(defn get [{:keys [summary start end] :as params}]
  (slingshot/try+
   (if (= summary "true")
     (get-summary)
     (get-range (parse-int start) (parse-int end)))
   (catch [:type ::param-validation]
       {:status 400})))

;;---------------------------------------------------------------------------
;; allocate - Allocates a new asset, returning the new location URI
;;---------------------------------------------------------------------------
(defn allocate [uri]
  (let [conn (database/get-connection)
        invid (tempid :db.part/user)
        timestamp (-> (l/local-now) c/to-long)
        {:keys [db-after tempids]} (->> [[:alloc-assetid invid {} timestamp]]
                                        (datomic/transact conn)
                                        deref)
        assetid (->> invid
                     (datomic/resolve-tempid db-after tempids)
                     (datomic/entity db-after)
                     :inventory/assetid)]

    {:status 201
     :headers (merge {"Location" (str uri "/" assetid)} json-content)
     :body (json/generate-string {:assetid assetid
                                  :timestamp timestamp})}))

;;---------------------------------------------------------------------------
;; get-by-id - Retrieves a specific asset from the database
;;---------------------------------------------------------------------------
(defn get-by-id [id]
  (if-let [entity (-> (database/get-connection)
                      datomic/db
                      (datomic/pull '[*] [:inventory/assetid id]))]
    ;; match
    {:status 200
     :headers json-content
     :body (json/generate-string entity)}

    ;; else
    {:status 404}))
