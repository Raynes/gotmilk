(ns gotmilk.core
  (:use [clojure.contrib.shell :only [sh]]
        [clj-github.core :only [with-auth]]))

(defn get-config [parameter]
  (apply str (butlast (sh "git" "config" "--global" (str "github." parameter)))))

(def commands (atom {}))

(def auth-map {:user (get-config "user")
               :pass (get-config "password")})

(defn parse-options [options]
  (let [pred #(or (.startsWith % "--") (.startsWith % "-"))]
    [(into {}
           (map #(if (.startsWith % "--")
                   (let [[front back] (.split % "=")] [(apply str (drop 2 front)) back])
                   [(apply str (drop 1 (take 2 %))) (apply str (drop 2 %))])
                (filter pred options)))
     (remove pred options)]))

(defn format-result [result]
  (cond
   (map? result) (apply
                  str
                  (for [[k v] result]
                    (str (->> k str rest (apply str) (#(.replaceAll % "_" " "))) " -> " v "\n")))
   (string? result) result
   (vector? result) (str (apply str (interpose ", " result)) "\n")
   :else "I'm stupid."))

(defmulti execute (comp identity first vector))

(defmacro defcommand [trigger help args & body]
  `(do
     (swap! commands assoc ~trigger ~help)
     (defmethod execute ~trigger [worthless# & ~args] ~@body)))

(defn do-shit [[action & args]]
  (println (str "\n" (apply execute action args))))

(defn run [] (with-auth auth-map (do-shit *command-line-args*)))