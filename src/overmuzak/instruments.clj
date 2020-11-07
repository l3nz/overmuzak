(ns overmuzak.instruments
  (:require [overtone.core :refer :all :as O]
    ;[overtone.gui.scope :as SCOPE] ????
           ; [overtone.inst.drum :as DRUM]
           ; [overtone.inst.piano :as PIANO]
           ; [overtone.inst.synth :as SYNTH]
            ))

;
; Lots of cool instruments here
; https://github.com/ctford/whelmed/blob/f937e74f150ed594c2d862834cf5ed41deb80f5a/src/whelmed/instrument.clj#L25


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

(definst
  testx []
  (rlpf:ar
   (dust:ar [12,15])
   (+ 1500 (* 200 (sin-osc 1)))

   0.01))

(definst
  test2 [freq 440]
  (comb-n
   (mix [(sin-osc
          (midicps
           (+ 40 (* 20
                    (+ 0.5 (lf-saw 0.05))))))

         ;(sin-osc freq)
         ])

   1 0.3 2))

(comment
  (def t (test2))
  (ctl t :freq 880)
  (kill t))

; per vedere un synth
; (show-graphviz-synth snaremix)


; =========================
;https://sccode.org/1-5do
; synths herE: https://sccode.org/1-5aD

(definst
  violin
  [midinote 60 dur 1.0 amp 0.8]
  ; var env = EnvGen.kr(Env.asr(0.1, 1, 0.1), gate, doneAction:2);
  ; var sig = VarSaw.ar(
  ;		midinote.midicps,
  ;		width:LFNoise2.kr(1).range(0.2, 0.8)*SinOsc.kr(5, Rand(0.0, 1.0)).range(0.7,0.8))*0.25;

  (let [env (env-gen (asr 0.4 1 0.3)
                     (line:kr 1 0 dur)
                     :action FREE)
        vibr (* (range-lin (sin-osc:kr 5 (i-rand 0 1)) 0.7 0.9)
                (range-lin (lf-noise2:kr 1) 0.2 0.8)
                0.40)
        sig (var-saw (midicps midinote)
                     :width vibr)]

    (* sig env  amp);
    ))

(definst
  chorus
  [midinote 60 pw 0.5 dur 1.0 amp 1.0]
  ; The example you provided isn't even a choir.
  ; That's pulse wave filtered, with heavy vibrato,
  ; a bit of chorus and too much reverb
  (let [freq (midicps midinote)
        mouse-y (mouse-y:kr 0 1)

        env (env-gen (asr 0.1 5 0.8)
                     (line:kr 1 0 dur)
                     :action FREE)

        vibr    (sin-osc:kr 3)]

    (->
        ; start with pulse
     (pulse :freq freq :width pw)

        ; vibrato
     (* (range-lin vibr 0.6 1))
        ;
        ;(+ (* 2 (sin-osc :freq (* freq 0.5))))
        ; filter
     (moog-ff   :freq (* freq 1.4) :gain 1)
        ; add reverb
     (free-verb :mix 0.7 :room 0.2)
     (* env  amp))))

(definst sing [freq 440 dur 1.0 volume 1.0 pan 0 wet 0.5 room 0.5]
  (-> (saw freq)
      (+ (saw (* freq 1.01)))
      (rlpf (mul-add (sin-osc 8) 200 1500) 1/8)
      (lpf 5000)
      (* 1/4 (env-gen (asr 0.03 0.3 0.1) (line:kr 1 0 dur)))))

(definst kraft-bass [freq 440 dur 1.0 vol 1.0 pan 0 wet 0.5 room 0.5]
  (let [envelope (env-gen (asr 0 1 1) (line:kr 1.0 0.0 dur))
        level (+ 100 (env-gen (perc 0 3) :level-scale 6000))
        osc (mix [(saw freq)
                  (saw (* freq 1.005))
                  (pulse (/ freq 2) 0.5)])]
    (-> osc
        (lpf level)
        (* envelope))))
