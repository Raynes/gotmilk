(ns gotmilk.repos
  (:use gotmilk.core
        clj-github.repos
        [clojure.contrib.string :only [join]]))

(defcommand "repo"
  "Create, delete, fork, a repo. Also for adding and removing collaborators and deploy keys.
for creation (--create) : The first argument is required, and it should be the name of the repo.
Optional following keys are as follows, in order: description, homepage, and whether or
not the repo is public which should be true or false.

for deletion (--delete): Just supply the name of the repo.

for forking (--fork): Just supply the name of the repo, and the user that owns the repo you want
to fork, in that order.

for adding a collaborator (--add-collaborator): Supply the name of the repo and the user you want to add.

for removing a collaborator (--remove-collaborator): Same as for adding one.

for adding a deploy key (--add-deploy): Supply repo, key title, and the key contents.

for removing a deploy key (--remove-deploy): Supply repo name and title of the key.

Will default to --create"
  [name & [desc home pub]]
  (cond-options
   options
   :delete (-> name delete-repo format-result)
   :fork (-> (fork-repo desc name) format-result)
   :add-collaborator (-> (add-collaborator desc name) format-result)
   :remove-collaborator (-> (remove-collaborator desc name) format-result)
   :add-deploy (-> (add-deploy-key name desc home))
   :remove-deploy (-> (remove-deploy-key name desc))
   :else (-> (create-repo name
                          :description desc
                          :homepage home
                          :public (or (= pub "true") (nil? pub)))
             format-result)))