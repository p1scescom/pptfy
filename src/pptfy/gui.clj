(ns pptfy.gui
  (:require [pptfy.data :refer [pdf-file options num-of-pages]]
            [pptfy.render :refer [get-pages]]
            [pptfy.operation :as op])
  (:import [javafx.stage Stage Screen]
           [javafx.scene Scene]
           [javafx.scene.control Label]
           [javafx.scene.layout AnchorPane BorderPane GridPane Pane VBox]
           [javafx.scene.input KeyCombination KeyCombination$Modifier KeyCodeCombination KeyCode]
           [javafx.scene.image ImageView])
  (:gen-class
   :naem pptfy.gui))

(def main-window (promise))

(def main-grid (GridPane.))

(def main-scene (Scene. main-grid))

(def now-page (ImageView.))

(def next-page (ImageView.))

(def audience-window (promise))

(def audience-place (BorderPane.))

(def audience-scene (Scene. audience-place))

(def audience-page (ImageView.))

(def pdf-images (atom nil))

(defonce finish-page (atom nil))

(defn choice-set-page [image target] (.setImage target image ))

(defn choice-mv-page [mv-page place target]
  (if (>= mv-page @num-of-pages)
    (do (-> place .getChildren (.remove target))
        (when-not (-> place .getChildren (.contains @finish-page))
          (-> place .getChildren (.add @finish-page))))

    (do (-> place .getChildren (.remove @finish-page))
        (choice-set-page (nth @pdf-images mv-page) target)
        (when-not (-> place .getChildren (.contains target))
          (-> place .getChildren (.add target))))))

(defn mv-now-page [mv-page]
  (choice-mv-page mv-page main-grid now-page))

(defn mv-next-page [mv-page]
  (let [mv-page (inc mv-page)]
    (choice-mv-page mv-page main-grid next-page)))

(defn mv-audience-page [mv-page]
  (choice-mv-page mv-page audience-place audience-page))

(defn mv-pages [mv-page]
  (mv-now-page mv-page)
  (mv-next-page mv-page)
  (mv-audience-page mv-page))

(defn init-windows [main-stage]
  (reset! finish-page (Label. "finish"))
  (deliver main-window main-stage)
  (deliver audience-window (new Stage)))

(defn get-screen-info []
  {:primary (Screen/getPrimary)
   :screens (Screen/getScreens)})

(defn set-windows [screen-info]
  (doto now-page
    (.setSmooth true)
    (.setPreserveRatio true)
    (.setFitHeight 500)
    (.setFitWidth 500))
  (doto next-page
    (.setSmooth true)
    (.setPreserveRatio true)
    (.setFitHeight 500)
    (.setFitWidth 500))

  (doto main-grid
    (.add now-page 1 0)
    (.add next-page 2 0))

  (doto @main-window
    (.setTitle "Main Window")
    (.setScene main-scene)
    (.setX (.getMinX (.getBounds (.get (:screens screen-info) (:main-screen-num @options)))))
    (.setY (.getMinY (.getBounds (.get (:screens screen-info) (:main-screen-num @options))))))

  (doto audience-page
    (.setSmooth true)
    (.setPreserveRatio true)
    (-> .fitWidthProperty (.bind (-> audience-place .widthProperty (.multiply 1.0))))
    (-> .fitHeightProperty (.bind (-> audience-place .heightProperty (.multiply 1.0)))))

  (doto audience-place
    (.setCenter audience-page)
    #_(-> .getChildren (.add audience-page)))

  (doto @audience-window
    (.setTitle "Audience Window")
    (.setScene audience-scene)
    (.setX (.getMaxX (.getBounds (.get (:screens screen-info) (:audience-screen-num @options)))))
    (.setY (.getMaxY (.getBounds (.get (:screens screen-info) (:audience-screen-num @options)))))
    (.setFullScreen true)))

(defn set-pdf-images []
  (reset! pdf-images (get-pages))
  (mv-pages @op/num-page))

(defn start-gui [main-stage]
  (let [screen-info (get-screen-info)]
    (dorun (map println (:screens screen-info)))
    (init-windows main-stage)
    (set-windows screen-info)
    (set-pdf-images)
    (op/init-operation)
    (.show @audience-window)
    (.show @main-window)))
