(ns cl-java-introspector.examples)

(comment
  ;preparation
  (import '(net.matlux NreplServerStartup))
  (import '(net.matlux NreplServerWithSpringLog4jStartup))
  (use 'clojure.reflect 'clojure.pprint)
  (use 'me.raynes.fs)
  (use 'cl-java-introspector.core)

  ;do following twice:
  ;list beans
  (get-beans)
  ;find a bean or an object
  (get-bean "department")
  ;what methods or fields has the obj?
  (methods-info  (get-bean "employee1"))
  (fields-info  (get-bean "employee1"))
  
  ; can we see inside private members?
  (to-tree (get-bean "department"))
  (obj2map (get-bean "department") 5)
  
  ;creation of new obj instance and overwrite class definition on the fly
  (.getCity (proxy [net.matlux.testobjects.Address] ["53 Victoria Str" "SE1 0LK" "London"] (getStreet [] "53 Victoria Str")))
  (net.matlux.testobjects.Employee. "John" "Smith" (proxy [net.matlux.testobjects.Address] ["53 Victoria Str" "SE1 0LK" "London"] (boo [other] false)))
  (pprint (reflect (.getObj NreplServerWithSpringLog4jStartup/instance "department")))
  ;;(print-table (sort-by :name (:members (reflect net.matlux.testobjects.Employee))))
  ;;(print-table (sort-by :name (filter :exception-types (:members (reflect net.matlux.testobjects.Employee)))))
  ;;(print-table (sort-by :name (filter :exception-types (:members (reflect (.getApplicationContext NreplServerWithSpringLog4jStartup/instance))))))
  ;;(print-table (sort-by :name (filter :exception-types (:members (reflect org.springframework.context.support.ClassPathXmlApplicationContext)))))
  
  ;now same with more details
  ;;decompose the following:
  (into [] (.getBeanDefinitionNames (.getApplicationContext NreplServerWithSpringLog4jStartup/instance))) 
  ;;=>(get-beans) or show code
  
  ;;decompose the following:
  (.getBean (.getApplicationContext NreplServerWithSpringLog4jStartup/instance) "employee1")
  ;;(get-bean "department")
  
  ;how about using clojure as a remote shell and listing some files?
  (list-dir ".")
  *cwd*
  (hidden? ".")
  (hidden? "pom.xml")
  (hidden? ".classpath")
  (directory? ".classpath")
  (directory? ".")
  (filter directory? (list-dir "."))

  (into [] (.getBeanDefinitionNames (.getApplicationContext NreplServerWithSpringLog4jStartup/instance)))  
  
  (pprint (reflect emp2))
  
  
  (obj2map nil)
  (obj2map department)
  (obj2map NreplServerStartup/instance)
  
  
  (println (remote-execute "localhost" 1112 code2execute2))
  
  
;(get-obj-methods "")
;(->> NreplServerStartup/instance get-member-fields first second get-member-fields)
;(->> NreplServerStartup/instance get-member-fields first second to-tree )
;(->> NreplServerStartup/instance get-member-fields first second to-tree :department get-obj-methods first bean)
;(->> (to-tree NreplServerStartup/instance) :objMap :department :employees second :lastname)

  

)