(ns library-asset-tag.main
  (:require [library-asset-tag.core :as core]
            [library-asset-tag.handler :as handler]
            [library-asset-tag.db :as db]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [compojure.handler :refer [site]]
            [org.httpkit.server :refer [run-server]]
            [beckon :as beckon]
            [clojure.core.async :refer [<!! >!!] :as async])
  (:gen-class))

(def options
  [["-h" "--help"]
   [nil "--port PORT" "The port to host our service endpoint"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 65536) "Must be a number between 0 and 65536"]]
   [nil "--db-url URL" "The connection string for our database"
    :default "datomic:mem:/library-asset-tag"]])

(defn exit [status msg & rest]
  (do
    (apply println msg rest)
    (System/exit status)))

(defn prep-usage [msg] (->> msg flatten (string/join \newline)))

(defn usage [options-summary]
  (prep-usage ["Usage: library-asset-tagd [options] action"
               ""
               "Options:"
               options-summary]))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args options)]
    (cond

      (:help options)
      (exit 0 (usage summary))

      (not= errors nil)
      (exit -1 "Error: " (string/join errors))

      :else
      (let [{:keys [port db-url]} options]
        (println "starting with options:" options)
        (db/connect db-url)
        (core/init)

        (let [stopfn (run-server handler/app {:port port})
              stopsig (async/chan)]

          (doseq [signame ["INT" "TERM"]]
            (reset! (beckon/signal-atom signame) [#(>!! stopsig signame)]))

          (let [sig (<!! stopsig)]
            (println "Received" sig "signal")
            (stopfn)))))))
