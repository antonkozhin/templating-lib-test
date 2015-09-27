(defproject dots "0.1.0-SNAPSHOT"
  :description "Test templating libraries"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [crate "0.2.4"]
                 [immoh/dommy.template "0.2.0"]
                 [hipo "0.5.0"]
                 [enfocus "2.1.1"]
                 [om "0.7.3"]
                 [prismatic/om-tools "0.3.12"]]
  :plugins [[lein-cljsbuild "1.1.0"]]
  :cljsbuild {:builds [{:source-paths ["src/test"]
                        :compiler {:output-to "resources/test.js"
                                   :optimizations :simple
                                   :pretty-print true}}]})
