(ns triggerfish.client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
 :objects
 (fn
   [db _]
   (reaction (:objects @db))))

(register-sub
 :positions
 (fn
   [db _]
   (reaction (:positions @db))))

(register-sub
 :connections
 (fn
   [db _]
   (reaction (:connections @db))))