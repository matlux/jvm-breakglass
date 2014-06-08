nRepl hook for Java
===================


Background on nRepl
-------------------


Setting up an [nrepl](https://github.com/clojure/tools.nrepl) can be useful to introspect into the JVM for troubleshouting/investigation or testing of regular Java applications. You can connect onto a process and use a Clojure prompt interactivelly or have client application that sends and execute Java code dynamically. It works because the code injected is Clojure and that the Clojure run-time allows to evaluate code at run-time. Furthermore Clojure interops very easily with Java i.e. you can translate pretty much any Java code into Clojure and access all your Java object from the injected Clojure code. This is the perfect tool to access the inside of your JVM process live after it has been deployed. To run any fancy change of code scenario, any data structure or call any method you don't need to redeploy your java code. You can see what your process sees in real time. This is an unvaluable tool to use to develop and maintain a java application.

What is this project about?
---------------------------

Clojure and REPL can be introduced into a pure Java project to improve troubleshooting without having to force a team to migrate their code away from Java.

nRepl is easy to start in Clojure but it needs a tiny bit of work to inject it into your java process. If your using Spring and Java, this project has done that for you. This project is a Maven module that you can integrate in your java project and inject easily the Repl in your application.

Watch this [video](http://skillsmatter.com/podcast/home/the-repl-an-innovative-way-to-troubleshoot-javajvm-processes) to get an introduction.

How to install the REPL in your application with Spring
-------------------------------

* insert the dependency inside your maven project

```xml
<dependency>
  <groupId>net.matlux</groupId>
  <artifactId>jvm-breakglass</artifactId>
  <version>0.0.5</version>
</dependency>
```

* add the following bean to your Spring config

```xml
<bean id="repl" class="net.matlux.NreplServerSpring">
  <constructor-arg index="0" value="1112" />
</bean>
```

1112 is the port number.

What if I don't use Spring?
---------------------------

No problems, just instanciate the following class in your application rather than using the xml spring context:
```java
    import net.matlux.NreplServer;
    new NreplServer(port) //start server listening onto port number
    .put("department",myObject);
```

Repeat the call to put with as many object as you want to register on the repl. The NreplServer instance is a Map onto which you can add Object instances that you can retreive later on under the repl access.


Quick demonstration of this project
-----------------------------------

* clone this repo and compile the example

```sh
    git clone https://github.com/matlux/jvm-breakglass.git
    cd jvm-breakglass/examples/server
```

* Compile this project

```sh
    mvn clean install
```

* Start the example of a server with the NreplServer

```sh
    ./startSpringServer.sh
```

* Start the repl client which introspects into the Java server process

```sh
    lein repl :connect localhost:1112
```

* Copy and past the following commands

```clojure
  (use 'cl-java-introspector.spring)
  (use 'cl-java-introspector.core)
  (use 'me.raynes.fs)

```

* Type one of the following Commands

```clojure
  ;list beans
  (get-beans)

  ;find a bean or an object
  (get-bean "department")

  ;what methods or fields has the obj?
  (methods-info  (get-bean "departement"))

```

* what next?

See more [examples](https://github.com/matlux/jvm-breakglass/blob/master/bootloader/src/main/clojure/cl_java_introspector/examples.clj).

Quick demonstration of a standard Java Server example
-----------------------------------------------------

* clone this repo and compile the example

```sh
    git clone https://github.com/matlux/jvm-breakglass.git
    cd jvm-breakglass/examples/server-no-spring
```

* Compile this project

```sh
    mvn clean install
```

* Start the example of a server with the NreplServer

```sh
    ./startServer.sh
```

* Start the repl client which introspects into the Java server process

```sh
    lein repl :connect localhost:1112
```

* Copy and past the following commands

```clojure
  (use 'cl-java-introspector.core)
  (use 'me.raynes.fs)

```

* Type one of the following Commands

```clojure
  ;list objs
  (get-objs)

  ;find a bean or an object
  (get-obj "department")

  ;what methods or fields has the obj?
  (methods-info  (get-obj "departement"))

```

* what next?

See section below with more use cases.
or
See more [examples](https://github.com/matlux/jvm-breakglass/blob/master/bootloader/src/main/clojure/cl_java_introspector/examples.clj).


# There are two type of client to access the nRepl server

## Via the repl client with lein

```sh
    lein repl :connect [host:port]
```

for example:

```sh
    lein repl :connect localhost:1112
```

## programatically

```clojure
    (require '[clojure.tools.nrepl :as repl])
    (with-open [conn (repl/connect :port 1112)]
     (-> (repl/client conn 1000)
       (repl/message {:op :eval :code "(+ 1 1)"})
       repl/response-values))
```

The above sends an expression "(+ 1 1)" to be evaluated remotely. See [nrepl](https://github.com/clojure/tools.nrepl) website for more details.

Also see quick demo above.

# Once you have the nRepl running inside your process. What can you do?

You need to connect onto it with the lein command above and the set of imports (also above). Now you can type any of the following commands.


## retrieve the list of System properties from the java process

```clojure
    (filter #(re-matches #"sun.*" (key %)) (into {} (System/getProperties)))
```

This example filters on a regex. It retrieves property keys which start with "sun"

## list bean or objects

```Clojure
  (get-beans) ; spring example
  (get-objs)  ; standard java example
```

## retrieve a bean or an object by name

```clojure
  (get-bean "department")  ; spring example
  (get-obj "department")   ; standard java example
```

keep the object reference
```clojure
  (def myobj (get-bean "department")) ; spring example
  ;;or
  (def myobj (get-obj "department")) ; standard java example
```

## what methods or fields has the obj?

```clojure
  (methods-info  myobj)
  (fields-info  myobj)
```

## show the content of the fields the obj

```clojure
  (to-tree  myobj)
```

## Terminate the process ;)

```clojure
    (System/exit 0)
```

### Retrieve the Spring application context

```clojure
    (import '(net.matlux NreplServerSpring))


    (. NreplServerSpring/instance getApplicationContext)

    ;; for example list all the bean names
    (. (. NreplServerSpring/instance getApplicationContext) getBeanDefinitionNames)
```


## Coherence example: Retrieve the number of object in a Cache

Your application needs to have a dependency on Oracle Coherence, The binary and dependency is not provided here, this is just an example.

```clojure
    (def all-filter (new AlwaysFilter))
    (def nodeCache (Caches/getCache "cachename")
    (let [all-filter (new AlwaysFilter)
      nodeCache (Caches/getCache "cachename")]
    (. (. nodeCache entrySet all-filter) size))
```


## Introspect into a Java bean (not a Spring one this time...)

```clojure
    (bean obj)
```

## Introspect into a Java Object

```clojure
    (to-tree myObject)
```

For example:

```java
        Department department = new Department("The Art Department",0L);
        department.add(new Employee("Bob","Dilan",new Address("1 Mayfair","SW1","London")));
        department.add(new Employee("Mick","Jagger",new Address("1 Time Square",null,"NY")));
        objMap.put("department", department);
        Set<Object> myFriends = new HashSet<Object>();
        myFriends.add(new Employee("Keith","Richard",new Address("2 Mayfair","SW1","London")));
        myFriends.add(new Employee("Nina","Simone",new Address("1 Gerards Street","12300","Smallville")));
        objMap.put("myFriends", myFriends);
        objMap.put("nullValue", null);
```

becomes

```clojure
[{objMap {myFriends [{address {city Smallville, zipcode 12300, street 1 Gerards Street}, lastname Simone, firstname Nina} {address {city London, zipcode SW1, street 2 Mayfair}, lastname Richard, firstname Keith}], nullValue nil, department {id 0, name The Art Department, employees [{address {city London, zipcode SW1, street 1 Mayfair}, lastname Dilan, firstname Bob} {address {city NY, zipcode nil, street 1 Time Square}, lastname Jagger, firstname Mick}]}}} nil]
```

See [cl-java-introspector.core](https://github.com/matlux/cl-repl-server-bootloader/blob/master/bootloader/src/main/clojure/cl_java_introspector/core.clj) for details of the implementation.



## License

Copyright (C) 2014 Mathieu Gauthron

Distributed under the Eclipse Public License, the same as Clojure.
