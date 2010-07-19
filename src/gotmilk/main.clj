(ns gotmilk.main
  (:use gotmilk.core
        gotmilk.users
        gotmilk.repos)
  (:gen-class))

(defn -main [& args]
  (binding [*command-line-args* args]
    (if (some #(= "--self-install" %) args) (self-install) (run))))

(run)