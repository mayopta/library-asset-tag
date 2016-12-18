(ns library-asset-tag.ui.main
  (:require [library-asset-tag.ui.auth :as auth]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [om-bootstrap.button :as b]))

(defui View
  Object
  (render [this]
          (dom/div nil
                   (dom/h1 nil "Logged in")
                   (b/button {:bs-style "link"
                              :onClick #(auth/logout)}
                             "Logout"))))

(def view (om/factory View))
