(ns scicloj.clay.html
  (:require [clojure.string :as str]
            [huff.core :as huff]
            [hiccup.page :as hp]
            [clojure.string :as string]
            [clojure.java.browse :as browse]
            [scicloj.kindly.v3.api :as kind]
            [scicloj.read-kinds.api :as api]))

(defn scittle-script [& cljs-forms]
  [:script {:type "application/x-scittle"}
   (->> cljs-forms
        (map pr-str)
        (string/join "\n"))])

(defn div-and-script [id widget]
  [[:div {:id id}]
   (scittle-script
     (list 'dom/render (list 'fn [] widget)
           (list '.getElementById 'js/document id)))])

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

(defn page [widgets]
  (hiccup.page/html5
    [:head]
    (into
      [:body
       (hiccup.page/include-js "https://unpkg.com/react@18/umd/react.production.min.js"
                               "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
                               "https://scicloj.github.io/scittle/js/scittle.js"
                               "https://scicloj.github.io/scittle/js/scittle.reagent.js")
       (scittle-script '(ns main
                          (:require [reagent.core :as r]
                                    [reagent.dom :as dom])))]
      (->> widgets
           (map-indexed (fn [i widget]
                          (div-and-script (str "widget" i)
                                          widget)))
           (apply concat)))))

(defn img [url]
  [:img {:height 50 :width 50
         :src    url}])

(def example-values
  [(img "https://clojure.org/images/clojure-logo-120b.png")
   (img "https://raw.githubusercontent.com/djblue/portal/fbc54632adc06c6e94a3d059c858419f0063d1cf/resources/splash.svg")])

;; run
(->> example-values
     page
     (spit "example.html"))

(browse/browse-url "example.html")


;;;;
;; we need to be able to recur (call advice)
;; extensible

(defmulti prepare :kind)

(defmethod prepare :default [{:keys [value kind]}]
  [:div
   (when kind
     [:div "Unimplemented: " [:code (pr-str kind)]])
   [:pre [:code (pr-str value)]]])

(defn prepare-value [v]
  (prepare (kind/advice {:value v})))

(defmethod prepare :kind/vector [{:keys [value]}]
  [:div (mapv prepare-value value)])

(defmethod prepare :kind/map [{:keys [value]}]
  [:div (-> value
            (update-keys prepare-value)
            (update-vals prepare-value))])

(defmethod prepare :kind/image [{:keys [value]}]
  [:img {:src value}])

(defmethod prepare :kind/comment [{:kindly/keys [comment]}]
  [:p comment])

(defmethod prepare :kind/var [{:keys [value]}]
  [:div "VAR" (str value)])

(defmethod prepare :kind/table [{:keys []}]
  [:div "TABLE"])

(defmethod prepare :kind/seq [{:keys [value]}]
  (map prepare-value value))

(defn render-html
  "Transforms advice into a Markdown string"
  [advice]
  (let [{:keys [code]} advice]
    (hiccup2.core/html
      [:div
       ;; code
       [:pre [:code (pr-str code)]]
       ;; value
       (prepare advice)])))
