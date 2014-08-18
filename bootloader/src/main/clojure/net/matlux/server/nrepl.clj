(ns net.matlux.server.nrepl)

(use '[clojure.tools.nrepl.server :only (start-server stop-server)])

(def server (atom nil))

(defn safe-stop-server [server]
	(when (not (nil? server))
		(stop-server server)))

(defn start-server-now [port]
  (swap! server (fn [old-server]
									(safe-stop-server old-server)
									(start-server :port port))))

(defn stop-server-now []
	(swap! server (fn [old-server]
									(safe-stop-server old-server)
									nil)))