(ns library-asset-tag.ui.view.loading
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [cljs-react-material-ui.core :as ui]))

(defui View
  Object
  (render [this]
          (dom/div
           #js {:id "app-loading"
                :className "row center-xs"}
           (dom/div #js {:className "col-xs-12"
                         :style #js {:height "33%"}}
                    (dom/img
                     #js {:id "logo"
                          :src "logo.png"})
                    (dom/div
                     #js {:style #js {:position "relative"
                                      :display "flex"
                                      :flex-direction "row"
                                      :justify-content "center"}}
                     (dom/h1 #js {:style #js {:width "auto"}}
                             "Checking login status")
                     (ui/refresh-indicator
                      {:size 20
                       :top 0
                       :left 5
                       :status "loading"
                       :style {:position "relative"
                               :flex "0"}}))))))

(def view (om/factory View))
