(ns net.matlux.server.nrepl)

(use '[clojure.tools.nrepl.server :only (start-server stop-server)])

(defn start-server-now [port]
  (defonce server (start-server :port port)))