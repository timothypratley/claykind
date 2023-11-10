(ns blog.gravity
  (:require [scicloj.kindly.v4.kind :as kind]))

;; spacetime is a way to describe geometry

;; what is gravity?

;; quantum interference

;; what holds an atom together?

;; quarks

;; nucleus and electrons

;; electrons exist in a fungible field

;; all matter is in a fungible field

;; interference and entanglement keep up with each other


;; not a force between mass (even though this is effectively the calculation)


(kind/hiccup
  '[(fn []
      ;; id, r, [x, y], [dx, dy], t, history
      (let [d90 (/ js/Math.PI 2.0)
            d180 js/Math.PI
            u (r/atom {"sun"   [1 [0 0] [0 0] 0 []]
                       "earth" [0.25 [-20 20] [-0.2 -0.2] 0 []]
                       "mercury" [0.15 [-10 10] [-0.2 -0.3] 0 []]
                       "i" [0.15 [-5 5] [-0.3 -0.4] 0 []]
                       ;"y0"    [0.5 -50 -10 0 0]
                       ;"y1"    [0.5 -30 -10 0 0]
                       ;"y2"    [0.5 -10 -10 0 0]
                       ;"y3"    [0.5 10 -10 0 0]
                       ;"y4"    [0.5 30 -10 0 0]
                       ;"y5"    [0.5 50 -10 0 0]
                       ;"x0"    [0.5 -10 -50 d90 0 0]
                       ;"x1"    [0.5 -10 -30 d90 0 0]
                       ;"x2"    [0.5 -10 -10 d90 0 0]
                       ;"x3"    [0.5 -10 10 d90 0 0]
                       ;"x4"    [0.5 -10 30 d90 0 0]
                       ;"x5"    [0.5 -10 50 d90 0 0]
                       })
            update-vals (fn [m f]
                          (into {} (for [[k v] m]
                                     [k (f v)])))
            path (fn [[[x y] & pts]]
                   (str "M " x "," y " L "
                        (apply str (interpose " "
                                              (for [[x y] pts]
                                                (str x "," y))))))
            wrap (fn [x]
                   (cond (> x 50) (recur (- x 100))
                         (< x -50) (recur (+ x 100))
                         :else x))
            d2 (fn [v]
                 (apply + (map #(js/Math.pow % 2) v)))
            normalize (fn [v]
                        (let [d (js/Math.sqrt (d2 v))]
                          (if (zero? d)
                            (vec v)
                            (mapv #(/ % d) v))))
            scale (fn [factor v]
                    (mapv #(* factor %) v))
            f (fn [[r [x y] [dx dy] t history]]
                (let [history (conj history [x y])
                      d' (d2 [x y])
                      R (if (zero? d') 0 (js/Math.sqrt d'))
                      G 3
                      ;; TODO: this damping effect should be time dilation
                      a (if (zero? d') 0 (/ G d'))
                      c 299792458
                      Msun 1.989e30
                      c2 (js/Math.pow c 2)
                      e 149597870700
                      ;; TODO: why
                      T2 (- 1 (/ (* 2 G Msun) (* 100000 e R c2)))
                      Tdilation (if (zero? R) 0 (js/Math.sqrt T2))
                      [ddx ddy] (scale (min 10 (* a Tdilation)) (normalize [(- x) (- y)]))
                      dx (+ (* dx Tdilation) ddx)
                      dy (+ (* dy Tdilation) ddy)
                      x (wrap (+ x dx))
                      y (wrap (+ y dy))]
                  [r [x y] [dx dy] (+ t Tdilation) history]))
            color "#0000FF"
            tick #(swap! u update-vals f)]
        (fn []
          (js/window.requestAnimationFrame tick)
          (into
            [:svg {:width   "100%"
                   :viewBox [-50 -50 100 100]
                   :style   {:border "solid 1px #eeeeee"
                             :background "#000000"}}
             [:defs
              [:radialGradient {:id "g"}
               [:stop {:offset 0 :stop-color color :stop-opacity 1}]
               [:stop {:offset 0.25 :stop-color color :stop-opacity 0.5}]
               [:stop {:offset 0.5 :stop-color color :stop-opacity 0.25}]
               [:stop {:offset 0.75 :stop-color color :stop-opacity 0.125}]
               [:stop {:offset 1 :stop-color color :stop-opacity 0}]]]]
            (concat
              #_(for [x (range -50 51 10)]
                (let [ys (range -50 51 10)
                      pts (for [y ys]
                            (let [d (d2 [x y])]
                              [(if (zero? d) x (* x (- 1 (/ 80 d))))
                               (if (zero? d) y (* y (- 1 (/ 80 d))))]))]
                  [:path {:stroke       "#eeeeee"
                          :fill         "none"
                          :stroke-width 0.5
                          :d            (path pts)}]))
              #_(for [y (range -50 51 10)]
                (let [xs (range -50 51 10)
                      pts (for [x xs]
                            (let [d (+ (js/Math.pow x 2)
                                       (js/Math.pow y 2))]
                              [(if (zero? d) x (* x (- 1 (/ 80 d))))
                               (if (zero? d) y (* y (- 1 (/ 80 d))))]))]
                  [:path {:stroke       "#eeeeee"
                          :fill         "none"
                          :stroke-width 0.5
                          :d            (path pts)}]))
              #_(for [x (range -50 51 10)
                      y (range -50 51 10)]
                  (let [d (js/Math.sqrt (+ (js/Math.pow x 2)
                                           (js/Math.pow y 2)))]
                    [:circle {:r  0.5
                              :cx (* x (- 1 (/ 8 d)))
                              :cy (* y (- 1 (/ 8 d)))}]))
              (for [[k [r [x y] [dx dy] t history]] @u]
                [:path {:stroke "green"
                        :stroke-width 0.25
                        :fill "none"
                        :d (path (take-last 100 history))}])
              (for [[k [r [x y] [dx dy] t history]] @u]
                [:circle {:fill "url(#g)"
                          :r    (* r 50) :cx x :cy y}])
              (for [[k [r [x y] [dx dy] t history]] @u]
                [:circle {:fill "white"
                          :r r :cx x :cy y}]))))))])

(kind/hiccup [:h2 "hello there, Daniel"])
