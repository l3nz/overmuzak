(ns overmuzak.scale-practice
  (:require [overmuzak.instruments :as I]
            [overtone.music.pitch :as pitch]
            [leipzig.melody :as m]
            [leipzig.scale :as scale]
            [leipzig.live :as live]
            [overtone.inst.piano :as PIANO]))

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

; see https://www.stefanomicarelli.it/scala-minore-melodica/
(def scale-min-melodic-asc
  (scale/scale [2 1 2 2 2 2 1]))

(def scale-min-melodic-desc
  (scale/scale [2 1 2 2 1 2 2]))

(def scale-tempo
  [1/2 1/2 1/2 1/2 1/2 1/2 1/2])

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
  ([tonica n_octaves fn-scale-asc fn-scale-desc]
   (let [n_notes (* n_octaves 7)
         scale-asc (comp scale/low tonica fn-scale-asc)
         scale-desc (comp scale/low tonica fn-scale-desc)

         grades (concat
                 (map scale-asc (range 0 n_notes))
                 (map scale-desc (range n_notes -1 -1)))]

     (->> (m/phrase (take (count grades)
                          (cycle scale-tempo))

                    grades))))

  ([tonica n_octaves fn-scale]
   (as-scale tonica n_octaves fn-scale fn-scale)))

(defn as-track [part]
  (->> part
       (m/all :part :piano)
       (m/tempo (m/bpm 25))))

(comment
  (live/play
   (->> []
        (m/then (->> my-track))))
  ;
  )

(comment
  (defn go! []
    (live/play
     (->> []
          (m/then (->> my-track))))))

(defn minor! [tonica]
  (live/play
   (as-track
    (as-scale tonica 2
              scale-min-melodic-asc
              scale-min-melodic-desc))))

(defn major! [tonica]
  (live/play
   (as-track
    (as-scale tonica 2 scale/major))))

(defn blues! [tonica]
  (live/play
   (as-track
    (as-scale tonica 1 scale/blues))))

(defn ! []
  (live/stop))
