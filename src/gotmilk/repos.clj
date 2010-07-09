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

(defcommand "repo"
  "Create or delete a repo.
for creation (--create) : The first argument is required, and it should be the name of the repo.
Optional following keys are as follows, in order: description, homepage, and whether or
not the repo is public which should be true or false.

for deletion (--delete): Just supply the name of the repo."
  [name & [desc home pub]]
  (if (option? options :delete)
    (-> name delete-repo format-result)
    (-> (create-repo name
         :description desc
         :homepage home
         :public (or (= pub "true") (nil? pub)))
        format-result)))