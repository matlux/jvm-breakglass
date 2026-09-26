# Historical examples and REPL recipes

These demonstrations use the independent projects under `examples/`, whose
dependency versions and launch scripts predate 0.1.0. They are preserved for
reference and have not been verified against the new release. Building them
does not test the core library in this checkout. For current integration
instructions, see the [project README](../README.md).

## Spring application demonstration

## Pre-requisites:

* You must have [leinengen](https://github.com/technomancy/leiningen)  installed. Otherwise follow the installation process on this [site](https://github.com/technomancy/leiningen).
* You must have [Maven](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) installed. Follow [this](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) otherwise.

## Tutorial

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

You should now see the following message repeating itself on the screen:

    Retrieve Employees from NY or London:
    Retrieve Employees from NY or London:
    ...

Notes: Ctrl-c does not seem to kill the process for some reason. Please find it's PID and kill it with `kill -9 [pid of the process]` from a different window when you've finished the demonstration.

* Leave the above server running and open a different shell window before you continue

Notes: You don't need to be inside the current directory of any particular project. Actually it's best to be outside of any project so you don't pull any specific project dependencies and introduce an uncertainty. If in doubt, type `cd /tmp` to make sure you're outside of any specific Lein project.

* Start the repl client which introspects into the Java server process

```sh
    lein repl :connect localhost:1112
```

* Copy and past the following commands

```clojure
  (require '[cl-java-introspector.spring :as spring])
  (require '[cl-java-introspector.core :as inspect])
  (require '[me.raynes.fs :as fs])

```

* Type one of the following Commands

```clojure
  ;list beans
  (spring/get-beans)

  ;find a bean or an object
  (spring/get-bean "department")

  ;what methods or fields has the obj?
  (inspect/methods-info  (spring/get-bean "department"))

```

* what next?

See more [examples](https://github.com/matlux/jvm-breakglass/blob/master/bootloader/src/main/clojure/cl_java_introspector/examples.clj).

## Standard Java application demonstration

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
  (require '[cl-java-introspector.core :as inspect])
  (require '[me.raynes.fs :as fs])

```

* Type one of the following Commands

```clojure
  ;list objs
  (inspect/get-objs)

  ;find a bean or an object
  (inspect/get-obj "department")

  ;what methods or fields has the obj?
  (inspect/methods-info  (inspect/get-obj "department"))

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
  (spring/get-beans) ; spring example
  (inspect/get-objs)  ; standard java example
```

## retrieve a bean or an object by name

```clojure
  (spring/get-bean "department")  ; spring example
  (inspect/get-obj "department")   ; standard java example
```

keep the object reference
```clojure
  (def myobj (spring/get-bean "department")) ; spring example
  ;;or
  (def myobj (inspect/get-obj "department")) ; standard java example
```

## what methods or fields has the obj?

```clojure
  (inspect/methods-info  myobj)
  (inspect/fields-info  myobj)
```

## show the content of the fields the obj

```clojure
  (inspect/to-tree  myobj)
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
    (inspect/to-tree myObject)
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

See [cl-java-introspector.core](https://github.com/matlux/jvm-breakglass/blob/master/bootloader/src/main/clojure/cl_java_introspector/core.clj) for details of the implementation.
