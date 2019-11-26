(defproject github-oauth-play "0.1.0-SNAPSHOT"
  :description "Playing with github oauth"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [http-kit "2.3.0"]
                 [tentacles "0.5.1"]
                 [techascent/tech.config "0.3.7"]
                 [hiccup "1.0.5"]
                 [bidi "2.1.6"]
                 [org.clojure/data.json "0.2.7"]
                 [ring "1.7.0"]]
  :main ^:skip-aot github-oauth-play.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
