(ns library-asset-tag.ui.root
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.loading :as loading]
            [library-asset-tag.ui.login :as login]
            [library-asset-tag.ui.main :as main]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [om-bootstrap.grid :as g]
            [cljs.pprint :as pprint]))

(defui RootView
  static om/IQuery
  (query [this]
         [:login])
  Object
  (render [this]
          (let [{:keys [login]} (om/props this)
                status (:status login)]
            (dom/div
             {:class "container"}
             (g/grid nil
                     (case status
                       :loading (loading/view)
                       :logged-out (login/view)
                       :logged-in (main/view)))))))

(defn init []
  (om/add-root! core/reconciler
                RootView (gdom/getElement "app")))
