(ns clj-ebis2.main
  (:gen-class)
  (:require [clojure.java.io :as io]            
            [duct.core :as duct]
            [integrant.core :as ig]
            [cheshire.core :as json]
            [clj-http.client :as client]
            [ring.adapter.jetty :as jetty]
            ))

#_(duct/load-hierarchy)

#_(defn -main [& args]
  (let [keys (or (duct/parse-keys args) [:duct/daemon])]
    (-> (duct/read-config (io/resource "clj_ebis2/config.edn"))
        (duct/prep keys)
        (duct/exec keys))))


(def endpoint "https://api.bitflyer.jp/v1/getboardstate")


(defn get-health [endpoint]
  (let [{{:keys [health]} :body} (client/get endpoint {:accept :json :as :auto})]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:health health})}))
(defn handler [request]
  (get-health endpoint))


(defn handler2 [hndlr url]
  (fn [request] (hndlr (assoc request :endpoint url))))

(defn handler3 [request]
  (get-health (:endpoint request)))

((handler2 handler3 "https://api.bitflyer.jp/v1/getboardstate") nil)




(defmethod ig/init-key :clj-ebis2/endpoint [_ endpoint]
  endpoint)

(defmethod ig/init-key :clj-ebis2/handler [_ endpoint]
  (fn [request] (get-health endpoint)))

(defmethod ig/init-key :clj-ebis2/jetty [_ {:keys [handler] :as options}]
  (jetty/run-jetty handler options))


(defn -main []
  (jetty/run-jetty handler {:port 3000}))
