(ns gotmilk.core
  (:use [clojure.contrib.shell :only [sh]]
        [clj-github.core :only [with-auth]]
        [clojure.java.io :only [file copy]]))

(defn get-config [parameter]
  (apply str (butlast (sh "git" "config" "--global" (str "github." parameter)))))

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

(defmacro cond-options [options & clauses]
  `(cond
    ~@(apply concat
             (for [[option happen] (partition 2 clauses)]
               (if (= option :else)
                 `(:else ~happen)
                 `((option? ~options ~option) ~happen))))))

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
    (let [owner (:owner res-map)
          name (:name res-map)]
      (assoc res-map
        "clone url (read only)" (str "git://github.com/" owner "/" name ".git")
        "ssh clone url (read write)" (str "git@github.com:" owner "/" name ".git")))
    res-map))

(defmulti execute (comp identity first vector))

(defmacro defcommand [trigger help args & body]
  `(do
     (swap! help-map assoc ~trigger ~help)
     (defmethod execute ~trigger [worthless# ~'options & ~args] ~@body)))

(defcommand "help"
  "Get help!"
  [cmd]
  (if cmd
    (println (@help-map cmd))
    (println "\nCommands are user and repo. Do gotmilk <command> for more.")))

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
        dotgotmilk (file home ".gotmilk")]
    (println "Creating ~/.gotmilk...\n")
    (when-not (.exists dotgotmilk) (.mkdir dotgotmilk))
    (println "Copying gotmilk-standalone.jar to ~/.gotmilk...\n")
    (copy (file cwd "gotmilk-standalone.jar") (file dotgotmilk "gotmilk-standalone.jar"))
    (println "Creating a start up script in ~/bin")
    (when-not (.exists bin) (.mkdir bin))
    (doto (file bin "gotmilk")
      (spit "java -jar ~/.gotmilk/gotmilk-standalone.jar $@")
      (.setExecutable true))
    (println "Installation complete. Please make sure ~/bin is on your PATH.")))

(defn run []
  (with-auth *auth-map*
    (let [[action & args] *command-line-args*
          [options argies] (parse-options args)]
      (println (apply execute action options argies)))))