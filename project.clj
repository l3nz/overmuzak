(defproject overmuzak "0.1.0-SNAPSHOT"
  :description "Overtone Muzak"
  :url "https://github.com/l3nz/overmuzak"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [overtone "0.10.6"]
                 ;[leipzig "0.10.0"]
                 [pjagielski/disclojure "0.1.4" :exclusions [overtone]]
                 [clj-http "3.10.3"]]

  :aliases {"fix" ["cljfmt" "fix"]
            ; Kondo
            "clj-kondo" ["with-profile" "kondo"
                         "trampoline" "run" "-m"
                         "clj-kondo.main" "--" "--lint" "src/" "--cache" ".cli-kondo-cache"]
            "clj-kondo-test" ["with-profile" "kondo"
                              "trampoline" "run" "-m"
                              "clj-kondo.main" "--" "--lint" "test/" "--cache" ".cli-kondo-cache"]}

  :plugins [[lein-eftest "0.5.1"]
            [jonase/eastwood "0.2.5"]
            [lein-kibit "0.1.6"]
            [lein-cljfmt "0.6.6"]
            [lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.11"]
            [lein-ancient "0.6.15"]
            [lein-cloverage "1.1.2"]
            [lein-ancient "0.6.15"]]

  :profiles {:test
             {:dependencies [[cljc.java-time "0.1.11"]]}
  
             :kondo
             {:dependencies [[org.clojure/clojure "1.10.1"]
                             [clj-kondo "2020.05.09"]]}})
