(ns lorrem.app
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.Uri :as uri]
            [dommy.core :as dommy :refer-macros [sel1]]
            [hipo.core :as hipo])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(goog-define API_URL "ADD CORRECT API ENDPOINT HERE")
(goog-define API_AUTH "ADD CORRECT TOKEN HERE")

(defn capitalize-first-letter [s]
  (str (string/capitalize (first s)) (subs s 1)))

(defn add-dot-to-end-if-needed [s]
  (if-not (string/ends-with? s ".") (str s ".") s))

(defn format-lorr [s]
  (str (add-dot-to-end-if-needed (capitalize-first-letter (string/trim s)))))

(defn selection-with-lorr-tag []
  (str "[LÖR] " (.getSelection (gdom/getDocument))))

(defn write-lorr-to-clipboard []
  (.writeText (.-clipboard (.-navigator (gdom/getWindow))) (selection-with-lorr-tag)))

(defn handle-copy [^js/BrowserEvent event]
  (.preventDefault event)
  (write-lorr-to-clipboard))

(defn select-lorr-node-content [range event]
  (.selectNodeContents range (.-lastChild (.-parentElement (.-target event))))
  (let [selection (.getSelection (gdom/getDocument))]
    (.removeAllRanges selection)
    (.addRange selection range)))

(defn select-lorr [event]
  (select-lorr-node-content (.createRange (gdom/getDocument)) event)
  (write-lorr-to-clipboard))

(defn create-lorr-html-element [s]
  (hipo/create
   [:li {:on-copy handle-copy}
    [:img {:src "images/bug.svg" :on-click select-lorr :tabindex 1 :on-focus select-lorr}]
    (format-lorr s)]))

(defn create-new-lorr [s]
  (dommy/append! (sel1 :#lorrem-list) (create-lorr-html-element s)))

(defn get-query-param [param-key]
  (let [param-value (.getParameterValue (uri/parse (.-href js/document.location)) (name param-key))]
    (if param-value
      (js->clj param-value :keywordize-keys true)
      nil)))

(defn mock-remote-call []
  (doall (map create-new-lorr ["              test string :D" "Another Test STRING.   "])))

(defn make-remote-call [endpoint]
  (go (let [response (<! (http/get endpoint
                                   {:with-credentials? false
                                    :headers {"Authorization" API_AUTH}
                                    :query-params {"amount" 100 "start" (get-query-param :start)}}))]
        (doall (map create-new-lorr (:lorrem (:body response)))))))

(defn init []
  (if goog.DEBUG
    (mock-remote-call)
    (make-remote-call API_URL)))
