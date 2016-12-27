(ns library-asset-tag.ui.view.loading
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [cljs-react-material-ui.core :as ui]))

(defui View
  Object
  (render [this]
          (dom/div
           #js {:id "app-loading"}
           (dom/div
            #js {:className "row center-xs"}
            (dom/div #js {:className "col-xs-12"}
                     (dom/img
                      #js {:id "logo"
                           :src "logo.png"})))
           (dom/div
            #js {:className "row center-xs"}
            (dom/div #js {:className "col-xs-8"}
                     (dom/h1 nil
                             "Checking login status"))
            (dom/div #js {:className "col-xs-4"
                          :id "login-indicator"}
                     (ui/refresh-indicator
                      {:size 30
                       :top 0
                       :left 0
                       :status "loading"
                       :style {:position "relative"}}))))))

(def view (om/factory View))
