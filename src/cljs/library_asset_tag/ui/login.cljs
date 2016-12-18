(ns library-asset-tag.ui.login
  (:require [library-asset-tag.ui.auth :as auth]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [om-bootstrap.panel :as p]
            [om-bootstrap.input :as i]
            [om-bootstrap.button :as b]))

(defui View
  Object
  (render [this]
          (dom/div nil
                   (p/panel {:header (dom/h3 nil "Please log in")}
                            (i/input {:type "text" :addon-after "@mayopta.com"})
                            (b/button {:bs-style "primary"
                                       :bs-size "large"
                                       :block? true
                                       :onClick #(auth/login)}
                                      "Sign In")))))

(def view (om/factory View))
