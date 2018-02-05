(ns clj-ebis2.boundary.message
  (:require [integrant.core :as ig])
  (:import [com.google.pubsub.v1 SubscriptionName PubsubMessage]
           [com.google.cloud.pubsub.v1 Subscriber MessageReceiver]))



(def subscription-name (SubscriptionName/create "clj-ebis2-194210" "clj-ebis2"))


(def receiver
  (reify MessageReceiver
    (receiveMessage [this message consumer]
      (println (.. message getMessageId))
      (println (.. message getData toStringUtf8))
      #_(. consumer ack))))

(def p (Subscriber/newBuilder subscription-name receiver))
#_(def a (. m/p build))
#_(. a startAsync)

(defprotocol Receiver
  (start [this])
  (stop [this]))

(defrecord PubSubReceiver [subscriber]
    Receiver
    (start [this]
      (println "start subscription")
      (. subscriber startAsync))
    (stop [this]
        (. subscriber stopAsync)
        (println "stop subscription")))

(defmethod ig/init-key :clj-ebis2.boundary/message [_ {:keys [subscription-name]}]
  (let [subscriber (. (Subscriber/newBuilder
                       subscription-name
                       (reify MessageReceiver
                         (receiveMessage [this message consumer]
                           (println (.. message getData toStringUtf8))
                           (. consumer ack)))) build)]
    #_(. subscriber startAsync)
    #_subscriber
    (let [receiver (->PubSubReceiver subscriber)]
      (start receiver)
      receiver)))

(defmethod ig/halt-key! :clj-ebis2.boundary/message [_ receiver]
  (stop receiver))
