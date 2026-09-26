# Release notes

## 0.1.0 — pending publication

This release updates the build, fixes listener lifecycle problems and establishes
unit and integration coverage. It follows the published 0.0.8 release.

### Compatibility changes

- Java 8 is the minimum runtime; Java classes use bytecode version 52.
- The listener binds to `127.0.0.1`. For remote access, use an SSH tunnel, for
  example `ssh -L 1112:127.0.0.1:1112 user@host`, then connect to local port 1112.
- Clojure namespaces ship as source and load at runtime instead of relying on
  the obsolete AOT build plugin.

### Fixes and testing

- Initialize Clojure before remote evaluation, including with Clojure 1.12.6.
- Serialize listener lifecycle operations and handle transient fixed-port
  rebinding failures. Clear stale state after a failed replacement listener.
- Preserve automatic port selection across restarts and report the bound port.
- Permit Spring map access before application-context injection.
- Correct namespace examples, document JMX lifecycle and cleanup, and restore
  Javadoc generation. These changes address issues #13, #16, #17 and #22.
- Add 20 behavioral test cases: three Java unit tests, six Clojure tests and
  eleven socket, Spring and JMX integration tests. Maven reports 15 methods
  because one Java bridge runs all six Clojure tests.
- Add CI coverage for Java 8, 17, 21 and 25 with Clojure 1.6.0 and 1.12.6.
- Use HTTPS Clojars repositories, exclude test fixtures from the library, and
  attach source and Javadoc JARs when building with the `release` profile.

### Dependency scope

Default runtime dependencies remain Clojure 1.6.0, tools.nrepl 0.2.6 and
me.raynes/fs 1.4.6, with Spring Web 3.0.7.RELEASE in provided scope. JUnit moves
to 4.13.2 for tests. This release does not complete the runtime dependency or
historical example application migrations; see [UPGRADE.md](UPGRADE.md).

Thanks to Jonathan Leitschuh for the HTTPS repository proposals (#23 and #25),
and Eugen Stan for the dependency migration and localhost-binding work in #24.

## 0.0.8 — 2015-03-11

Previous published release: [Clojars](https://clojars.org/net.matlux/jvm-breakglass/versions/0.0.8).
