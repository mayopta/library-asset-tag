(ns library-asset-tag.ui.main
  (:require [library-asset-tag.ui.auth :as auth]
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
                    {:title "Library Assets"
                     :icon-element-right
                     (ui/flat-button
                      {:label     "Logout"
                       :secondary true
                       :on-touch-tap   #(auth/logout)})}))))

(def view (om/factory View))
