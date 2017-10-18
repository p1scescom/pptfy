(ns pptfy.operation
  (:require [pptfy.data :as d :refer [pdf-file]]
            [pptfy.render :refer [get-pages]])
  (:import [javafx.scene.input KeyCode KeyEvent]))

(def num-page (atom 0))

(def page-history (atom [0]))

(def page-history-num (atom 0))

(def command-numbers (atom []))

(def command (atom []))

(defn command-numbers2num []
  (loop [num 0
         nums @command-numbers]
    (let [next-num (get nums 0)]
      (if (nil? next-num)
        num
        (recur (+ (* 10 num ) next-num) (subvec nums 1))))))

(defn reset-page-history-num []
  (reset! page-history-num 0))

(defn reset-command-number []
  (reset! command-numbers []))

(defn add-command-num [key-code]
  (let [number (cond
                 (some #(= key-code %) (list KeyCode/DIGIT1 KeyCode/NUMPAD1 KeyCode/SOFTKEY_1)) 1
                 (some #(= key-code %) (list KeyCode/DIGIT2 KeyCode/NUMPAD2 KeyCode/SOFTKEY_2)) 2
                 (some #(= key-code %) (list KeyCode/DIGIT3 KeyCode/NUMPAD3 KeyCode/SOFTKEY_3)) 3
                 (some #(= key-code %) (list KeyCode/DIGIT4 KeyCode/NUMPAD4 KeyCode/SOFTKEY_4)) 4
                 (some #(= key-code %) (list KeyCode/DIGIT5 KeyCode/NUMPAD5 KeyCode/SOFTKEY_5)) 5
                 (some #(= key-code %) (list KeyCode/DIGIT6 KeyCode/NUMPAD6 KeyCode/SOFTKEY_6)) 6
                 (some #(= key-code %) (list KeyCode/DIGIT7 KeyCode/NUMPAD7 KeyCode/SOFTKEY_7)) 7
                 (some #(= key-code %) (list KeyCode/DIGIT8 KeyCode/NUMPAD8 KeyCode/SOFTKEY_8)) 8
                 (some #(= key-code %) (list KeyCode/DIGIT9 KeyCode/NUMPAD9 KeyCode/SOFTKEY_9)) 9
                 :else 0)]
    (swap! command-numbers conj number)
    nil))

(defn change-data []
  (swap! page-history conj @num-page))

(defn inc-page []
  (if (< (dec @d/num-of-pages) @num-page)
    (do (reset-command-number)
        nil)
    (do (let [num (command-numbers2num)]
          (if (<= num 1)
            (swap! num-page inc)
            (if (> (+ @num-page num) (dec @d/num-of-pages))
              (reset! num-page @d/num-of-pages)
              (swap! num-page + num))))
        (change-data)
        (reset-command-number)
        @num-page)))

(defn dec-page []
  (if (>= 0 @num-page)
    (do (reset-command-number)
        nil)
    (do (let [num (command-numbers2num)]
          (if (<= num 1)
            (swap! num-page dec)
            (if (<= (- @num-page num) 0)
              (reset! num-page 0)
              (swap! num-page - num))))
        (change-data)
        (reset-command-number)
        @num-page)))

(defn key-input [^KeyEvent key-event]
  (when (= KeyEvent/KEY_PRESSED (.getEventType key-event))
    (let [key-code (.getCode key-event)]
      (if (.isDigitKey key-code)
        (add-command-num key-code)
        (do (condp = key-code
              KeyCode/L (inc-page)
              KeyCode/H (dec-page)
              KeyCode/Q (reset-page-history-num)
              nil))))))
