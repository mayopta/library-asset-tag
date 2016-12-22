(ns library-asset-tag.ui.create
  (:require [library-asset-tag.ui.client :as client]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.icons :as ic]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui View
  static om/IQuery
  (query [this]
         [:assets])
  Object
  (render [this]
          (let [{:keys [assets]} (om/props this)]
            (ui/paper nil
                      (dom/div #js {:className "row center-xs"}
                               (dom/div #js {:className "col-xs-2"
                                             :id "action-buttons"}
                                        (ui/floating-action-button
                                         {:mini true
                                          :secondary true
                                          :on-touch-tap #(client/alloc!)}
                                         (ic/social-plus-one))
                                        )
                               (dom/div #js {:className "col-xs-8"
                                            :id "current-id"}
                                        (dom/h1 nil (first assets))))
                      (dom/div #js {:className "row center-xs"}
                               (dom/div #js {:className "col-xs-12"}
                                        (dom/h3 #js {:style
                                                     #js {:margin-bottom "10px"
                                                          :margin-top "10px"}}
                                                "Session History")
                                        (ui/divider nil)))
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
                                         (ui/table-body
                                          {:display-row-checkbox false}
                                          (map (fn [asset]
                                                 (ui/table-row
                                                  nil
                                                  (ui/table-row-column nil asset)
                                                  (ui/table-row-column nil "Today")))
                                               assets)))))))))

(def view (om/factory View))
