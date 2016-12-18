(ns library-asset-tag.ui.client
  (:require [happy.core :as h :refer [GET PUT DELETE]]
            [happy.client.xmlhttprequest :as hc]
            [promesa.core :as p :include-macros true]))

(h/set-default-client! (hc/create))

(defn login! [idtoken]
  (p/do*
   (PUT "/api/v1/login" idtoken)
   true))
