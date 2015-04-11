(defproject qttt "0.1.0-SNAPSHOT"
  :description "Quantum Tic Tac Toe"
  :dependencies [[org.clojure/clojure "1.7.0-alpha6"]
                 [org.clojure/clojurescript "0.0-3126"]
                 [reagent "0.5.0" :exclusions [cljsjs/react]]
                 [org.omcljs/om "0.8.8":exclusions [cljsjs/react]]
                 [cljsjs/react-with-addons "0.12.2-4"]]
  :plugins [[lein-cljsbuild "1.0.5"]]
  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/gen"]
  :cljsbuild {:builds {:dev {:source-paths ["src"]
                             :compiler {:output-to "resources/public/gen/dev/main.js"
                                        :output-dir "resources/public/gen/dev"
                                        :asset-path "gen/dev"
                                        :main qttt.ui
                                        :optimizations :none
                                        :source-map true
                                        :foreign-libs [{:file-min "cljsjs/development/react-with-addons.inc.js",
                                                        :file "cljsjs/development/react-with-addons.inc.js",
                                                        :provides ["cljsjs.react"]}]}}}})