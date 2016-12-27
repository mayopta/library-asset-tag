(ns library-asset-tag.ui.view.login
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
          (dom/div #js {:className "row center-xs"}
                   (dom/div #js {:className "col-xs-12"}
                            (ui/card
                             nil
                             (ui/card-media
                              {}
                              (dom/img #js {:src "books.jpg"}))
                             (ui/card-title
                              {:title "Mayo Elementary School, Holden MA"
                               :subtitle "Please log in"})
                             (ui/card-text
                              {}
                              "Welcome to the Mayo PTA Library Asset Tag generator.  This program requires an active @mayopta.com account")
                             (ui/card-actions
                              {}
                              (ui/raised-button
                               {:label          "Sign In"
                                :primary        true
                                :label-position :before
                                :icon           (ic/action-verified-user)
                                :on-touch-tap   #(auth/login)})))))))

(def view (om/factory View))
