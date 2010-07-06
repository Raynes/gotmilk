(ns gotmilk.users
  (:use gotmilk.core
        clj-github.users
        clojure.contrib.command-line))

(defcommand "user-info"
  "Get a ton of information about a user."
  [user] (-> user show-user-info format-result))

(defcommand "follow"
  "Follow a user."
  [user] (-> user follow format-result))

(defcommand "followers"
  "Get a list of a user's followers."
  [user] (-> user show-followers format-result))