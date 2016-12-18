(ns library-asset-tag.ui.login
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
          (println "login-view")
          (dom/div
           nil
           (ui/paper
            nil
            (dom/h3 nil "Please log in")
            (ui/raised-button
             {:label          "Sign In"
              :primary        true
              :label-position :before
              :icon           (ic/action-verified-user)
              :on-touch-tap   #(auth/login)})))))

(def view (om/factory View))
