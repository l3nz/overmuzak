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

(defmethod live/play-note :violin [{midi :pitch seconds :duration amp :amp}]
  (when midi
    (println "Playing note" (pitch/find-note-name midi)
             " for " seconds
             " (MIDI: " midi ")")
    (I/violin :midinote midi
              :dur seconds
              :amp amp)))

(defn phrase-rakt
  "Radioaktivity riff."
  [offset]
  (->> (phrase [1/2 1 1/2 1/2 1/2 1 1/2 1 1/2 1 2]
               [0 3 3 0 3 0 3 5 4 3 nil])
       (where :pitch (fn [p] (+ p offset)))
       (where :pitch (comp scale/C scale/major))
       (all :amp 0.5)
       (all :part :piano)))

(defn pad
  "Il pad, tipo coro umano"

  [scale mode]
  (->> (phrase [4]
               [0])
       (where :pitch (comp scale mode))
       (all :amp 0.5)
       (all :part :violin)))


; trasforma il pitch da nota a hz


(def rakt-track
  (->>
   (->> []
        (then (->> (phrase-rakt 2)
                   (with (pad scale/A scale/major)))); A
        (then (->> (phrase-rakt 0)
                   (with (pad scale/F scale/major)))) ; F
        (then (phrase-rakt 1))) ;G

   (tempo (bpm 80))))

(comment
  (live/play rakt-track)
  ;
  (live/stop)
  ;
  )
