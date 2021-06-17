(ns net.matlux.server.nrepl
  (:require [nrepl.server :refer [start-server stop-server]]))

(def server nil)

(defn safe-stop-server [server]
  (when (not (nil? server))
    (stop-server server)))

(defn start-server-now
  "Start nrepl server with port or host and port.
   If host is not supplied, localhost is used instead.
   Stops the old server before starting the new one"
  ([port]
   (start-server-now "127.0.0.1" port))
  ([host port]
   (alter-var-root (var server) (fn [old-server]
                                  (safe-stop-server old-server)
                                  (start-server :bind host :port port)))))

(defn stop-server-now []
  (alter-var-root (var server) (fn [old-server]
                                 (safe-stop-server old-server)
                                 nil)))