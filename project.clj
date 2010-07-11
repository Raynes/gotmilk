(defproject gotmilk "0.2.0-SNAPSHOT"
  :description "A little tool for working with Github via the command line."
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
                 [clj-github "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :main gotmilk.main)