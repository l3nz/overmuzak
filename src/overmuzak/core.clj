(ns overmuzak.core
  (:require [overtone.core :as O]))

(defn boot! []
  (O/boot-server))

(defn status []
  (println "Server-info:"  (O/server-info))
  (println "Server-status:"  (O/server-status))
  (println "Server-opts:" (O/server-opts)))