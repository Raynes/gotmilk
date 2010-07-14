(ns gotmilk.core
  (:use [clojure.java.shell :only [sh]]
        [clj-github.core :only [with-auth]]
        [clojure.java.io :only [file copy]])
  (:require [clojure.string :as s]))

(def *version* "0.2.1")

(defn get-config [parameter]
  (apply str (butlast (:out (sh "git" "config" "--global" (str "github." parameter))))))

(def help-map (atom {}))

(def *auth-map* {:user (get-config "user")
                 :pass (get-config "password")})

(defn parse-options [options]
  (let [pred #(or (.startsWith % "--") (.startsWith % "-"))]
    [(into {}
           (map #(if (.startsWith % "--")
                   (let [[front back] (.split % "=")]
                     [(keyword (apply str (drop 2 front))) back])
                   [(keyword (apply str (drop 1 (take 2 %))))
                    (apply str (drop 2 %))])
                (filter pred options)))
     (remove pred options)]))

(defn any-option?
  "Tests whether or not an option was provided."
  [options opt]
  ((into #{} (keys options)) opt))

(defn option?
  "Tests whether or not any of the provided options were provided."
  [option-map & options]
  (some identity (map (partial any-option? option-map) options)))

(defn format-result [result]
  (str "\n"
       (cond
        (map? result) (apply
                       str
                       (interpose
                        "\n"
                        (for [[k v] result]
                          (let [strk (if (keyword? k)
                                       (->> k str rest (apply str) (#(.replaceAll % "_" " ")))
                                       k)]
                            (str strk " -> " v)))))
        (string? result) result
        (nil? result) "wut"
        (not (seq result)) "Nothing interested happened."
        :else (apply str (interpose ", " result)))
       "\n"))

(defn generate-clone-urls [res-map]
  (if (map? res-map)
    (let [owner (if-let [owner (:owner res-map)] owner (:username res-map))
          name (:name res-map)]
      (assoc res-map
        "clone url (read only)" (str "git://github.com/" owner "/" name ".git")
        "ssh clone url (read write)" (str "git@github.com:" owner "/" name ".git")))
    res-map))

(defmulti execute (comp identity first vector))

(def moar "\nI need more information than that.\n")

(defmacro defcommand [trigger help args & body]
  `(do
     (swap! help-map assoc ~trigger ~help)
     (defmethod execute ~trigger
       [worthless# ~'options & ~args]
       (cond
        ~@(apply concat
                 (for [[option moars happen] (partition 3 body)]
                   `(~(if (= option :else) :else `(option? ~'options ~option))
                     (if (seq ~moars)
                       (if (some nil? ~moars) moar ~happen)
                       ~happen))))))))

(defmethod execute :default
  [& _] "\nInvalid command. Use the 'help' command if you don't know what to do.\n")

(defcommand "help"
  "Get help!"
  [cmd]
  :else [] (if cmd
             (str "\n" (@help-map cmd) "\n")
             "\nCommands are user and repo. Do gotmilk help <command> for more.\n"))

(defn take-and-format [x & [n]]
  (let [n (when n (Integer/parseInt n))
        x (if n (take n x) x)]
    (if (some map? x)
      (apply str (map format-result x))
      (format-result x))))

(defn if-only [options key only results]
  (if (option? options key) (map only results) results))

(defn self-install []
  (println "Installing gotmilk.\n")
  (let [cwd (System/getProperty "user.dir")
        home (System/getProperty "user.home")
        bin (file home "bin")
        dotgotmilk (file home ".gotmilk")
        current-jar (file (first
                           (filter
                            #(re-find (re-pattern (str "gotmilk(\\.|-"
                                                       *version*
                                                       "-standalone\\.|-standalone\\.)(jar|zip)")) %)
                            (s/split (System/getProperty "java.class.path")
                                     (re-pattern (System/getProperty "path.separator"))))))]
    (println "Creating ~/.gotmilk...\n")
    (when-not (.exists dotgotmilk) (.mkdir dotgotmilk))
    (println (str "Copying " current-jar " to ~/.gotmilk...\n"))
    (copy (file cwd current-jar) (file dotgotmilk current-jar))
    (println "Creating a start up script in ~/bin")
    (when-not (.exists bin) (.mkdir bin))
    (doto (file bin "gotmilk")
      (spit (str "java -jar ~/.gotmilk/" current-jar " $@"))
      (.setExecutable true))
    (println "Installation complete. Please make sure ~/bin is on your PATH.")))

(defn run-cycle [cmd-args]
  (let [[action & args] cmd-args
        [options argies] (parse-options args)]
    (if (= "--help" action)
      (println (apply execute "help" {} argies))
      (println (apply execute action options argies)))))

(defcommand "exit"
  "Exits the application. For usage in the shell."
  []
  :else [] (do (println "\nBai!\n")
               (System/exit 0)))

(defn run-as-shell []
  (println "Welcome to the gotmilk shell. Enter commands and their options like you normally would.")
  (while true
    (print "gotmilk> ")
    (flush)
    (run-cycle
     (filter seq
             (flatten
              (map #(%1 %2)
                   (cycle [#(s/split % #" ") identity])
                   (s/split (read-line) #"(?<!\\)(?:'|\")")))))))

(defn run []
  (with-auth *auth-map*
    (if (= (first *command-line-args*) "--shell")
      (run-as-shell)
      (run-cycle *command-line-args*))))