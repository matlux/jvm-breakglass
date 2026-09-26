(ns net.matlux.server.nrepl
  (:require [clojure.tools.nrepl.server :as nrepl]))

(def server nil)

(defn safe-stop-server [server]
	(when (not (nil? server))
		(nrepl/stop-server server)))

(defn- bind-server [port]
  ;; On Linux, close may return before an in-flight accept releases the native
  ;; socket. Allow that release to finish when rebinding a requested port.
  ;; A genuinely occupied port still fails within one second; other errors and
  ;; interruption propagate immediately.
  (let [deadline (+ (System/nanoTime) 1000000000)]
    (loop []
      (let [result (try
                     (nrepl/start-server :port port :bind "127.0.0.1")
                     (catch java.net.BindException error error))]
        (if (instance? java.net.BindException result)
          (if (and (pos? port) (< (System/nanoTime) deadline))
            (do (Thread/sleep 10) (recur))
            (throw result))
          result)))))

(defn start-server-now [port]
  (locking #'server
    (safe-stop-server server)
    ;; Clear the stopped handle even if binding the replacement port fails.
    (alter-var-root #'server (constantly nil))
    (alter-var-root #'server
                    (fn [_] (bind-server port)))))

(defn stop-server-now []
  (locking #'server
    (safe-stop-server server)
    (alter-var-root #'server (constantly nil))))
