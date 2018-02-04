(ns clj-ebis2.handler.handler
  (:require [integrant.core :as ig]))


(defn get-health [endpoint]
  (let [{{:keys [health]} :body} (client/get endpoint {:accept :json :as :auto})]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:health health})}))

(defmethod ig/init-key :clj-ebis2.handler/handler [_ endpoint]
  (fn [request] (get-health endpoint)))
