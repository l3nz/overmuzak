(ns overmuzak.radioaktivitaet
  (:require [overtone.core :as O]
            [overtone.music.pitch :as pitch]
            [overmuzak.instruments :as I]
            [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [leipzig.live :as live]
            [leipzig.temperament :as temperament]
            [leipzig.chord :refer :all]
            [overtone.inst.piano :as PIANO]))

(defmethod live/play-note :riff1 [{midi :pitch seconds :duration amp :amp cutoff :cutoff}]
  (when midi
    (I/da-funk :freq (temperament/equal midi)
               :dur seconds
               :amp (or amp 1)
               :cutoff (or cutoff 1600))))

(defmethod live/play-note :piano [{midi :pitch seconds :duration amp :amp}]
  (when midi
    (println "Playing note" (pitch/find-note-name midi)
             " for " seconds
             " (MIDI: " midi ")")
    (PIANO/piano :note midi
                 :random 0
                 ;:vel (* 100.0 amp)
                 :decay 0.1
                 :release 0.1
                 :sustain 0)))

(defn phrase-rakt
  "Radioaktivity riff."
  [sc]
  (->> (phrase [1/4 1/2 1/4 1/4 1/4 1/2 1/4 1/2 1/4 1/2]
               [0 3 3 0 3 0 3 5 4 3])
       (where :pitch (comp sc scale/major))
       (all :amp 0.5)))

; trasforma il pitch da nota a hz
(def rakt-track
  (->>
   (->> []
        (then (phrase-rakt scale/A))
        (then (phrase-rakt scale/F))
        (then (phrase-rakt scale/G)))

   (all :part :piano)
   (tempo (bpm 40))))

(comment
  (live/play rakt-track)
  ;
  (live/stop)
  ;
  )
