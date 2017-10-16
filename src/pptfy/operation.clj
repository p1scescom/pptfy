(ns pptfy.operation
  (:require [pptfy.data :refer [pdf-file]]
            [pptfy.render :refer [get-pages]]))

(def num-page (atom 0))

(def page-history (atom (list)))

(def page-history-num (atom 0))

(def command-number (atom (list)))

(def command (atom (list)))

(defn init-operation [] ())
