(ns net.matlux.server.nrepl)

(use '[clojure.tools.nrepl.server :only (start-server stop-server)])

(def server nil)

(defn safe-stop-server [server]
	(when (not (nil? server))
		(stop-server server)))

(defn start-server-now [port]
  (alter-var-root (var server) (fn [old-server]
									(safe-stop-server old-server)
									(start-server :port port))))

(defn stop-server-now []
	(alter-var-root (var server) (fn [old-server]
									(safe-stop-server old-server)
									nil)))