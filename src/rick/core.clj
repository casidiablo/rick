(ns rick.core
  (:require [slack-rtm.core :as rtm]
            [clj-slack.chat :as chat]
            [clojure.core.async :refer [chan <!!]]
            [clj-slack.core :refer [slack-request stringify-keys]]
            [environ.core :refer [env]]))

(def token (env :rick-token))
(def target-user (env :rick-user))
(def target-channel (env :rick-channel))
(def reaction-emoji (or (env :rick-reaction) "white_check_mark"))

(def connection {:api-url "https://slack.com/api" :token token})

(defn on-reaction-removed [{:keys [user item reaction] :as msg}]
  (if (and (= user target-user)
           (= reaction reaction-emoji)
           (= (:channel item) target-channel))
    (do
      (println "Deleting message" msg)
      (chat/delete connection (:ts item) target-channel))))


(defn add-reaction
  "Add a checkmark reaction to the provided message"
  [optionals]
  (->> optionals
       stringify-keys
       (merge {"name" reaction-emoji})
       (slack-request connection "reactions.add")))

(defn on-message-received [{:keys [user channel ts] :as msg}]
  (if (and (= user target-user) (= channel target-channel))
    (do
      (println "Adding reaction to" msg)
      (add-reaction {:channel channel :timestamp ts})
      (println "Reaction added."))))


(defn -main [& args]
  (println "Initializing rick...")

  (let [rtm-conn (rtm/connect token)
        events-publication (:events-publication rtm-conn)]

    ;; listen for reaction_removed events
    (rtm/sub-to-event events-publication :reaction_removed on-reaction-removed)

    ;; listen for messages added
    (rtm/sub-to-event events-publication :message on-message-received)

    ;; block for ever
    (<!! (chan))))
