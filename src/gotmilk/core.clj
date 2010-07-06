(ns gotmilk.core
  (:use [clj-github core issues repos users gists]
        [clojure.contrib.shell :only [sh]]))

(defn get-config [parameter]
  (apply str (butlast (sh "git" "config" "--global" (str "github." parameter)))))

(def commands (atom {}))

(def auth-map {:user (get-config "user")
               :pass (get-config "password")})

(defn format-result [result]
  (cond
   (map? result) (apply
                  str
                  (for [[k v] result]
                    (str (->> k str rest (apply str) (#(.replaceAll % "_" " "))) " -> " v "\n")))
   (string? result) result
   (vector result) (apply str (for [x result] (str x ", ")))
   :else "I'm stupid."))

(defmulti execute (comp identity first vector))

(defmacro defcommand [trigger help args & body]
  `(do
     (swap! commands assoc ~trigger ~help)
     (defmethod execute ~trigger [worthless# & ~args] ~@body)))

(defcommand "user-info"
  "Get a ton of information about a user."
  [user] (-> user show-user-info format-result))

(defn do-shit [[action & args]]
  (println (str "\n" (apply execute action args))))

(with-auth auth-map (do-shit *command-line-args*))