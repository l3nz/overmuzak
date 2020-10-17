(ns overmuzak.instruments
  (:require [overtone.core :refer :all :as O]
           ; [overtone.inst.drum :as DRUM]
           ; [overtone.inst.piano :as PIANO]
           ; [overtone.inst.synth :as SYNTH]
            ))

(definst da-funk
  [freq 440 dur 1.0 amp 1.0 cutoff 1700 boost 6 dist-level 0.015]

  (let [env (env-gen (adsr 0.1 0.7 0.5 0.3)
                     (line:kr 1.0 0.0 dur)
                     :action FREE)
        filter-env (+ (* freq 0.15)
                      (env-gen (adsr 0.5 0.3 1 0.5)
                               (line:kr 1.0 0.0 (/ dur 2))
                               :level-scale cutoff))
        osc (mix [(saw freq)
                  (saw (* freq 0.2))])]

    (-> osc
        (bpf filter-env 0.6)
        (* env amp)
        pan2
        (clip2 dist-level)
        (* boost)
        distort)))

;(da-funk (midi->hz (note :D4)))



;; kick
;; https://www.musicradar.com/how-to/how-to-recreate-classic-analogue-drum-sounds-in-your-daw-and-with-hardware


(definst
  kick
  [amp 1 decay 0.6 freq 65]

  (* (sin-osc freq (* Math/PI 0.5))
     (env-gen (perc 0 decay) 1 1 0 1 FREE)
     amp))


; (:sdef kick)

; snare


(definst
  snare
  [amp 1]

  (* (sin-osc 160 (* Math/PI 0.5))
     (env-gen (perc 0 0.6) 1 1 0 1 FREE)
     amp))

(definst
  snare-lenz
  [amp 1]

  ;; somma del rumore rosa a 1/3 dell'ampiezza con
  ;; un kick a 200hz

  (sum [(* (pink-noise 0.1)
           (env-gen (perc 0 0.2) 1 1 0 1 FREE)
           (/ amp 3))

        (* (sin-osc 200 (* Math/PI 0.5))
           (env-gen (perc 0 0.1) 1 1 0 1 FREE)
           amp)]))



; per vedere un synth
; (show-graphviz-synth snaremix)