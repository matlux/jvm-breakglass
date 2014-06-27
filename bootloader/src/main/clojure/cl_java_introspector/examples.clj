(ns cl-java-introspector.examples)

(comment
  ;;preparation
  (do
   (import '(net.matlux NreplServerSpring))
   (import '(net.matlux NreplServer))

   (use 'cl-java-introspector.spring)
   (use 'cl-java-introspector.core)
   (import 'net.matlux.testobjects.Address)
   (use 'clojure.reflect 'clojure.pprint 'clojure.java.javadoc)
   (use 'me.raynes.fs))

  ;;intro
  (System/getProperties)
  (list-dir ".")
  *cwd*
    ;;demonstrate how we can search through ns to find a function of a lib like "fs"
  (->> (ns-map *ns*) (filter #(re-find #"fs" (.toString (val %)))) (map key))


  ;;demonstrate a shell like pipping with aiming for this:
  (find-files ".." #".*")
  (->> (find-files ".." #".*") (map absolute-path) (filter #(and (re-find #"conf" %) (directory? %))))

  ;do following twice:
  ;list beans
  (get-objs)  ; standard java example
  (get-beans) ; spring example
  ;retrieve a bean or an object
  (get-bean "reportController") ; spring example
  (get-bean "department") ; spring example  ( 8 mins)
  (get-obj "department")  ; standard java example

  ; can we see inside private members?
  (bean (get-bean "department"))
  (to-tree (get-bean "department"))
  (obj2map (get-bean "department") 5)


  ;;what methods or fields has the obj?
  (methods-info  (get-bean "department"))
  (fields-info  (get-bean "department"))


  ; what is the bug?
  (->> (get-obj "department") .getEmployees)
  ; get hold of the two employees
  (->> (get-obj "department") .getEmployees (map #(vector (keyword (.getFirstname %)) %)) (into {}))
  (->> (get-obj "department") .getEmployees (group-by #(keyword (.getFirstname %))))
  (def employees (->> (get-obj "department") .getEmployees (into []) (map #(vector (keyword (.getFirstname %)) %)) (into {})))

  (:Mick employees)
  (methods-info (:Mick employees))
  (->> (:Mick employees) .getAddress)
  (->> (:Mick employees) .getAddress methods-info)
  ;; here it is:
  (->> (:Mick employees) .getAddress .getCity)
  (->> (:Mick employees) .getAddress .getStreet)


  ;creation of new obj instance and overwrite class definition on the fly
  (proxy [Address] ["1 Mayfair","SW1","London"])
  (.getCity (proxy [Address] ["1 Mayfair","SW1","London"]))
  (def new-addr (proxy [Address] ["1 Mayfair","SW1","London"] (getStreet [] "53 Victoria Str") (getCity [] "London")))
  (def new-addr (proxy [Address] ["1 Madison Square","SW2","NY"] (getStreet [] "1 Madison Square") (getCity [] "NY")))
  (.getCity new-addr)

  ;; fixing bug
  (.setAddress (:Mick employees) new-addr)





  (.setAddress (:Bob employees) (proxy [Address] ["1 Mayfair","SW1","London"] (getStreet [] "53 Victoria Str") (getCity [] "London")))

  ;; verify fix
  (->> (:Mick employees) .getAddress .getCity)

  (net.matlux.testobjects.Employee. "John" "Smith" (proxy [net.matlux.testobjects.Address] ["53 Victoria Str" "SE1 0LK" "London"] (boo [other] false)))


  ;how about using clojure as a remote shell and listing some files?
  (list-dir ".")
  *cwd*
  (hidden? ".")
  (hidden? "pom.xml")
  (hidden? ".classpath")
  (directory? ".classpath")
  (directory? ".")
  (filter #(directory? (str "../" %)) (list-dir ".."))

  ;;demonstrate how we can search through ns to find a function of a lib like "fs"
  (->> (ns-map *ns*) (filter #(re-find #"fs" (.toString (val %)))) (map key))
  (->> (ns-map *ns*) (filter #(re-find #"javadoc" (.toString (val %)))) (map key))


  (println (remote-execute "localhost" 1112 code2execute2))


;(get-obj-methods "")
;(->> NreplServerStartup/instance get-member-fields first second get-member-fields)
;(->> NreplServerStartup/instance get-member-fields first second to-tree )
;(->> NreplServerStartup/instance get-member-fields first second to-tree :department get-obj-methods first bean)
;(->> (to-tree NreplServerStartup/instance) :objMap :department :employees second :lastname)

  (->> (get-obj "department") .getEmployees (map #(.getAddress %)) )
  (->> (get-obj "department") .getEmployees (map #(.getAddress %)) first)
  (->> (get-obj "department") .getEmployees (map #(.getAddress %)) first methods-info)
  (->> (get-obj "department") .getEmployees (map #(->> (.getAddress %) .getCity)) )


)
