(ns pptfy.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as clostr]
            [clojure.java.io :as io]
            [pptfy.gui :as gui]
            [pptfy.data :as d :refer [pdf-file]])
  (:import [javafx.application Application Platform]
           [javafx.stage Screen])
  (:gen-class
   :extends javafx.application.Application
   :name pptfy.core))

(def cli-options
  [["-mw" "--main-screen-num" "Main screen number"
    :default 0
    :parse-fn #(Integer/parseInt %)
    :validate [#(and (<= 0 %) (< % (count (Screen/getScreens)))) "Not found the num screen"]]
   ["-aw" "--audience-screen-num" "Audience screen number"
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(and (<= 0 %) (< % (count (Screen/getScreens)))) "Not found the num screen"]]
   ["-h" "--help" "Show help"]])

(defn -start [this stage]
  (if (and (not (nil? @pdf-file))
           (= "pdf" (last (clostr/split @pdf-file #"\.")))
           (.exists (io/file @pdf-file)))
    (do (println "Num of Screens :" (count (Screen/getScreens)))
        (clojure.lang.RT.)
        (gui/start-gui stage))
    (do (println "Please select pdf file.")
        (Platform/exit))))

(defn help-println [summary]
  (println
"""USAGE
[command] [filename] [options]

options
""")
  (println summary))


(defn -main
  "start pptfy"
  [& args]
  (let [prmtrs (parse-opts args cli-options)]
    (if (:help (:options prmtrs))
      (help-println (:summary prmtrs))
      (do (d/init-data prmtrs)
          (Application/launch pptfy.core (into-array String []))))))
