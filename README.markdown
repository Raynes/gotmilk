# gotmilk

A little tool for working with Github via the command-line. Sometimes quicker than doing things through the Github web UI. It's written in Clojure.

## Usage

To use this application, you need to have a Github account, and have git installed on your system and in your PATH. For configuration, gotmilk will ask git to give some information. Git requests that you use git's `git config --global github.x` for authentication information. So, you'll need to use git to set github.user and github.password. Here is an example of doing so:

    git config --global github.user Raynes
    git config --global github.password mypassword

http://develop.github.com/p/general.html points out that using tokens for authentication is deprecated, and that one should use HTTP Basic Auth for authentication. OAuth will be preferred eventually, but for now, we're going to use Basic Auth.

## Installation

FIXME: write

## License

This work is Licensed under the EPL, the same thing that Clojure itself is licensed under. You can find a copy of the EPL in the root of this directory.