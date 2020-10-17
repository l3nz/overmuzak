(ns overmuzak.dance
  (:require [overtone.core :as O]
            [overmuzak.instruments :as I]
            [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [leipzig.live :as live]
            [leipzig.temperament :as temperament]
            [leipzig.chord :refer :all]))

;(boot-server)

;(I/da-funk )


(def leipzig-melody
  [{:time 0 :duration 12/11 :pitch 391.99 :part :da-funk}
   {:time 12/11 :duration 3/11 :pitch 349.22 :part :da-funk}
   {:time 15/11 :duration 3/11 :pitch 391.99 :part :da-funk}])

;
; Instruments
;

(defmethod live/play-note :da-funk [{hertz :pitch seconds :duration amp :amp cutoff :cutoff}]
  (when hertz
    (I/da-funk :freq hertz :dur seconds :amp (or amp 1) :cutoff (or cutoff 1600))))

(defmethod live/play-note :drums [{instr :pitch amp :amp}]
  (condp = instr
    0 (I/kick :amp (or amp 1))
    1 (I/snare-lenz :amp (or amp 1))))

(def g-minor (comp scale/G scale/minor))

(def da-funk-phrase
  (->> (phrase (concat [2] (take 12 (cycle [1/2 1/2 1/2 5/2])) [1 1])
               [7 6 7 9 4 3 4 6 2 1 2 4 0 1 2])
       (where :pitch (comp scale/low scale/G scale/minor))
       (all :amp 0.5)))

(def da-funk-track
  (->> da-funk-phrase
       (all :part :da-funk)
       (wherever :pitch, :pitch temperament/equal)
       (tempo (bpm 120))))

; (live/play leipzig-melody)


(def beat
  (->>
   (phrase (cycle [1/2 1/4 1/4 1/2 1/2])
           (cycle [0   0   1   0   1]))
   (take 20)
   (times 1)
   (all :part :drums)))

(defn length-of-part
  "How long does a part last?"
  [part]
  (let [ultimo (last part)]
    (+ (:time ultimo) (:duration ultimo))))


; uno dopo l'altro
;(live/play
;  (->> da-funk-track
;       (then beat)))

; insieme
; (live/play
;  (->> da-funk-track
;     (with beat)))

; stoppa
; (live/stop)


(comment
  (live/play
   (->> []
        (then (->> da-funk-track))
        (then (->> da-funk-track (with beat)))
        (then (->> beat))))
  ;
  )

;