(ns library-asset-tag.inventory
  (:require [library-asset-tag.db :as database]
            [clojure.string :as string]
            [datomic.api :refer [tempid] :as datomic]))

(def text-content {"Content-Type" "text/plain"})

(defn- get-summary []
  (str "Summary:"))

(defn- filter-range [start end coll]
  (cond
    (and (some? start) (some? end))
    (let [count (- end start)]
      (->> coll (drop start) (take count)))

    (some? start)
    (drop start coll)

    :else
    coll))

(defn- parse-or-nil [val]
  (if val
    (Integer/parseInt val)
    nil))

;;---------------------------------------------------------------------------
;; get-range(start, end) - Returns a range of assetids with optional paging
;;                         (via 'start' and 'end' paramters).  The values
;;                         are emitted as newline delimited plaintext values
;;---------------------------------------------------------------------------
(defn- get-range [start end]
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
  (if (= summary "true")
    (get-summary)
    (get-range (parse-or-nil start) (parse-or-nil end))))

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
