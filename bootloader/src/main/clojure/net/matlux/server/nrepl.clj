(ns net.matlux.server.nrepl
  (:require [clojure.tools.nrepl.server :as nrepl]))

(def server nil)

(defn safe-stop-server [server]
	(when (not (nil? server))
		(nrepl/stop-server server)))

(defn start-server-now [port]
  (locking #'server
    (safe-stop-server server)
    ;; Clear the stopped handle even if binding the replacement port fails.
    (alter-var-root #'server (constantly nil))
    (alter-var-root #'server
                    (fn [_] (nrepl/start-server :port port :bind "127.0.0.1")))))

(defn stop-server-now []
  (locking #'server
    (safe-stop-server server)
    (alter-var-root #'server (constantly nil))))
