(ns library-asset-tag.ui.main
  (:require [library-asset-tag.ui.core :as core]
            [library-asset-tag.ui.auth :as auth]
            [library-asset-tag.ui.home :as home]
            [library-asset-tag.ui.activity :as activity]
            [library-asset-tag.ui.settings :as settings]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.icons :as ic]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui View
  Object
  (render [this]
          (dom/div nil
                   (ui/app-bar
                    {:title "Mayo Library Assets"
                     :show-menu-icon-button false
                     :icon-element-right
                     (ui/flat-button
                      {:label     "Logout"
                       :secondary true
                       :on-touch-tap   #(auth/logout)})})
                   (ui/tabs
                    nil
                    (ui/tab
                     {:label "Home"
                      :icon (ic/action-home)}
                     (-> this om/props home/view))
                    (ui/tab
                     {:label "Activity"
                      :icon (ic/action-restore)}
                     (activity/view))
                    (ui/tab
                     {:label "Settings"
                      :icon (ic/action-settings)}
                     (settings/view))))))

(def view (om/factory View))
