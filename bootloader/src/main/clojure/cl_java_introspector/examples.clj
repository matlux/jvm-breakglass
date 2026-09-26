(ns cl-java-introspector.examples)

(comment
  ;;preparation
  (do
   (import '(net.matlux NreplServerSpring))
   (import '(net.matlux NreplServer))

   (require '[cl-java-introspector.spring :as spring])
   (require '[cl-java-introspector.core :as inspect])
   (import 'net.matlux.testobjects.Address)
   (require '[clojure.reflect :as reflect]
            '[clojure.pprint :as pprint]
            '[clojure.java.javadoc :as javadoc])
   (require '[me.raynes.fs :as fs]))

  ;;intro
  (System/getProperties)
  (fs/list-dir ".")
  fs/*cwd*
    ;;demonstrate how we can search through ns to find a function of a lib like "fs"
  (->> (ns-map *ns*) (filter #(re-find #"fs" (.toString (val %)))) (map key))


  ;;demonstrate a shell like pipping with aiming for this:
  (fs/find-files ".." #".*")
  (->> (fs/find-files ".." #".*") (map fs/absolute-path) (filter #(and (re-find #"conf" %) (fs/directory? %))))

  ;do following twice:
  ;list beans
  (inspect/get-objs)  ; standard java example
  (spring/get-beans) ; spring example
  ;retrieve a bean or an object
  (spring/get-bean "reportController") ; spring example
  (spring/get-bean "department") ; spring example  ( 8 mins)
  (inspect/get-obj "department")  ; standard java example

  ; can we see inside private members?
  (bean (spring/get-bean "department"))
  (inspect/to-tree (spring/get-bean "department"))
  (inspect/obj2map (spring/get-bean "department") 5)


  ;;what methods or fields has the obj?
  (inspect/methods-info  (spring/get-bean "department"))
  (inspect/fields-info  (spring/get-bean "department"))


  ; what is the bug?
  (->> (inspect/get-obj "department") .getEmployees)
  ; get hold of the two employees
  (->> (inspect/get-obj "department") .getEmployees (map #(vector (keyword (.getFirstname %)) %)) (into {}))
  (->> (inspect/get-obj "department") .getEmployees (group-by #(keyword (.getFirstname %))))
  (def employees (->> (inspect/get-obj "department") .getEmployees (into []) (map #(vector (keyword (.getFirstname %)) %)) (into {})))

  (:Mick employees)
  (inspect/methods-info (:Mick employees))
  (->> (:Mick employees) .getAddress)
  (->> (:Mick employees) .getAddress inspect/methods-info)
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
  (fs/list-dir ".")
  fs/*cwd*
  (fs/hidden? ".")
  (fs/hidden? "pom.xml")
  (fs/hidden? ".classpath")
  (fs/directory? ".classpath")
  (fs/directory? ".")
  (filter #(fs/directory? (str "../" %)) (fs/list-dir ".."))

  ;;demonstrate how we can search through ns to find a function of a lib like "fs"
  (->> (ns-map *ns*) (filter #(re-find #"fs" (.toString (val %)))) (map key))
  (->> (ns-map *ns*) (filter #(re-find #"javadoc" (.toString (val %)))) (map key))


  (println (remote-execute "localhost" 1112 code2execute2))


;(get-obj-methods "")
;(->> NreplServerStartup/instance get-member-fields first second get-member-fields)
;(->> NreplServerStartup/instance get-member-fields first second inspect/to-tree )
;(->> NreplServerStartup/instance get-member-fields first second inspect/to-tree :department get-obj-methods first bean)
;(->> (inspect/to-tree NreplServerStartup/instance) :objMap :department :employees second :lastname)

  (->> (inspect/get-obj "department") .getEmployees (map #(.getAddress %)) )
  (->> (inspect/get-obj "department") .getEmployees (map #(.getAddress %)) first)
  (->> (inspect/get-obj "department") .getEmployees (map #(.getAddress %)) first inspect/methods-info)
  (->> (inspect/get-obj "department") .getEmployees (map #(->> (.getAddress %) .getCity)) )


)
