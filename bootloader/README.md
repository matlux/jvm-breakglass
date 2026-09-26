# Core library: build and tests

This module produces `net.matlux/jvm-breakglass`. For installation, embedding
and REPL usage, see the [project README](../README.md).

## Requirements

Use Maven 3.9+ and JDK 8 or later. `mvn --version` shows which Java installation
Maven uses; set `JAVA_HOME` to select another JDK. The library targets Java 8
bytecode regardless of the JDK used to build it.

Run the commands below from the repository root. There is no root Maven reactor;
the historical projects under `examples/` are independent builds.

## Tests

```sh
mvn -B -f bootloader/pom.xml clean verify
```

Surefire runs three Java unit tests and a bridge that executes six Clojure tests
(17 assertions). Failsafe runs eleven integration tests covering real nREPL
connections, Spring beans, concurrent listener lifecycle and remote JMX.
Maven reports 15 test methods; the bridge expands the total to 20 behavioral
cases. Reports are written to `bootloader/target/surefire-reports/` and
`bootloader/target/failsafe-reports/`.

For unit tests only:

```sh
mvn -B -f bootloader/pom.xml test
```

To check compatibility with a different Clojure version:

```sh
mvn -B -f bootloader/pom.xml -Dclojure.version=1.12.6 clean verify
```

That override applies to this build; it does not edit the POM's default.
[GitHub Actions](../.github/workflows/build.yml) checks Java 8, 17, 21 and 25 with
Clojure 1.6.0 and 1.12.6. These tests cover the core module, not the historical
example applications or all possible host configurations.

## Build output

```sh
mvn -B -f bootloader/pom.xml -Prelease clean verify
```

The `release` profile adds source and Javadoc JARs alongside the library JAR in
`bootloader/target/`. Filenames contain the version declared in the POM.
`verify` does not install or publish them. CI runs this profile to check that
packaging works; it retains test reports but does not upload the JARs or publish
them to Clojars.

To install the library into the local Maven repository for use by another local
project, including before a version is published:

```sh
mvn -B -f bootloader/pom.xml -Prelease clean install
```

To generate browsable Javadocs without the release attachments:

```sh
mvn -B -f bootloader/pom.xml javadoc:javadoc
```

Open `bootloader/target/reports/apidocs/index.html` after generation.
For publication, see [Releasing](../docs/releasing.md). Dependency migration
work and its validation history are recorded in [UPGRADE.md](../UPGRADE.md).
