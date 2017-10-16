(defproject pptfy "0.1.0-SNAPSHOT"
  :description "PPTFY is a pdf presentation tool for you."
  :url "https://github.com/p1scescom/pptfy"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2017
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.apache.pdfbox/pdfbox "2.0.7"]]
  :main pptfy.core
  :aot [pptfy.core pptfy.gui]
  :profiles {uberjar [:aot :all]})
