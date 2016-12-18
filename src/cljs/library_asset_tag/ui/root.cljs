(ns library-asset-tag.ui.root
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.loading :as loading]
            [library-asset-tag.ui.login :as login]
            [library-asset-tag.ui.main :as main]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.icons :as ic]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui RootView
  static om/IQuery
  (query [this]
         [:login])
  Object
  (render [this]
          (let [{:keys [login]} (om/props this)
                status (:status login)]
            (ui/mui-theme-provider
             {:mui-theme
              (ui/get-mui-theme {:palette
                                 {:primary1-color (ui/color :indigo-900)}
                                 :raised-button
                                 {:primary-text-color (ui/color :light-black)
                                  :font-weight 200}})}
             (dom/div nil
              (case status
                :loading (loading/view)
                :logged-out (login/view)
                :logged-in (main/view)))))))

(defn init []
  (om/add-root! core/reconciler
                RootView (gdom/getElement "app")))
