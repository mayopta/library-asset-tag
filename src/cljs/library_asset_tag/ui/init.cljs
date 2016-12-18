(ns library-asset-tag.ui.init
  (:require [library-asset-tag.ui.root :as root]
            [library-asset-tag.ui.auth :as auth]))

(enable-console-print!)

(auth/init)
(root/init)
