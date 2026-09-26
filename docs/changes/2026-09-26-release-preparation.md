# 0.1.0 release preparation — 2026-09-26

## Scope and status

Prepare the core `net.matlux/jvm-breakglass` artifact for its first release since
0.0.8. The runtime fixes and test foundation were merged in
[PR #27](https://github.com/matlux/jvm-breakglass/pull/27). At this checkpoint,
release metadata and documentation are local changes; 0.1.0 has not been
published. The runtime dependency migration and historical application examples
remain outside this release.

This record preserves decisions and verification evidence. User instructions
belong in the [README](../../README.md), reusable maintainer commands in the
[release procedure](../releasing.md), and migration work in
[UPGRADE.md](../../UPGRADE.md). Machine-specific setup and interactive coaching
are not project documentation.

## Decisions

- Use version 0.1.0 to distinguish the Java 8 minimum and loopback-only listener
  from the previous published version. Use `R_0.1.0` to match existing tag names.
- Retain the current runtime dependency baseline; do not imply that compatibility
  checks with Clojure 1.12.6 upgrade the default dependency.
- Attach Java/Clojure sources and Javadocs through a Maven `release` profile.
  CI verifies packaging, but publication remains a separate manual operation.
- Keep the top-level README focused on embedding, connecting and lifecycle
  management. Preserve older tutorials as explicitly historical reference material.
- Keep the release notes pending until Clojars publication succeeds.

## Verification completed during preparation

- Java 8 and Java 23 builds with the default Clojure 1.6.0 dependency passed all
  20 behavioral cases (15 Maven test methods, including a six-test Clojure bridge).
- The Java 23 release build deployed successfully to a temporary local filesystem
  repository. All three deployed JARs matched the build output byte for byte.
- Inspected the library JAR: Java classes use bytecode version 52, Clojure sources
  are included, test fixtures are absent, and embedded version/SCM metadata matches
  0.1.0 / R_0.1.0. Source and Javadoc attachments contain the expected files.
- A separate Java 8 consumer loaded the packaged JAR without Spring, registered
  an object, and retrieved its value through a real nREPL connection.
- PR #27 passed the Java/Clojure CI matrix before merge. The release-profile
  changes still need their own CI run; the local checks above do not replace it.

## Publication checkpoint — 2026-09-26

- [PR #28](https://github.com/matlux/jvm-breakglass/pull/28) was squash-merged as
  `070d2873bbee5ca9f73a7017d9d57eb9ccc74103`. Both the PR matrix and the
  [master build](https://github.com/matlux/jvm-breakglass/actions/runs/36262727913)
  passed. Tag `R_0.1.0` identifies this revision.
- The maintainer published 0.1.0 to Clojars. The repository API and published POM
  confirm the version and release tag.
- Follow-up documentation marks the release published and explicitly selects
  consumer articles in `docs/cljdoc.edn`. The maintainer procedure and this record
  are excluded from cljdoc's article navigation.

## Remaining release work

Verify a fresh consumer download, commit the documentation changes, publish the
`cljdoc-0.1.0` documentation tag, and build or rebuild cljdoc. Create the GitHub
release and advance the development version separately. Update this record at
those checkpoints or if the scope changes.
