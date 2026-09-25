# Dependency audit and upgrade plan

Assessed 2026-09-25 against local commit `e6d107d` (2015-03-11), the GitHub issue
tracker, official release documentation, and Maven Central/Clojars metadata.

## Assessment

This is an approximately **11.5-year-old project**, built around a 2011–2015
library stack. It is several framework generations behind, not merely missing
patch updates. The small core is tractable to modernize; the historical web
examples require a separate servlet/JSON migration.

The repository has five independent Maven builds, with no root reactor:

- `bootloader`: five Java classes and five Clojure namespaces implementing a
  Java object registry, a shared embedded nREPL listener, optional Spring lookup,
  JMX lifecycle control, and reflection helpers.
- `examples/server` and `examples/server-no-spring`: console demonstrations.
- `examples/SpringMVC` and `examples/SpringMVCrest`: legacy WAR demonstrations.

The bootloader version is `0.0.9-SNAPSHOT`, while examples resolve published
`0.0.6`/`0.0.7` artifacts. Building an example therefore does **not** test changes
to this checkout. Launch scripts also hard-code older dependency JAR paths.

## Dependency inventory

Versions in the first column are from the original checkout, before this change.
Targets are recommendations, not a statement that the production dependencies
have all been upgraded.

| Component | Original version / location | Current reference / proposed action |
| --- | --- | --- |
| Java bytecode | 6 in core/WARs, 7 in console examples | Java 8 transitional build now; propose Java 17 minimum for the next major release, tested on 17/21/25. |
| Clojure | `org.clojure:clojure:1.6.0` | Stable **1.12.6**, six feature releases ahead. Upgrade alongside nREPL for a maintained stack. 1.13 is still prerelease. [Official downloads](https://clojure.org/releases/downloads). |
| nREPL | `org.clojure:tools.nrepl:0.2.6` | Maintained artifact is **`nrepl:nrepl:1.7.0`**. Development moved after 0.2.13; namespace migration is required. [Old project](https://github.com/clojure/tools.nrepl), [installation](https://nrepl.org/nrepl/installation.html). |
| Spring | Core `spring-web:3.0.7.RELEASE` provided; examples 3.0.4/3.0.5/3.2.2 | **7.0.9** current stable. Four major generations ahead. Prefer `spring-context` for the adapter, since it does not use web APIs. [Reference](https://docs.spring.io/spring-framework/reference/overview.html). |
| Filesystem helpers | `me.raynes:fs:1.4.6` | Still the last version under these coordinates; a numerically current artifact can still be legacy. Used in example REPL commands, not server implementation. Move to optional examples or replace with Java NIO / `clojure.java.io`. [Clojars metadata](https://repo.clojars.org/me/raynes/fs/maven-metadata.xml). |
| JUnit | 4.12 core, 3.8.1 console examples | Core moved to **4.13.2** for this baseline. Plan Jupiter **6.1.3** after Java 17 adoption; retain a Clojure test bridge. [JUnit guide](https://docs.junit.org/current/user-guide/). |
| Jackson | `org.codehaus.jackson:jackson-mapper-asl:1.9.10`, REST example only | Jackson 1.x is unsupported. Migrate JSON configuration/tests to Jackson 3.x with Spring 7 (3.2 release line; 3.1 LTS also exists). This is a coordinate/API migration, not a version substitution. [Release policy](https://github.com/FasterXML/jackson/wiki/Jackson-Releases). |
| Clojure Maven plugin | 1.3.10 core and console examples | Artifact metadata reaches 1.9.4, but core does not need AOT compilation. Removed from core; source resources load at runtime and JUnit runs Clojure tests. Still present in historical examples. [Metadata](https://repo.maven.apache.org/maven2/com/theoryinpractise/clojure-maven-plugin/maven-metadata.xml). |
| Maven compiler plugin | 3.1 core, 2.3.2 or unpinned examples | Core pinned to 3.14.1 with `release=8`. Examples still need migration. |
| Test/build infrastructure | Implicit test plugin, Travis config, HTTP Clojars | Core Surefire/Failsafe 3.5.4, GitHub Actions, HTTPS repositories. Maven 3.9.16 used locally. Maven 4 remains a separate migration. |
| Eclipse plugin | 2.9 in REST example | Remove obsolete IDE-specific generation when rebuilding this example. |

Spring 6.2 is **not** a good long-term open-source destination: its OSS support
ended in June 2026. Spring 7 retains a Java 17 baseline and moves the web stack
to Jakarta EE 11 / Servlet 6.1. Keep `javax.management` and other Java SE packages:
only relevant Java EE APIs migrate to `jakarta.*`.
[Spring support policy](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions).

The Spring `provided` scope means host applications choose their actual Spring
runtime. Updating this POM alone cannot upgrade consumers. Likewise, obsolete
Jackson is confined to the REST example, not a core transitive dependency.
The resolved legacy core runtime also includes `commons-compress:1.8` and
`org.tukaani:xz:1.5` via `me.raynes/fs`. Removing the optional filesystem library
would remove both from consumers. The provided Spring tree includes
`commons-logging:1.1.1` and `aopalliance:1.0`.
This is a dependency/support audit, not a completed CVE or exploitability scan;
check resolved consumer dependencies as part of release validation.

## Fixes and test foundation implemented

- Fixed HTTP Clojars URLs in all five POMs. The original core build actually
  failed on Maven's HTTP repository blocker before compilation.
- Modernized the core compiler/test configuration; moved JUnit to 4.13.2.
  Clojure and other production library versions remain at their original values.
- Kept Clojure source in the JAR, while removing accidental packaging of
  `src/test/resources/spring/server-test.xml` and test source resources.
- Replaced fixed ports and the leaked RMI registry with OS-assigned ports.
  `getPort()` now reports the chosen port after startup when port zero was used.
- Added Java registry tests, Clojure field/collection/lookup/client tests, and
  integration coverage for real eval, cloned sessions, Spring XML and live
  object mutation, bind failures, restart, concurrent lifecycle calls, and
  remote JMX attributes/operations.
- Concurrent workers now use a start barrier, checked `Future.get` results,
  bounded waits, and cleanup in `finally`; worker failures can no longer be
  silently discarded. Replaced the 4,000-operation stress loop with a bounded
  contention test; prolonged stress testing can be added separately.
- Serialized the Clojure stop/replace sequence and clear the old handle before
  trying to bind again. A failed replacement now reports `isStarted() == false`
  instead of retaining a closed server handle.
- Added explicit `RT.init()` before loading the server namespace. New Clojure
  versions defer initialization; without this, remote forms such as `(+ 1 1)`
  failed with `Unable to resolve symbol: + in this context`. Existing remote
  eval tests reproduce the failure under the 1.12.6 override and pass with the fix.
- Made Spring registry lookups work before context injection.
- Explicitly realize client results within the connection scope. This is
  defensive: the pinned 0.2.6 `response-values` already consumes responses.
- Bound the unauthenticated REPL to **127.0.0.1**, replacing the old all-interface
  default. README documents SSH forwarding for remote access.
- Added public API Javadocs, lifecycle examples, and namespace-qualified REPL
  examples. Marked the historical `ReplStartup` entry point deprecated; its
  external `server.socket` namespace is not part of this artifact.

**Compatibility changes:** core builds now emit Java 8 bytecode rather than
Java 6; remote clients must use local access or a tunnel; the automatically
assigned port is visible via `getPort()`. Removing AOT output changes packaging
but preserves loadable Clojure sources. Validate any consumer relying on generated
Clojure class names before release.

The swapped city/street getters in demonstration `Address` classes are
intentional: the tutorial demonstrates correcting their behavior at runtime.
They have been preserved and the live mutation scenario remains tested.

## GitHub issues

Four open issues were found; their changes are implemented in this branch.
The pull request links these issues for closure when the changes are merged.

| Issue | Resolution in this checkout |
| --- | --- |
| [#22: recommend require](https://github.com/matlux/jvm-breakglass/issues/22) | README and interactive examples now use aliases and qualified calls, avoiding `fs/name` and `fs/parents` collisions. |
| [#17: JMX examples](https://github.com/matlux/jvm-breakglass/issues/17) | Java/Spring configuration, JConsole steps, port changes, shutdown and remote access examples. Remote JMX lifecycle integration test. |
| [#16: Java documentation](https://github.com/matlux/jvm-breakglass/issues/16) | Public Java APIs document constructor flags, shared listener semantics, port reporting, failures and cleanup; Javadoc build configured. |
| [#13: intermittent CI failures](https://github.com/matlux/jvm-breakglass/issues/13) | Dynamic ports, deterministic cleanup, checked concurrency failures, separate unit/integration phases and GitHub Actions. The historical Travis failure cannot be reproduced from the issue's short description, so no single original cause is claimed. |

Closed issue #19 (session cloning / `StdOutBuffer`) gets a real clone/eval/close
regression test. Existing JMX registration/error behavior and the thread-safety
scenario from closed issues are retained in the replacement tests.

## Recommended sequence

1. **Land the test foundation and fixes in this change.** Review Java 8 and
   loopback binding compatibility changes. Run the configured 8/17/21/25 matrix
   with both Clojure 1.6.0 and 1.12.6 before merging; do not interpret a passing modern JVM test as vendor support
   for Spring 3 or tools.nrepl 0.2.6.
2. **Upgrade Clojure and nREPL together.** Use Clojure 1.12.6 and nREPL 1.7.0,
   changing server/client imports from `clojure.tools.nrepl[.server]` to
   `nrepl.core`/`nrepl.server`. Test both this client and editor/Lein clients,
   including clone, eval, interruption, session closure and actual application
   object access. Java 21+ forced interruption requires additional nREPL agent
   configuration; do not enable self-attachment silently in embedded consumers.
   [nREPL JVM compatibility](https://nrepl.org/nrepl/installation.html).
3. **Separate the Spring adapter and adopt Java 17.** Keep the core free of
   Spring classes and move Spring integration into a dedicated artifact using
   Spring 7.0.9 `spring-context`. Preserve a compatibility artifact if existing
   Java 8/Spring 3 consumers are still required. Replace global singleton state
   with explicit listener ownership and an `AutoCloseable` lifecycle; define
   whether multiple listeners/registries are supported. Add failure-path tests
   for constructor rollback and ownership-safe MBean cleanup.
4. **Rebuild examples against the local reactor.** Align all examples to the
   local version, replace hard-coded launch classpaths with Maven-resolved ones,
   and modernize the two WAR examples together with their servlet container and
   Jackson stack. Add HTTP smoke tests for controller responses/JSON and a
   startup/shutdown test for each console example. Remove Eclipse/legacy Clojure
   plugins and obsolete transitive dependencies. Treat example migration as a
   distinct reviewable change, not part of the core dependency bump.
5. **Harden introspection and prepare a release.** Add cycle/depth/size limits,
   inherited-field handling, and explicit behavior for inaccessible JDK fields,
   booleans, characters, enums and arrays. Java's module boundaries make blind
   `setAccessible(true)` unreliable; avoid requiring global `--add-opens` flags.
   Migrate the test harness to JUnit Jupiter, add dependency update automation
   and a resolved dependency security scan, validate a packaged-JAR consumer
   without Spring, and decide Clojars versus Maven Central publication.

The main architectural risks are the global mutable listener/registry,
non-thread-safe `HashMap`, broadly caught initialization errors, and unbounded
reflective object traversal. The new tests establish a useful baseline; they do
not claim to cover every arbitrary object graph or hosting container.

## Validation

See the validation record below for the actual local results. GitHub Actions is
configured but has not run remotely from this unpublished checkout.

```sh
mvn -f bootloader/pom.xml clean verify
mvn -f bootloader/pom.xml test
mvn -f bootloader/pom.xml javadoc:javadoc
mvn -f bootloader/pom.xml dependency:tree
```

The Java unit phase contains three registry tests and one JUnit bridge that runs
five Clojure tests (15 assertions). The integration phase contains nine tests.
That is **17 behavioral test cases**, reported by Maven as 13 JUnit methods because
five Clojure tests share one bridge method.

The first Clojure 1.12.6 experiment passed unit tests but failed remote eval.
Protocol diagnostics identified missing `clojure.core` references in the `user`
namespace. Adding `RT.init()` in Java startup resolved this for both old and new
nREPL, rather than requiring application callers to initialize Clojure themselves.
The official Java API also explicitly initializes the runtime:
[Clojure Java API source](https://github.com/clojure/clojure/blob/clojure-1.12.6/src/jvm/clojure/java/api/Clojure.java).

An isolated copy also passes the complete suite on JDK 23 with the following
migration changes, **not applied to the production dependency declarations**:

- `maven.compiler.release=17`, Clojure 1.12.6.
- Replace `org.clojure:tools.nrepl:0.2.6` with `nrepl:nrepl:1.7.0`.
- Replace `clojure.tools.nrepl` imports/references in client/tests with
  `nrepl.core`, and `clojure.tools.nrepl.server` with `nrepl.server`.
- Replace provided `spring-web:3.0.7.RELEASE` with `spring-context:7.0.9`.

This probe validates the core API path, not every consumer, servlet container,
editor integration, or nREPL interrupt behavior. Use the staged plan above to
turn it into a release.

| Local verification | Result |
| --- | --- |
| JDK 23 / Clojure 1.6.0 / legacy nREPL and Spring | Full `clean verify` and Javadoc generation passed. |
| JDK 8 / Clojure 1.6.0 / legacy nREPL and Spring | Full `clean verify` passed. |
| JDK 8 / Clojure 1.12.6 / legacy nREPL and Spring | Full `clean verify` passed with explicit initialization fix. |
| JDK 23 / Clojure 1.12.6 / legacy nREPL and Spring | Full `clean verify` passed with explicit initialization fix. |
| JDK 23 / Clojure 1.12.6 / nREPL 1.7.0 / Spring 7.0.9 (isolated probe) | Full `clean verify` passed, Java 17 bytecode target. |
| Javadocs | Generated successfully; public API documentation checked by Javadoc. |
| Packaged JAR | Clojure sources present; test Spring XML and fixture classes absent. |

Java 17, 21 and 25 are configured in CI but were not installed locally and have
not been executed in this session. The example applications remain outside this
verification. Maven and dependency caches were placed in temporary directories;
no machine-wide Maven installation was changed.
