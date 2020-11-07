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

(defmethod live/play-note :drums [{instr :pitch amp :amp}]
  (cond
    (= instr 0)   (I/kick :amp amp)
    (= instr 1)   (I/snare :amp amp)

    :else
    (println "Unknown drum " instr)))

(defn phrase-beats
  "Data una frase, la ripete N volte perchè abbia la lunghezza richiesta"
  [beats time notes]
  (loop [my-time []
         my-notes []
         all-time (cycle time)
         all-notes (cycle notes)]
    (cond
      (> (apply + my-time) beats)
      (phrase my-time my-notes)

      :else
      (recur
       (->> all-time first (conj my-time))
       (->> all-notes first (conj my-notes))
       (rest all-time)
       (rest all-notes)))))

(defn phrase-rakt
  "Radioaktivity riff.

  It's 12 beats, but the first measure is just
  the syncope. Chords change on 2nd measure.
  "
  [offset]
  (->> (phrase [3   1/2 1/2 1 1/2 1/2 1/2 1 1/2 1 1/2 1 1 1/2]
               [nil nil 0 3 3 0 3 0 3 5 4 3 nil nil])
       (where :pitch (fn [p] (+ p offset)))
       (where :pitch (comp scale/C scale/major))
       (all :amp 0.5)
       (all :part :piano)))

(defn pad
  "Il pad, tipo coro umano"
  [scale-mode n-beats]
  (->> (phrase-beats
        n-beats
        [1 1 1 1]
        [0 0 2 0])
       (where :pitch scale-mode)
       (all :amp 0.1)
       (all :part :violin)))

(defn click
  "Base drums"

  [n-beats]
  (->> (phrase-beats
        n-beats
        [1 1 1 1/2 1/4 1/4]
        [0 1 0 nil 1 1])
       (all :amp 0.4)
       (all :part :drums)))

(defn silence [beats]
  (->>
   (phrase [beats] [99])
   (all :amp 0.4)
   (all :part :drums)))

(def A-min (comp scale/A scale/minor scale/low))
(def F-maj (comp scale/F scale/major scale/low))
(def G-maj (comp scale/G scale/major scale/low))

(def rakt-track
  (->>
   (->> []
        (then (click 8))

        (then
         (->> []

              ; main part
              (with (->> []
                         (then (silence 4))
                         (then (phrase-rakt 2))
                         (then (phrase-rakt 0))
                         (then (phrase-rakt 1))))

              ; chords
              (with (->> []
                         (then (pad A-min 4))
                         (then (pad F-maj 4)) (then (pad A-min 8))
                         (then (pad F-maj 4)) (then (pad F-maj 8))
                         (then (pad G-maj 4)) (then (pad G-maj 8))))

              ; click track
              (with (->> (click (+ 4 12 12 12)))))))

   (tempo (bpm 80))))

(comment
  (live/play rakt-track)
  ;
  (live/stop)
  ;
  )

(defn howlong [phrase]
  (reduce + 0 (map :duration phrase)))