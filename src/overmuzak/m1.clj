(ns overmuzak.m1
  (:require [overtone.live :as O]
            [overtone.samples.freesound :as FS]
            [leipzig.live :as live]
            [leipzig.scale :as scale]
            [leipzig.melody :refer [all bpm is phrase tempo then times where with]])


  )

;(def kick-buf (O/load-sample "/Users/lenz/varie/overtone-covers/resources/lights/kick.wav"))




(def melody
          ; Row,  row,  row   your  boat
  (phrase [3/3   3/3   2/3   1/3   3/3]
          [  0     0     0     1     2]))

(def daft
  (phrase [2 1/2 1/2 1/2 2.5 1/2 1/2 1/2 2.5 1/2 1/2 1/2 2.5 1 1]
          [0 -1 0 2 -3 -4 -3 -1 -5 -6 -5 -3 -7 -6 -5])

  )

(O/definst beep [freq 440 dur 1.0]
                  (-> freq
                      O/saw
                      (* (O/env-gen (O/perc 0.05 dur) :action O/FREE))))

(O/defsynth kick [amp 0.5 decay 0.6 freq 65]
(let [env (O/env-gen (O/perc 0 decay) 1 1 0 1 O/FREE)
      snd (O/sin-osc freq (* Math/PI 0.5))]
  (O/out 0 (O/pan2 (* snd env amp) 0))))



(defmethod live/play-note :default [{midi :pitch seconds :duration}]
  (-> midi O/midi->hz (beep seconds)))


(defmethod live/play-note :beat [note]
  (kick))

(defn tap [drum times length & {:keys [amp] :or {amp 1}}]
  (map #(zipmap [:time :duration :drum :amp]
                [%1 (- length %1) drum amp]) times))


(def da-beats
  (->>
    (reduce with
            [(tap :fat-kick (range 8) 8)
             (tap :kick (range 8) 8)
             (tap :snare (range 1 8 2) 8)
             (tap :close-hat (sort (concat [3.75 7.75] (range 1/2 8 1))) 8)])
    (all :part :beat)))






  (->>
    daft
    (with da-beats)
    (tempo (bpm 110))
    (where :pitch (comp scale/G scale/minor))
    live/play)

