(defproject jokes "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring/ring-jetty-adapter "0.3.1"]
                 [compojure "0.6.0-RC3"]
                 [hiccup "0.3.2"]
                 [digest "1.0.0-SNAPSHOT"]
                 [redis-clojure "1.0.4"]]
  :main jokes.core)
