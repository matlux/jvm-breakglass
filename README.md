nRepl hook for Java
===================


Background on nRepl
-------------------


Setting up an [nrepl](https://github.com/clojure/tools.nrepl) can be useful to introspect into the JVM for troubleshouting/investigation or testing of regular Java applications. You can connect onto a process and use a Clojure prompt interactivelly or have client application that sends and execute Java code dynamically. It works because the code injected is Clojure and that the Clojure run-time allows to evaluate code at run-time. Furthermore Clojure interops very easily with Java i.e. you can translate pretty much any Java code into Clojure and access all your Java object from the injected Clojure code. This is the perfect tool to access the inside of your JVM process live after it has been deployed. To run any fancy change of code scenario, any data structure or call any method you don't need to redeploy your java code. You can see what your process sees in real time. This is an unvaluable tool to use to develop and maintain a java application.

What is this project about?
---------------------------

nRepl is easy to start in Clojure but it needs a tiny bit of work to inject it into your java process. If your using Spring and Java, this project has done that for you. This project is a Maven module that you can integrate in your java project and inject easily the Repl in your application.

How to install this with Spring
-------------------------------

* clone this project
* compile the project

    mvn clean install

* insert the dependency inside your maven project

```xml
    <dependency>
      <groupId>net.matlux</groupId>
      <artifactId>repl-bootloader</artifactId>
    </dependency>
```

* add the following bean to your Spring config

```xml
    <bean id="repl" class="net.matlux.NreplServerWithSpringLog4jStartup">
      <constructor-arg index="0" value="1234" />
    </bean>
```

1234 is the port number.

Quick demonstration of this project
-----------------------------------

* Compile this project
    mvn clean install
* Start the server with the NreplServerStartup
    ./startServer.sh
* Start the programatic client which introspects into the Java Objects
    ./clj.sh -m cl-java-introspector.core

What if I don't use Spring?
---------------------------

use

* net.matlux.NreplServerStartup

instead of

* net.matlux.NreplServerWithSpringLog4jStartup


Why are there 3 classes available?
----------------------------------

* net.matlux.ReplStartup
* net.matlux.NreplServerStartup
* net.matlux.NreplServerWithSpringLog4jStartup

## Simple telnet Repl server

Use the following class

    import net.matlux.ReplStartup
    new ReplStartup(1234);

Then access the repl via telnet

    telnet localhost 1234

You don't need a repl to access this version. But you can't use a client to programatically control it. It is only for testing or for very simple use cases.

## nRepl hook for standard Java applications

instanciate the following class:

    import net.matlux.NreplServerStartup;
    new NreplServerStartup(port); //start server listening onto port number


## nRepl hook for Spring and log4j applications

For nRepl server and Spring support use the following class:


    net.matlux.NreplServerWithSpringLog4jStartup

Add the following bean to your Spring config:

```xml
    <bean id="repl" class="net.matlux.ReplStartup">
      <constructor-arg index="0" value="1234" />
    </bean>
```

the server will listen onto the port 1234


# Accessing nRepl with a client

## Via the repl client with lein

    lein repl :connect [host:port]

## programatically

```clojure
    (require '[clojure.tools.nrepl :as repl])
    (with-open [conn (repl/connect :port 1111)]
     (-> (repl/client conn 1000)
       (repl/message {:op :eval :code "(+ 1 1)"})
       repl/response-values))
```

The above sends an expression "(+ 1 1)" to be evaluated remotely. See [nrepl](https://github.com/clojure/tools.nrepl) website for more details.

Also see quick demo above.

# Once you have the nRepl running inside your process. What can you do?

## retrieve the list of System properties from the java process

    (filter #(re-matches #"[so].*" (key %)) (into {} (System/getProperties)))

This example filters on a regex. It retrieves property keys which start with "so"

## Terminate the process ;)

    (System/exit 0)

## Print hello on the process console

    ((System.out/println "hello"))

## Coherence example: Retrieve the number of object in a Cache

```clojure
    (def all-filter (new AlwaysFilter))
    (def nodeCache (Caches/getCache "cachename")
    (let [all-filter (new AlwaysFilter)
      nodeCache (Caches/getCache "cachename")]
    (. (. nodeCache entrySet all-filter) size))
```


## Introspect into a Java bean (not a Spring one this time...)

    (bean obj)

## Introspect into a Java Object

    (obj2map myObject)

See src/test//cl_java_introspector/core.clj for details implementation.

## Special for `NreplServerWithSpringLog4jStartup`

### Retrieve a Spring bean called "mybean"

    (import 'net.matlux.NreplServerWithSpringLog4jStartup)

    (. NreplServerWithSpringLog4jStartup/instance getObj "mybean")

### Retrieve a list of Spring beans

    (. (. NreplServerWithSpringLog4jStartup/instance getApplicationContext) getBeanDefinitionNames)


## Special  for `NreplServerStartup`

### Retrieve a Spring bean called "mybean"

    (import 'net.matlux.NreplServerStartup)

    (. NreplServerStartup/instance getObj "mybean")

### Retrieve a list of Spring beans

    (. (. NreplServerStartup/instance getApplicationContext) getBeanDefinitionNames)


## License

Copyright (C) 2013 Mathieu Gauthron

Distributed under the Eclipse Public License, the same as Clojure.
