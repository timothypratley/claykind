(ns scicloj.kind-adapters.portal)
;; TODO: does kind-portal already do this?

(defn as-portal-hiccup [hiccup]
  (with-meta
    hiccup
    {:portal.viewer/default :portal.viewer/hiccup}))

(defn img [url]
  (as-portal-hiccup
    [:img {:height 50 :width 50
           :src url}]))

(defn md [text]
  (as-portal-hiccup
    [:portal.viewer/markdown
     text]))

(defn vega-lite-point-plot [data]
  (as-portal-hiccup
    [:portal.viewer/vega-lite
     (-> {:data {:values data},
          :mark "point"
          :encoding
          {:size {:field "w" :type "quantitative"}
           :x {:field "x", :type "quantitative"},
           :y {:field "y", :type "quantitative"},
           :fill {:field "z", :type "nominal"}}})]))
