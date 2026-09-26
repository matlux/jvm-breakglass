# jvm-breakglass

[![Build](https://github.com/matlux/jvm-breakglass/actions/workflows/build.yml/badge.svg)](https://github.com/matlux/jvm-breakglass/actions/workflows/build.yml)
[![Clojars Project](https://img.shields.io/clojars/v/net.matlux/jvm-breakglass.svg)](https://clojars.org/net.matlux/jvm-breakglass)

Embed an nREPL server in a Java application to inspect live objects and evaluate
Clojure expressions inside its JVM. Register objects directly or access beans
from a Spring application context, and control the listener through JMX.

Version **0.1.0** is available on [Clojars](https://clojars.org/net.matlux/jvm-breakglass/versions/0.1.0)
and requires Java 8 or later. See [release notes](CHANGELOG.md) for changes from
0.0.8, including the move to loopback-only listening.

The library still depends on Clojure 1.6.0 and tools.nrepl 0.2.6 by default.
Spring integration is compiled against Spring 3; the host application supplies
Spring. Broader dependency upgrades are tracked in the [upgrade plan](UPGRADE.md).

## Installation

Add the library to your Maven dependencies:

```xml
<dependency>
  <groupId>net.matlux</groupId>
  <artifactId>jvm-breakglass</artifactId>
  <version>0.1.0</version>
</dependency>
```

Add Clojars to your project's repositories:

```xml
<repositories>
  <repository>
    <id>clojars</id>
    <url>https://repo.clojars.org</url>
  </repository>
</repositories>
```

For Leiningen, add `[net.matlux/jvm-breakglass "0.1.0"]` to `:dependencies`.
For Clojure CLI, add `net.matlux/jvm-breakglass {:mvn/version "0.1.0"}` to `:deps`.

## With Spring

Add this bean to your Spring configuration:

```xml
<bean id="repl" class="net.matlux.NreplServerSpring">
  <constructor-arg index="0" value="1112" />
</bean>
```

1112 is the port number. For lifecycle control and cleanup, see the JMX examples below.

## Without Spring

Create a server and register the objects you want to inspect:

```java
    import net.matlux.NreplServer;
    NreplServer repl = new NreplServer(1112);
    repl.put("department", myObject);
    // At application shutdown: repl.stop(); repl.unregisterMBean();
```

Call `put` for each object you want to expose. `NreplServer` implements
`Map<String, Object>`; registered objects can be retrieved from the REPL.

## JMX lifecycle control

The server exposes a management bean named `net.matlux:name=Nrepl`, accessible
through a JMX console such as JConsole:

|Type|Name|Meaning|
|----|----|-------|
|Attribute|Port|Indicates the port used for the nrepl server (read/write).|
|Attribute|Started|Indicates whether the NreplServer is started or not (read only).|
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
choose one on each start; read `Port` after starting to see the result.

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

In 0.1.0, the listener binds to **127.0.0.1**. nREPL permits arbitrary code execution in
the host JVM and has no authentication here. For remote access, use an SSH tunnel:
`ssh -L 1112:127.0.0.1:1112 user@host`, then connect to local port 1112.
This is a deliberate change from the old all-interfaces binding.


## Connect and inspect objects

With the application running and the listener started on port 1112:

```sh
lein repl :connect 127.0.0.1:1112
```

At the connected prompt, inspect objects registered with `put`:

```clojure
(require '[cl-java-introspector.core :as inspect])
(inspect/get-objs)
(def department (inspect/get-obj "department"))
(inspect/methods-info department)
(inspect/fields-info department)
```

For a Spring application:

```clojure
(require '[cl-java-introspector.spring :as spring])
(spring/get-beans)
(spring/get-bean "department")
```

`inspect/to-tree` recursively exposes an object's fields as Clojure data. It has
no cycle or depth limit, and Java module access rules can prevent access to
private fields. Use it on known, bounded object graphs.

## Examples and further reading

- [Historical example applications and REPL recipes](docs/examples.md).
- [Java API sources](bootloader/src/main/java/net/matlux) and
  [Clojure introspection helpers](bootloader/src/main/clojure/cl_java_introspector/core.clj).
- Project introductions: [EuroClojure 2014](https://vimeo.com/100425265) and
  [Skills Matter 2013](http://skillsmatter.com/podcast/home/the-repl-an-innovative-way-to-troubleshoot-javajvm-processes).

## Building and contributing

The core module requires JDK 8+ and Maven 3.9+. From the repository root:

```sh
mvn -B -f bootloader/pom.xml clean verify
```

See the [module README](bootloader/README.md) for test phases, compatibility
checks and build output, and the [upgrade plan](UPGRADE.md) for outstanding work.
Maintainers can follow the [release procedure](docs/releasing.md).

## License

Copyright (C) 2015 Mathieu Gauthron

Distributed under the Eclipse Public License, the same as Clojure.
