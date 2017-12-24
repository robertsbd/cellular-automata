(defproject cellular-automata "0.1.0-SNAPSHOT"
  :description "Game of life using the seesaw library for swing"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [seesaw "1.4.5"]
                 [net.mikera/core.matrix "0.61.0"]
                 [net.mikera/vectorz-clj "0.47.0"]]
  :main ^:skip-aot cellular-automata.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
