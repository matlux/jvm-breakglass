(ns cl-java-introspector.test-app
  (:require [clojure.test :refer :all]
            [cl-java-introspector.core :refer :all]
            [cl-java-introspector.client-example :refer [remote-execute]])
  (:import [net.matlux NreplServerSpring]
           [net.matlux NreplServer]
           [net.matlux.testobjects Employee]
           [net.matlux.testobjects Address]
           [net.matlux.fixtures Fixtures]
           ))


(def test1 (str "(import '(net.matlux NreplServerSpring))
   (import '(net.matlux NreplServer))
   (use 'cl-java-introspector.spring)
   (use 'cl-java-introspector.core)
   (import 'net.matlux.testobjects.Address)
   (use 'clojure.reflect 'clojure.pprint 'clojure.java.javadoc)
   (use 'me.raynes.fs)
   (get-beans)
   (get-objs)
   (def employee1 (get-obj \"employee1\"))
   (.getFirstname employee1)
   (->> employee1 .getAddress .getCity)
   (.setAddress employee1 (proxy [Address] [\"1 Madison Square\",\"SW2\",\"NY\"] (getStreet [] \"1 Madison Square\") (getCity [] \"" Fixtures/CITY1 "\")))
   (->> employee1 .getAddress .getCity)"))

(def fixture-test1-result (list 'net.matlux.NreplServerSpring 'net.matlux.NreplServer nil nil 'net.matlux.testobjects.Address nil nil ["employee1" "a_test_obj"] '(var user/employee1) Fixtures/EMPLOYEE_FNAME1 Fixtures/STREET1  nil Fixtures/CITY1))
(def fixture-test1-result-dropped (list ["employee1" "a_test_obj"] '(var user/employee1) Fixtures/EMPLOYEE_FNAME1 Fixtures/STREET1  nil Fixtures/CITY1))

(def test2 (str "(import '(net.matlux NreplServerSpring))
   (import '(net.matlux NreplServer))
   (use 'cl-java-introspector.spring)
   (use 'cl-java-introspector.core)
   (import 'net.matlux.testobjects.Address)
   (use 'clojure.reflect 'clojure.pprint 'clojure.java.javadoc)
   (use 'me.raynes.fs)
   (get-beans)
   (get-objs)
   (def employees (->> (get-obj \"department\") .getEmployees (into []) (map #(vector (keyword (.getFirstname %)) %)) (into {})))
   (->> (:Mick employees) .getAddress .getCity)
   (def new-addr (proxy [Address] [\"1 Madison Square\",\"SW2\",\"NY\"] (getStreet [] \"1 Madison Square\") (getCity [] \"NY\")))
   (.getCity new-addr)
   (.setAddress (:Mick employees) new-addr)
   (->> (:Mick employees) .getAddress .getCity)"))

(def fixture-test2-result (vector 'net.matlux.NreplServerSpring 'net.matlux.NreplServer nil nil 'net.matlux.testobjects.Address nil nil ["address1" "address2" "address3" "employee1" "employee2" "employee3" "department" "repl"] [] '(var user/employees) "1 Madison Square" '(var user/new-addr) "NY" nil "NY"))
(def fixture-test2-result-7dropped (vector ["address1" "address2" "address3" "employee1" "employee2" "employee3" "department" "repl"] [] '(var user/employees) "1 Madison Square" '(var user/new-addr) "NY" nil "NY"))


(defn run-remote-test-code1 [port code]
  (let [server (NreplServer. port)
                 _ (.put server "a_test_obj" "this is a test String.")
                 original-emp (Employee. Fixtures/EMPLOYEE_FNAME1 Fixtures/EMPLOYEE_LNAME1 (Address. Fixtures/STREET1 Fixtures/ZIPCODE1 Fixtures/CITY1))
                 _ (.put server "employee1" original-emp)
                 res (remote-execute "localhost" 1114 code)]
             (.stop server)
             (.unregisterMBean server)
             (drop 7 res)))

(deftest test-app-with-NreplServer
  (testing "test"
    (is (= (run-remote-test-code1 1114 test1)
           fixture-test1-result-dropped))))

;;(deftest test-app-with-NreplServerSpring
;;  (testing "test"
;;    (is (= (let [server (NreplServer. 1113)
;;                 _ (.put server "a_test_obj" "this is a test String.")
;;                 original-emp (Employee. Fixtures/EMPLOYEE_FNAME1 Fixtures/EMPLOYEE_LNAME1 (Address. Fixtures/STREET1 Fixtures/ZIPCODE1 Fixtures/CITY1))
;;                 _ (.put server "employee1" original-emp)
;;                 res (remote-execute "localhost" 1113 test1)]
;;             (.stop server)
;;             (.unregisterMBean server)
;;             (drop 7 res))
;;           (list ["employee1" "a_test_obj"] '(var user/employee1) Fixtures/EMPLOYEE_FNAME1 Fixtures/STREET1  nil Fixtures/CITY1)))))
