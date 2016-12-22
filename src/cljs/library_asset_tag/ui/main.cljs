(ns library-asset-tag.ui.main
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.auth :as auth]
            [library-asset-tag.ui.create :as create]
            [library-asset-tag.ui.activity :as activity]
            [library-asset-tag.ui.settings :as settings]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.icons :as ic]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Snackbar
  static om/IQuery
  (query [this]
         [:user])
  Object
  (render [this]
          (let [{:keys [user]} (om/props this)]
            (ui/snackbar {:open true
                          :auto-hide-duration 4000
                          :message (str "Logged in as " (:email user))}))))

(def snackbar-view (om/factory Snackbar))

(defui View
  static om/IQuery
  (query [this]
         [:login])
  Object
  (render [this]
          (let [{:keys [login session]} (om/props this)]
            (dom/div nil
                     (ui/app-bar
                      {:title "Mayo Library Assets"
                       :show-menu-icon-button false
                       :icon-element-right
                       (ui/flat-button
                        {:icon (ic/action-power-settings-new)
                         :label     "Logout"
                         :label-position "before"
                         :secondary true
                         :on-touch-tap   #(auth/logout)})})
                     (ui/tabs
                      nil
                      (ui/tab
                       {:label "Create"
                        :icon (ic/content-add-circle-outline)}
                       (create/view session))
                      (ui/tab
                       {:label "Activity"
                        :icon (ic/action-restore)}
                       (activity/view))
                      (ui/tab
                       {:label "Settings"
                        :icon (ic/action-settings)}
                       (settings/view)))
                     (snackbar-view login)))))

(def view (om/factory View))
