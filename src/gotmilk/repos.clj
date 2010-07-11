(ns gotmilk.repos
  (:use gotmilk.core
        clj-github.repos
        [clojure.contrib.string :only [join]]))

(defcommand "repo"
  "for creation (--create) : The first argument is required, and it
should be the name of the repo. Optional following keys are as follows,
in order: description, homepage, and whether or not the repo is public which
should be true or false.

for deletion (--delete): Just supply the name of the repo.

for forking (--fork): Just supply the name of the user that owns the repo,
and the name of the repo, in that order.

for adding a collaborator (--add-collaborator): Supply the name of the repo
and the user you want to add.

for removing a collaborator (--remove-collaborator): Same as for adding one.

for adding a deploy key (--add-deploy): Supply repo, key title, and the key contents.

for removing a deploy key (--remove-deploy): Supply repo name and title of the key.

for searching (--search): Supply a string with your search terms. Optionally,
supply --language=<language> and --start-page=<start-page> to narrow your search.
Also optionally supply --results=<number> to limit the number of results gotmilk gives you.

for setting info about a repo (--set-info): Possible keys are description, homepage,
has_wiki, has_downloads, and has_issues. Supply the username of the owner of the repo,
the repo name, a key, and that key's values.

for setting repo visibility (--set-visibility): Supply the name of the repo and either
'public' or 'private'.

for watching a repo (--watch): Supply the user that owns the repo, and the name of the repo.

for unwatching a repo (--unwatch): Same as for watching one.

for showing the collaborators on a project (--show-collaborators): Supply the user that
owns the repo, and the name of the repo.

for showing the contributors to a project (--show-contributors): Supply the name of the users
that owns it, the name of the repo, and optionally --anon if you'd like to include anonymous
contributors in your results. Also optionally supply --results=<number> to limit the number
of results printed.

for showing the deploy keys a project uses (--show-deploy-keys): Supply the name of the repo.

for showing the languages used by a project (--show-languages): Same as for --show-collaborators.

for showing a repo's network (--show-network): Same as for --show-collaborators. Also,
optionally supply --results=<number> to limit the number of results.

for getting a list of repos you can push to (--show-pushable): Optionally supply --results=<number>
to limit the number of results.

for showing detailed information about a repo (--show-repo-info): Same as for --show-collaborators.

for showing all the repos a user has (--show-repos): Supply the username. Optionally supply
--results=<number> to limit the number of results.

for showing a repo's tags (--show-tags): Supply the name of the user who owns the repo and the
repo's name.

Will default to --create"
  [one two three four]
  (cond-options
   options
   :delete (-> one delete-repo format-result)
   :fork (-> (fork-repo one two) generate-clone-urls format-result)
   :add-collaborator (-> (add-collaborator two one) format-result)
   :remove-collaborator (-> (remove-collaborator two one) format-result)
   :add-deploy (-> (add-deploy-key one two three) format-result)
   :remove-deploy (-> (remove-deploy-key one two) format-result)
   :search (-> (map generate-clone-urls (apply search-repos one (apply concat (dissoc options :search))))
               (take-and-format (:results options)))
   :set-info (-> (set-repo-info one two three four) generate-clone-urls format-result)
   :set-visibility (-> (set-repo-visibility one two) generate-clone-urls format-result)
   :watch (-> (watch-repo one two) generate-clone-urls format-result)
   :unwatch (-> (unwatch-repo one two) generate-clone-urls format-result)
   :show-collaborators (-> (show-collaborators one two) format-result)
   :show-contributors (-> (show-contributors one two :include-anon? (option? options :anon))
                          (take-and-format (:results options)))
   :show-deploy-keys (-> (show-deploy-keys one) format-result)
   :show-languages (-> (show-languages one two) format-result)
   :show-network (-> (map generate-clone-urls (show-network one two)) (take-and-format (:results options)))
   :show-pushable (-> (map generate-clone-urls (show-pushable)) (take-and-format (:results options)))
   :show-repo-info  (-> (show-repo-info one two) generate-clone-urls format-result)
   :show-repos (-> (map generate-clone-urls (show-repos one)) (take-and-format (:results options)))
   :show-tags (-> (show-tags one two) format-result)
   :else (if one
           (-> (create-repo one
                            :description two
                            :homepage three
                            :public (or (= four "true") (nil? four)))
               generate-clone-urls format-result)
           moar)))