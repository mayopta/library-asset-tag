(ns library-asset-tag.ui.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(def init-data
  {:login {:status :loading}})

(defmulti read om/dispatch)

(defmethod read :login
  [{:keys [state]} k _]
  (let [st @state]
    {:value (get st k)}))

(defmulti mutate om/dispatch)

(defmethod mutate 'login
  [{:keys [state]} k {:keys [status]}]
  {:action
   (fn []
     (swap! state assoc-in [:login :status] status))})

(def reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})}))

(defn alloc! []
  (println "alloc!"))
