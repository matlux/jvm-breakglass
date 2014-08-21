(ns cl-java-introspector.client-example)

(require '[clojure.tools.nrepl :as repl])


(defn remote-execute [hostname port code]
  (try
    (with-open [conn (repl/connect :host hostname :port port)]
     (-> (repl/client conn 1000)
       (repl/message {:op :eval :code code})
       repl/response-values))
     (catch java.net.ConnectException e
         ;(println "Caught" (.getMessage e))
         "cannot connect")
     (finally
          ;(println "")
          )))



(def code2inject
  "(import '(net.matlux NreplServerStartup))
   (import '(java.lang.reflect Modifier))
   (import '(net.matlux NreplServerWithSpringLog4jStartup))
   (defn obj2map [obj]
   (let [obj2fields (fn [obj] (map #(do (.setAccessible % true) %) (into [] (. (. obj getClass) getDeclaredFields))))
        get-inst-fields (fn [fields] (filter #(not (Modifier/isStatic (.getModifiers %))) fields))
        field2ref (fn [field obj] (.get field obj))
        ]
                                        ;(reduce #(assoc %1 (.getName %2) (field2value %2)) {} in-fields)
    (cond (nil? obj) nil
          (instance? java.lang.String obj) obj
          (instance? java.lang.Number obj) obj
          (instance? java.lang.Iterable obj) (into [] (map (fn [e] (obj2map e)) (into [] obj)))
          (instance? java.util.Map obj) (let [m (into {} obj)
                                              ks (keys m)
                                              ]
                                          (reduce #(assoc %1 %2 (obj2map (m %2))) {} ks))
          :else (reduce #(assoc %1 (.getName %2) (obj2map (field2ref %2 obj))) {} (get-inst-fields (obj2fields obj))) )
    ))")

(def code2execute
  "(obj2map NreplServerStartup/instance)
   (into [] (.getBeanDefinitionNames (.getApplicationContext NreplServerWithSpringLog4jStartup/instance)))
   (obj2map (.getObj NreplServerWithSpringLog4jStartup/instance \"department\"))
   (obj2map (.getObj NreplServerWithSpringLog4jStartup/instance \"employee1\"))
   (obj2map nil)")


(defn -main []

  (println "introspecting objects:")
  (remote-execute "localhost" 1112 code2inject)
  (println (remote-execute "localhost" 1112 code2execute))
  (System/exit 0)
  )

