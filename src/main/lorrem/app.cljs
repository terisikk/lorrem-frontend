(ns lorrem.app
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.events :as events])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn capitalize-first-letter [s]
  (str (string/capitalize (first s)) (subs s 1)))

(defn format-lorr [s]
  (capitalize-first-letter (string/trim s)))

(defn selection-with-lorr-tag []
  (str "[LÖR] " (.getSelection (gdom/getDocument))))

(defn write-lorr-to-clipboard []
  (.writeText (.-clipboard (.-navigator (gdom/getWindow))) (selection-with-lorr-tag)))

(defn handle-copy [^js/BrowserEvent event] 
  (.preventDefault event)
  (write-lorr-to-clipboard))

(defn select-lorr [event]
  (let [range (.createRange (gdom/getDocument))]
    (js/console.log (.-lastChild (.-parentElement (.-target event))))
    (.selectNodeContents range (.-lastChild (.-parentElement (.-target event))))
    (let [selection (.getSelection (gdom/getDocument))]
      (.removeAllRanges selection)
      (.addRange (.getSelection (gdom/getDocument)) range))) 
  (write-lorr-to-clipboard))

(defn create-new-lorr [s]
  (let [lorr (gdom/createElement "li")]
    (gdom/setTextContent lorr (format-lorr s))
    (gdom/appendChild (.querySelector js/document "#lorrem-list") lorr)
    (events/listen lorr "copy" handle-copy)

    (let [icon (gdom/createElement "img")]
      (gdom/setProperties icon (clj->js {"src" "images/bug.svg"})) 
      (gdom/insertChildAt lorr icon 0)
      (events/listen icon "click" select-lorr))))

;; Switch make-remote-call to this in development
;;(defn mock-remote-call [endpoint]
;;  (doall (map create-new-lorr ["              test string :D" "Another Test STRING.   "])))

(defn make-remote-call [endpoint]
  (go (let [response (<! (http/get endpoint
                                   {:with-credentials? false
                                    :headers {"Authorization"
                                              "ADD CORRECT TOKEN HERE"}}))]
        (doall (map create-new-lorr (:lorrem (:body response)))))))

(defn init []
  (make-remote-call "ADD CORRECT API ENDPOINT HERE"))
