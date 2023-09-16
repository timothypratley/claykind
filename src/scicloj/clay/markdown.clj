(ns scicloj.clay.markdown
  "Thin wrapper for Flexmark, a Java Markdown library with comprehensive features."
  (:import (com.vladsch.flexmark.html HtmlRenderer)
           (com.vladsch.flexmark.parser Parser)
           (com.vladsch.flexmark.util.data MutableDataSet)))

(set! *warn-on-reflection* true)

;; TODO: users may want to change options, provide a way to do that
(def ^MutableDataSet options (new MutableDataSet))

;;// uncomment to set optional extensions
;;//options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

;;// uncomment to convert soft-breaks to hard breaks
;;//options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

(def ^Parser parser (.build (Parser/builder options)))
(def ^HtmlRenderer renderer (.build (HtmlRenderer/builder options)))

(defn render [^String s]
  (->> (.parse parser s)
       (.render renderer)))
