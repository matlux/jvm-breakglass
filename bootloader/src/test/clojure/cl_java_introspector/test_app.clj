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

(deftest test-app-with-NreplServer
  (testing "test"
    (is (= (let [server (NreplServer. 1113)
                 original-emp (Employee. Fixtures/EMPLOYEE_FNAME1 Fixtures/EMPLOYEE_LNAME1 (Address. Fixtures/STREET1 Fixtures/ZIPCODE1 Fixtures/CITY1))
                 _ (.put server "employee1" original-emp)
                 res (remote-execute "localhost" 1113 test1)]
             (.stop server)
             (drop 7 res))
           (list ["employee1" "a_test_obj"] '(var user/employee1) Fixtures/EMPLOYEE_FNAME1 Fixtures/STREET1  nil Fixtures/CITY1)))))

(deftest test-app-with-NreplServerSpring
  (testing "test"
    (is (= (let [server (NreplServer. 1114)
                 original-emp (Employee. Fixtures/EMPLOYEE_FNAME1 Fixtures/EMPLOYEE_LNAME1 (Address. Fixtures/STREET1 Fixtures/ZIPCODE1 Fixtures/CITY1))
                 _ (.put server "employee1" original-emp)
                 res (remote-execute "localhost" 1114 test1)]
             (.stop server)
             (drop 7 res))
           (list ["employee1" "a_test_obj"] '(var user/employee1) Fixtures/EMPLOYEE_FNAME1 Fixtures/STREET1  nil Fixtures/CITY1)))))
