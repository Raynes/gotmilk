# gotmilk

A little tool for working with Github via the command-line. Sometimes quicker than doing things through the Github web UI. It's written in Clojure.

## Usage

gotmilk works by using a small set of command with lots of options for each to accomplish different things. I'll list every option for each command here.

### gotmilk repo

example: `gotmilk repo --fork nixeagle nisp`

for creation (create): The first argument is required, and it should be the name of the repo. 
Optional following  keys are as follows, in order: description, homepage, and whether or not the repo 
is public which should be true or false.

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

for showing detailed information about a repo (default): Same as for --show-collaborators.

for showing all the repos a user has (--show-repos): Supply the username. Optionally supply
--results=<number> to limit the number of results.

for showing a repo's tags (--show-tags): Supply the name of the user who owns the repo and the
repo's name.

Will default to --create


### gotmilk user

example: `gotmilk --search "John Doe" --names`

for getting info about a user (default): Supply a username.

to follow a user (--follow): Supply the user you want to follow.

to unfollow a user (--unfollow): Same as for following.

to get a list of a user's followers (--followers): Same as for following.

to get a list of repos a user is watching (--watching): Supply the name of the user.
Optionally supply --results=<number> to limit the number of results printed, and --names
to only get the names of the repos.

to search for users (--search): Supply the search terms. Optionally supply --results=<number>
to limit the number of results printed, and --names to only get the names of users.

to set information about yourself (--set): Supply one of name, email, blog, company, location,
and the value you want to set it to.

defaults to show-info


There is also a vanilla help command that I hope to improve in the future. It prints the above help without much special formatting.

## Installation

Either clone the repo, or fetch gotmilk-standalone.jar from the Downloads section. After that, just do this:

    java -jar gotmilk-standalone.jar --self-install

and it will install all by it's lonesome. After that, make sure ~/bin is on your PATH, and then use `gotmilk help`.

## TODO

Lots of stuff. Here are some important things.

- Wrap clj-github's issue API.
- Fix edge cases and do as much valid data checking as possible.
- Add cool stuff that nobody else has.
- Purchase a large 2 topping pizza from Pizza Hut.

## License

This work is Licensed under the EPL, the same thing that Clojure itself is licensed under. You can find a copy of the EPL in the root of this directory.