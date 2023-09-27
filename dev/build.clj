(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]
            [scicloj.clay.api :as clay]))

(def lib 'org.scicloj/claykind)
(def version (format "0.1.%s-alpha" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn build [_]
  (clay/render!))

(defn jar [_]
  (spit "src/scicloj/clay/version.clj"
        (str ";; Generated from dev/build.clj"
             "(ns scicloj.clay.version)" \newline \newline
             "(def version " (pr-str version) ")" \newline))
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :basis     basis
                :src-dirs  ["src"]})
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file}))

(defn deploy [_]
  (dd/deploy {:installer :remote
              :artifact  jar-file
              :pom-file  (b/pom-path {:lib lib :class-dir class-dir})}))
