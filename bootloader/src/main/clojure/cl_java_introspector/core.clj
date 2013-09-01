(ns cl-java-introspector.core)

(require '[clojure.walk :only [walk prewalk postwalk]])
(use 'clojure.reflect 'clojure.pprint)

(import '(net.matlux NreplServer))
(import '(java.lang.reflect Modifier))




(defn get-objs []
    (into [] (.keySet NreplServer/instance))
)



(defn get-obj [^String bean-name] 
  (.getObj NreplServer/instance bean-name))


(defn methods-info [obj] 
  (print-table (sort-by :name (filter :exception-types (:members (reflect obj))))))
(defn fields-info [obj] 
  (print-table (sort-by :name (filter :type (:members (reflect obj))))))
(defn members-info [obj] 
  (print-table (sort-by :name (:members (reflect obj)))))

(defn field? [field] (not (Modifier/isStatic (.getModifiers field))))
(defn get-fields [obj] (map #(vector (keyword (.getName %)) (.get % obj)) (filter field? (map #(do (.setAccessible % true) %) (into [] (. (. obj getClass) getDeclaredFields))))))
(def primitive? (some-fn string? number?))
(def clojure-struct? (some-fn map? set? vector? list?))

(defn- objfields-to-map [obj] (reduce #(let [[fname ob] %2] (assoc %1 fname ob )) {} (get-fields obj)))

(defn- to-map [obj2map obj]
  ;(print "walk:") (prn obj)
  (cond
   ((some-fn nil? primitive? clojure-struct? keyword?) obj) obj
   (instance? java.lang.Iterable obj) (into [] obj)
   (instance? java.util.Map obj) (let [m (into {} obj)] (reduce #(assoc %1 (if (string? %2) (keyword %2) %2) (m %2)) {} (keys m)))
   :else (obj2map obj)
   ))

(def to-tree (partial clojure.walk/prewalk (partial to-map objfields-to-map)))

(defn get-obj-methods [obj]
  (let [obj2methods (fn [obj] (map #(do (.setAccessible % true) %) (into [] (. (. obj getClass) getDeclaredMethods))))
        get-inst-methods (fn [fields] (filter #(not (Modifier/isStatic (.getModifiers %))) fields))
        method2ref (fn [field obj] (.get field obj))
        ]

    (obj2methods obj)))

(defn get-method-names [obj] (map #(.getName %) (get-obj-methods obj)))

(defn obj2map [obj level]
  (if (zero? level) obj (let [obj2fields (fn [obj] (map #(do (.setAccessible % true) %) (into [] (. (. obj getClass) getDeclaredFields))))
         get-inst-fields (fn [fields] (filter #(not (Modifier/isStatic (.getModifiers %))) fields))
         field2ref (fn [field obj] (.get field obj))
         ]
                                        ;(reduce #(assoc %1 (.getName %2) (field2value %2)) {} in-fields)
     (cond (nil? obj) nil
           (instance? java.lang.String obj) obj
           (instance? java.lang.Number obj) obj
           (instance? java.lang.Iterable obj) (into [] (map (fn [e] (obj2map e (dec level))) (into [] obj)))
           (instance? java.util.Map obj) (let [m (into {} obj)
                                               ks (keys m)
                                               ]
                                           (reduce #(assoc %1 %2 (obj2map (m %2) (dec level))) {} ks))
           :else (reduce #(assoc %1 (.getName %2) (obj2map (field2ref %2 obj) (dec level))) {} (get-inst-fields (obj2fields obj))) )
     )))









