(ns gotmilk.repos
  (:use gotmilk.core
        clj-github.repos
        [clojure.contrib.string :only [join]]))

(defcommand "collaborator"
  "Add or remove a collaborator. Use --add to add a collaborator or --rm
to remove one. Will default to --add, values taken are repo and user."
  [repo user]
  (-> ((if (option? options :rm)
         remove-collaborator
         add-collaborator)
       user repo)
      format-result))

(defcommand "deploy-key"
  "Add or remove a deploy key. Use --add to add a key, or --rm to remove one.
Will default to --add. Values taken are repo, title, and key or repo and id (title)
if you are removing a key."
  [repo title key]
  (-> (if (option? options :rm)
        (remove-deploy-key repo title)
        (add-deploy-key repo title key))
      format-result))