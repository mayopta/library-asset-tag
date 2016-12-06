(ns library-asset-tag.db
  (:refer-clojure :exclude [print update get ])
  (:require  [datomic.api :refer [q db] :as d]
             [clojure.pprint :refer :all]))

(def handle (atom {}))

(defn- install-schema [conn]
  )

(defn connect [url]
  (let [init (d/create-database url)
        conn (d/connect url)]
    (when init
      (install-schema conn))
    (swap! handle assoc :url url :conn conn)))
