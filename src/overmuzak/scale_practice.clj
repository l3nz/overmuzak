(ns overmuzak.scale-practice
  (:require [overmuzak.instruments :as I]
            [overtone.music.pitch :as pitch]
            [leipzig.melody :as m]
            [leipzig.scale :as scale]
            [leipzig.live :as live]
            [overtone.inst.piano :as PIANO]))

(defmethod live/play-note :piano [{midi :pitch seconds :duration amp :amp}]
  (when midi
    (prn "Playing note" (pitch/find-note-name midi) " for " seconds)
    (PIANO/piano :note midi
                 :random 0
                 ;:vel (* 100.0 amp)
                 :decay 0.1
                 :release 0.1
                 :sustain 0)))

; see https://www.stefanomicarelli.it/scala-minore-melodica/
(def scale-min-melodic-asc
  (scale/scale [2 1 2 2 2 2 1]))

(def scale-min-melodic-desc
  (scale/scale [2 1 2 2 1 2 2]))

(def scale-tempo
  [[1] 1/2 1/2 1/2 1/2 1/2 1/2])

(defn as-scale
  "Builds a scale using a different function for
  ascending and descending (so we can have a minor
  melodic scale).

  If that's not needed, use the arity-3 version
  that will use the same ascending and descending.

  Scale starts from :C3 as this is the lowest note
  on a viola.

  Timing pattern for each note is defined
  in `scale-tempo` above.

  "
  ([tonalita n_octaves fn-scale-asc fn-scale-desc]
   (let [n_notes (* n_octaves 7)
         scale-asc (comp scale/low tonalita fn-scale-asc)
         scale-desc (comp scale/low tonalita fn-scale-desc)

         grades (concat
                 (map scale-asc (range 0 n_notes))
                 (map scale-desc (range n_notes -1 -1)))]

     (->> (m/phrase (take (count grades)
                          (cycle scale-tempo))

                    grades))))

  ([tonalita n_octaves fn-scale]
   (as-scale tonalita n_octaves fn-scale fn-scale)))

(defn major-scale [tonalita octaves]
  (as-scale tonalita octaves scale/major))

(defn minor-scale [tonalita octaves]
  (as-scale tonalita octaves scale-min-melodic-asc scale-min-melodic-desc))

(defn as-track [part]
  (->> part
       (m/all :part :piano)
       (m/tempo (m/bpm 90))))

(comment
  (live/play
   (->> []
        (m/then (->> my-track))))
  ;
  )

(defn go! []
  (live/play
   (->> []
        (m/then (->> my-track)))))

(defn minor! [scale]
  (live/play
   (as-track
    (minor-scale scale 1))))

(defn major! [scale]
  (live/play
   (as-track
    (major-scale scale 1))))

