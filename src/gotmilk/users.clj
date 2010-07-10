(ns gotmilk.users
  (:use gotmilk.core
        clj-github.users
        [clojure.contrib.string :only [join]]))

(defcommand "user"
  "for getting info about a user (--show-info): Supply a username.

to follow a user (--follow): Supply the user you want to follow.

to unfollow a user (--unfollow): Same as for following.

to get a list of a user's followers (--followers): Same as for following.

to get a list of repos a user is watching (--watching): Supply the name of the user.
Optionally supply --results=<number> to limit the number of results printed, and --names
to only get the names of the repos.

to search for users (--search): Supply the search terms. Optionally supply --results=<number>
to limit the number of results printed, and --names to only get the names of users.

to set information about yourself (--set): Supply one of name, email, blog, company, location,
and the value you want to set it to."
  [one two three four]
  (cond-options
   options
   :follow (-> one follow format-result)
   :unfollow (-> one unfollow format-result)
   :followers (-> one show-followers format-result)
   :watching (-> (if-only options :names :name (show-watching one)) (take-and-format (:results options)))
   :search (-> (if-only options :names :name (search-users one)) (take-and-format (:results options)))
   :set (-> (user-set (:user *auth-map*) one two) format-result)
   :else (-> one show-user-info format-result)))