(ns cl-java-introspector.examples)

(comment
  ;preparation
  (import '(net.matlux NreplServerSpring)) 
  (import '(net.matlux NreplServer))

  (use 'cl-java-introspector.spring)
  (use 'cl-java-introspector.core)
  (import 'net.matlux.testobjects.Address)
  (use 'clojure.reflect 'clojure.pprint)
  (use 'me.raynes.fs)

  ;;intro
  (System/getProperties)
  (list-dir ".")
  *cwd*
  
  ;do following twice:
  ;list beans
  (get-objs)  ; standard java example
  (get-beans) ; spring example
  ;retrieve a bean or an object
  (get-bean "department") ; spring example
  (get-obj "department")  ; standard java example
  ;what methods or fields has the obj?
  (methods-info  (get-bean "department"))
  (fields-info  (get-bean "department"))
  
  ; can we see inside private members?
  (to-tree (get-bean "department"))
  (obj2map (get-bean "department") 5)
  
  ; what is the bug?
  (->> (get-obj "department") .getEmployees (into []) (map #(->> (.getAddress %) .getCity)) )
  ; get hold of the two employees
  (->> (get-obj "department") .getEmployees (into []) (map #(vector (keyword (.getFirstname %)) %)) (into {}))
  (def employees (->> (get-obj "department") .getEmployees (into []) (map #(vector (keyword (.getFirstname %)) %)) (into {})))
  (:Mick employees)
  
  ;creation of new obj instance and overwrite class definition on the fly
  (.getCity (proxy [net.matlux.testobjects.Address] ["1 Mayfair","SW1","London"] (getStreet [] "53 Victoria Str") (getCity [] "London")))
  (.getCity (proxy [net.matlux.testobjects.Address] ["1 Madison Square","SW2","NY"] (getStreet [] "1 Madison Square") (getCity [] "NY")))
  
  ;; fixing bug 
  (.setAddress (:Mick employees) (proxy [net.matlux.testobjects.Address] ["1 Madison Square","SW2","NY"] (getStreet [] "1 Madison Square") (getCity [] "NY")))
  (.setAddress (:Bob employees) (proxy [net.matlux.testobjects.Address] ["1 Mayfair","SW1","London"] (getStreet [] "53 Victoria Str") (getCity [] "London")))

  ;; verify fix
  (->> (:Mick employees) .getAddress .getCity)
  
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
  
  ;;demonstrate how we can search through ns to find a function of a lib like "fs"
  (->> (ns-map *ns*) (filter #(re-find #"fs" (.toString (val %)))) (map key))
  
  ;;demonstrate a shell like pipping with aiming for this:
  (->> (find-files "." #".*") (map absolute-path) (filter #(and (re-find #"target" %) (file? %))) pprint)
  

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