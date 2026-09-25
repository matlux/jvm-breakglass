nRepl hook for Java
===================

[![Build](https://github.com/matlux/jvm-breakglass/actions/workflows/build.yml/badge.svg)](https://github.com/matlux/jvm-breakglass/actions/workflows/build.yml)
[![Clojars Project](http://clojars.org/net.matlux/jvm-breakglass/latest-version.svg)]

Build and upgrade status
------------------------

See [UPGRADE.md](UPGRADE.md) for the dependency audit, issue mapping, and staged
migration plan. The build now requires **JDK 8+ and Maven 3.9+**, and emits Java 8
bytecode. Production dependency versions remain on the legacy baseline while
upgrade compatibility is evaluated.

From the repository root:

```sh
mvn -f bootloader/pom.xml clean verify     # Java/Clojure unit + socket/Spring/JMX integration tests
mvn -f bootloader/pom.xml test             # unit tests only
mvn -f bootloader/pom.xml javadoc:javadoc  # bootloader/target/reports/apidocs
mvn -f bootloader/pom.xml -Dclojure.version=1.12.6 clean verify
```

Clojure tests run through a JUnit bridge so failures fail Maven. Integration tests
use OS-assigned ports, bounded waits, checked worker futures, and cleanup on failure.
The CI matrix covers Java 8, 17, 21 and 25 with Clojure 1.6.0 and 1.12.6. The four historical example applications still need the migrations in
UPGRADE.md and are not part of this build.

Background on nRepl
-------------------


Setting up an [nrepl](https://github.com/clojure/tools.nrepl) can be useful to introspect into the JVM for troubleshouting/investigation or testing of regular Java applications. You can connect onto a process and use a Clojure prompt interactivelly or have client application that sends and execute Java code dynamically. It works because the code injected is Clojure and that the Clojure run-time allows to evaluate code at run-time. Furthermore Clojure interops very easily with Java i.e. you can translate pretty much any Java code into Clojure and access all your Java object from the injected Clojure code. This is the perfect tool to access the inside of your JVM process live after it has been deployed. To run any fancy change of code scenario, any data structure or call any method you don't need to redeploy your java code. You can see what your process sees in real time. This is an unvaluable tool to use to develop and maintain a java application.

What is this project about?
---------------------------

Clojure and REPL can be introduced into a pure Java project to improve troubleshooting without having to force a team to migrate their code away from Java.

nRepl is easy to start in Clojure but it needs a tiny bit of work to inject it into your java process. If you are using Spring and Java, this project has done that for you. This project is a Maven module that you can integrate in your java project and inject easily the Repl in your application.

Watch these videos to get an introduction:
* [EuroClojure2014](http://vimeo.com/100425265)
* [Skillsmatter Sept 2013](http://skillsmatter.com/podcast/home/the-repl-an-innovative-way-to-troubleshoot-javajvm-processes)

How to install the REPL in your application with Spring
-------------------------------

* insert the dependency inside your maven project

```xml
<dependency>
  <groupId>net.matlux</groupId>
  <artifactId>jvm-breakglass</artifactId>
  <version>0.0.8</version>
</dependency>
```

* add the following bean to your Spring config

```xml
<bean id="repl" class="net.matlux.NreplServerSpring">
  <constructor-arg index="0" value="1112" />
</bean>
```

1112 is the port number. For lifecycle control and cleanup, see the JMX examples below.

What if I don't use Spring?
---------------------------

No problems, just instanciate the following class in your application rather than using the xml spring context:
```java
    import net.matlux.NreplServer;
    NreplServer repl = new NreplServer(port);
    repl.put("department", myObject);
    // At application shutdown: repl.stop(); repl.unregisterMBean();
```

Repeat the call to put with as many object as you want to register on the repl. The NreplServer instance is a Map onto which you can add Object instances that you can retreive later on under the repl access.

MBean registration (new in R_0.0.7)
------------------

It is also possible to register the NreplServer as MBean for access via a JMX console. The registred MBean is found under the name `net.matlux:name=Nrepl`. The MBean has the following properties and operations.

|Type|Name|Meaning|
|----|----|-------|
|Attribute|Port|Indicates the port used for the nrepl server (read/write).|
|Attribute|Started|Indicates wether the NreplServer is started or not (read only).|
|Operation|start|Starts the NreplServer.|
|Operation|stop|Stops the NreplServer.|

The one-argument constructor starts the listener and registers this MBean automatically.
For an on-demand JMX switch, register the MBean while leaving the listener stopped:

```java
NreplServer repl = new NreplServer(1112, false, true, true, true);
repl.put("department", myDepartment);
// Keep repl for the lifetime of the application.
// During shutdown, even if application work fails:
try {
    repl.stop();
} finally {
    repl.unregisterMBean();
}
```

In JConsole, attach to the local application JVM, open **MBeans → net.matlux → Nrepl**,
then invoke the lowercase `start` operation. `Started` becomes true. Connect with
`lein repl :connect 127.0.0.1:1112`; invoke `stop` when finished. Change the `Port`
attribute before the next `start` to choose another port. Port `0` asks the OS to
choose one; read `Port` after starting to see the result.

The equivalent Spring bean leaves the REPL stopped until JMX starts it:

```xml
<bean id="repl" class="net.matlux.NreplServerSpring" destroy-method="stop">
  <constructor-arg index="0" value="1112" />
  <constructor-arg index="1" value="false" /> <!-- startOnCreation -->
  <constructor-arg index="2" value="true" />  <!-- registerMBeanOnCreation -->
  <constructor-arg index="3" value="true" />  <!-- propagateException -->
  <constructor-arg index="4" value="true" />  <!-- logExceptionStack -->
</bean>
```

Closing the Spring context invokes `stop`. The application must also unregister
its MBean during shutdown (keep a reference to the bean before closing):

```java
NreplServerSpring repl = context.getBean("repl", NreplServerSpring.class);
try {
    context.close();
} finally {
    repl.unregisterMBean();
}
```

There is one shared listener and one MBean name per class loader/platform server
respectively. Starting another instance replaces the listener; stopping any
instance stops it. Lifecycle calls are serialized, but the object registry is a
`HashMap`: coordinate concurrent access in the hosting application.

The listener now binds to **127.0.0.1**. nREPL permits arbitrary code execution in
the host JVM and has no authentication here. For remote access, use an SSH tunnel:
`ssh -L 1112:127.0.0.1:1112 user@host`, then connect to local port 1112.
This is a deliberate change from the old all-interfaces binding.


Quick demonstration of this project
-----------------------------------

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



## License

Copyright (C) 2015 Mathieu Gauthron

Distributed under the Eclipse Public License, the same as Clojure.
