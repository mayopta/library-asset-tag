(ns library-asset-tag.ui.home
  (:require [library-asset-tag.ui.client :as client]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.icons :as ic]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui View
  Object
  (render [this]
          (ui/paper nil
                    (dom/div #js {:className "row center-xs"}
                             (dom/div #js {:className "col-xs-12"}
                                      (ui/floating-action-button
                                       {:mini true
                                        :secondary true
                                        :on-touch-tap #(client/alloc!)}
                                       (ic/content-add))))
                    (dom/div #js {:className "row"}
                            (dom/div #js {:className "col-xs-12"}
                                     (ui/table
                                      {:selectable false
                                       :multi-selectable false}
                                      (ui/table-header
                                       {:display-select-all false
                                        :adjust-for-checkbox false}
                                       (ui/table-row
                                        nil
                                        (ui/table-header-column nil "ID")
                                        (ui/table-header-column nil "Date")))
                                      (ui/table-body nil)))))))

(def view (om/factory View))
