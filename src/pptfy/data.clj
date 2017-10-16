(ns pptfy.data)

(def pdf-file (atom nil))

(def options (atom nil))

(def num-of-pages (atom 0))

(defn init-data [prmtrs]
  (reset! options (:options prmtrs))
  (reset! pdf-file (first (:arguments prmtrs))))
