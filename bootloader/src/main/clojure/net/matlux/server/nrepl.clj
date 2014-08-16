(ns net.matlux.server.nrepl)

(use '[clojure.tools.nrepl.server :only (start-server stop-server)])

(def server (atom {}))

(defn start-server-now [port]
  (reset! server (start-server :port port)))

(defn stop-server-now []
	(stop-server @server))