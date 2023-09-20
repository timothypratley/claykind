#!/usr/bin/env bb

;; # Analysing git logs in babashka


;; In this notebook, we will analyze our git log.
;; We will check how many commits we have had on each day
;; and will plot the results as a time series.

;; ![babashka](https://avatars.githubusercontent.com/u/64927540?s=200&v=4){height=128}
;; ![claykind](https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg){height=128}
;; ![portal](https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg){height=128}
;; ![quarto](https://avatars.githubusercontent.com/u/67437475?s=200&v=4){height=128}


;; ## Setup

;; This is a Babashka Kindly notebook
;;
;; that gets rendered in Claykind
;;
;; as a Quarto document
;;
;; with embedded Portal viewers.

(require '[scicloj.kindly.v4.kind :as kind])

(defn pr-str-with-meta [value]
(binding [*print-meta* true]
  (pr-str value)))

(defn portal-widget [value]
(kind/hiccup
 [:div
  [:script (str "portal.extensions.vs_code_notebook.activate().renderOutputItem(
                {\"mime\": \"x-application/edn\",
                 \"text\": (() => " (pr-str (pr-str-with-meta value)) ")}
                , document.currentScript.parentElement);")]]))

;; ## Data preparation

(def git-log
(-> (shell/sh "git" "log" "--date=format:%Y-%m-%d")
    :out
    (str/split #"\n")
    kind/pprint))

(def dates-and-freqs
(->> git-log
     (filter (partial re-matches #"^Date:.*"))
     (map (fn [line]
            (-> line
                (str/replace #"Date:   " ""))))
     frequencies
     (map (fn [[date freq]]
            {:date date
             :freq freq}))
     (sort-by :date)))

;; ## Data exploration



(-> dates-and-freqs
    (with-meta {:portal.viewer/default
                :portal.viewer/table})
    portal-widget)

;; ## Plotting

(def freqs-plot
  (kind/vega-lite
   {:data {:values dates-and-freqs}
    :mark :bar
    :encoding {:x {:field :date
                   :type :temporal}
               :y {:field :freq
                   :type :quantitative}}
    :width :container
    :height 200
    :background :floralwhite}))

freqs-plot

(portal-widget freqs-plot)

;; --------------------------------------------------
;; ![babashka](https://avatars.githubusercontent.com/u/64927540?s=200&v=4){height=128}
;; ![claykind](https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Clay.svg){height=128}
;; ![portal](https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg){height=128}
;; ![quarto](https://avatars.githubusercontent.com/u/67437475?s=200&v=4){height=128}
