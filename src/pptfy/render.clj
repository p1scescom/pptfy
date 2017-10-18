(ns pptfy.render
  (:require [clojure.java.io :as io]
            [pptfy.data :as d :refer [pdf-file]])
  (:import [javafx.embed.swing SwingFXUtils]
           [org.apache.pdfbox.pdmodel PDDocument]
           [org.apache.pdfbox.rendering PDFRenderer]))

(defn pdf-render [pdfr number-page]
  (-> pdfr (.renderImageWithDPI number-page 300)))

(defn get-pages []
  (let [pdf-path (io/file @pdf-file)
        pdd (PDDocument/load pdf-path)
        pdfr (PDFRenderer. pdd)
        num-of-pages (.getNumberOfPages pdd)]
    (reset! d/num-of-pages num-of-pages)
    (doall (map #(SwingFXUtils/toFXImage % nil) (map #(pdf-render pdfr %) (range num-of-pages))))))
