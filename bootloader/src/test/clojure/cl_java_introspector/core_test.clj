(ns cl-java-introspector.core-test
  (:require [clojure.test :refer :all]
            [cl-java-introspector.core :as inspect]
            [cl-java-introspector.client-example :as client]
            [net.matlux.server.nrepl :as server])
  (:import [java.util ArrayList HashMap]
           [net.matlux NreplServer]
           [net.matlux.testobjects Address Employee]))

(deftest scalar-and-collection-introspection
  (is (nil? (inspect/to-tree nil)))
  (is (= ["text" 42 :key] (inspect/to-tree ["text" 42 :key])))
  (is (= {:value [1 2]} (inspect/to-tree (doto (HashMap.)
                                        (.put "value" (ArrayList. [1 2])))))))

(deftest nested-private-field-introspection
  (let [address (Address. "Street" "Zip" "City")
        employee (Employee. "First" "Last" address)]
    (is (= {:firstname "First" :lastname "Last"
            :address {:street "Street" :zipcode "Zip" :city "City"}}
           (inspect/to-tree employee)))
    (is (= #{:street :zipcode :city} (set (keys (into {} (inspect/get-fields address))))))
    (is (some #{"getCity"} (inspect/get-method-names address)))
    (is (= {"street" "Street" "zipcode" "Zip" "city" "City"}
           (inspect/obj2map address 1)))
    (is (identical? address (inspect/obj2map address 0)))))

(deftest registered-object-lookup
  (let [server (NreplServer. 0 false false true false)
        value (Object.)]
    (.put server "value" value)
    (is (= #{"value"} (set (inspect/get-objs))))
    (is (identical? value (inspect/get-obj "value")))
    (is (nil? (inspect/get-obj "missing")))))

(deftest remote-values-are-realized-before-closing-transport
  (let [closed? (atom false)
        transport (reify java.io.Closeable (close [_] (reset! closed? true)))]
    (with-redefs [clojure.tools.nrepl/connect (fn [& _] transport)
                  clojure.tools.nrepl/client (fn [& _] :client)
                  clojure.tools.nrepl/message (fn [& _] :responses)
                  clojure.tools.nrepl/response-values
                  (fn [_] (lazy-seq
                            (when @closed? (throw (IllegalStateException. "closed transport")))
                            (list 42)))]
      (let [result (client/remote-execute "localhost" 0 "(+ 40 2)")]
        (is @closed?)
        (is (= '(42) result))))))

(deftest transport-closes-when-decoding-fails
  (let [closed? (atom false)
        transport (reify java.io.Closeable (close [_] (reset! closed? true)))]
    (with-redefs [clojure.tools.nrepl/connect (fn [& _] transport)
                  clojure.tools.nrepl/client (fn [& _] :client)
                  clojure.tools.nrepl/message (fn [& _] :responses)
                  clojure.tools.nrepl/response-values
                  (fn [_] (lazy-seq (throw (IllegalStateException. "bad response"))))]
      (is (thrown? IllegalStateException (client/remote-execute "localhost" 0 "bad")))
      (is @closed?))))

(deftest transient-bind-failure-allows-native-socket-release
  (let [attempts (atom 0)]
    (with-redefs [clojure.tools.nrepl.server/start-server
                  (fn [& _]
                    (if (< (swap! attempts inc) 3)
                      (throw (java.net.BindException. "still closing"))
                      {:port 12345}))
                  clojure.tools.nrepl.server/stop-server (fn [_])]
      (try
        (is (= {:port 12345} (server/start-server-now 12345)))
        (is (= 3 @attempts))
        (finally (server/stop-server-now))))))
