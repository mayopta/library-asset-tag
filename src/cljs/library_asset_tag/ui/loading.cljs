(ns library-asset-tag.ui.loading
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui View
  Object
  (render [this]
          (dom/h1 nil "Loading")))

(def view (om/factory View))
