(ns library-asset-tag.ui.root
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.view.loading :as loading]
            [library-asset-tag.ui.view.login :as login]
            [library-asset-tag.ui.view.main :as main]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.icons :as ic]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui RootView
  static om/IQuery
  (query [this]
         [:login :session])
  Object
  (render [this]
          (let [{:keys [login session]} (om/props this)
                status (:status login)]
            (ui/mui-theme-provider
             {:mui-theme
              (ui/get-mui-theme {:palette
                                 {:primary1-color (ui/color :indigo-900)
                                  :primary2-color (ui/color :indigo-700)
                                  :primary3-color (ui/color :indigo-400)
                                  :accent1-color (ui/color :lime-500)
                                  :accent2-color (ui/color :lime-900)
                                  :accent3-color (ui/color :lime-900)}})}
             (dom/div
              nil
              (case status
                :loading (loading/view)
                :logged-out (login/view)
                :logged-in (main/view (om/props this))))))))

(defn init []
  (om/add-root! core/reconciler
                RootView (gdom/getElement "app")))
