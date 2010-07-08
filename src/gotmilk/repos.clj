(ns gotmilk.repos
  (:use gotmilk.core
        clj-github.repos
        [clojure.contrib.string :only [join]]))

(defcommand "collaborator"
  "Add or remove a collaborator. Use --add to add a collaborator or --rm
to remove one. Will default to --add"
  [repo user]
  (-> ((if (option? options :rm)
         remove-collaborator
         add-collaborator)
       user repo)
      format-result))