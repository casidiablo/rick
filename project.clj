(defproject rick "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [slack-rtm "0.1.0"]
                 [org.julienxx/clj-slack "0.5.0"]]
  :main ^:skip-aot rick.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
