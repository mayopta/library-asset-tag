(ns library-asset-tag.ui.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(def init-data
  {:login {:status :loading}
   :session {:assets '()}})

(defmulti read om/dispatch)

(defmethod read :login
  [{:keys [state]} k _]
  (let [st @state]
    {:value (get st k)}))

(defmethod read :session
  [{:keys [state]} k _]
  (let [st @state]
    {:value (get st k)}))

(defmulti mutate om/dispatch)

(defmethod mutate 'auth/login
  [{:keys [state]} k {:keys [status]}]
  {:action
   (fn []
     (swap! state assoc-in [:login :status] status))})

(defmethod mutate 'session/add-asset
  [{:keys [state]} k {:keys [id]}]
  {:action
   (fn []
     (swap! state update-in [:session :assets] conj id))})

(def reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})}))
