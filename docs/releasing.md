# Releasing to Clojars

This procedure is for maintainers of `net.matlux/jvm-breakglass`. Run commands
from the repository root with Maven 3.9+ and a supported JDK. Publication is
manual; the GitHub Actions build does not deploy artifacts.

## Prepare the release

1. Set the stable version in `bootloader/pom.xml` and its SCM tag to
   `R_<version>`, following the repository's tag convention.
2. Update the installation examples in `README.md` and add release notes to
   `CHANGELOG.md`. Mark the version unreleased until publication succeeds.
3. Review the changes in a PR and require passing CI before merging.

Build with the default dependency versions from the POM:

```sh
mvn -B -f bootloader/pom.xml -Prelease clean verify
```

Do not use the CI Clojure-version override for publication. The release must
match the dependencies declared in its POM. Check the library, source and
Javadoc JARs under `bootloader/target/`; the main JAR must contain the Clojure
namespaces and no test fixtures. Test the packaged library in a separate
consumer as well as running the module tests.

An optional deployment rehearsal writes to a temporary filesystem repository:

```sh
release_check_dir="$(mktemp -d /tmp/jvm-breakglass-release-check.XXXXXX)" &&
mvn -B -f bootloader/pom.xml -Prelease clean deploy \
  "-DaltDeploymentRepository=release-check::file://$release_check_dir"
```

That explicit repository override keeps the upload local. Without it, `deploy`
uses the Clojars repository configured in the POM.

## Tag the source revision

After the preparation PR is merged, update a clean checkout of `master` and
confirm that CI passed on the intended release revision. The following example
uses 0.1.0; change the version for subsequent releases:

```sh
release_version=0.1.0
git switch master &&
git pull --ff-only &&
test -z "$(git status --porcelain)" &&
git tag -a "R_$release_version" -m "Release jvm-breakglass $release_version" &&
git show --no-patch "R_$release_version"
```

Inspect the tag before pushing it:

```sh
git push origin "refs/tags/R_$release_version"
```

Build and deploy from this same clean, tagged revision. Pushing a tag does not
publish to Clojars. Do not move a tag once artifacts have been published from it.

## Configure authentication

Create a [Clojars deploy token](https://clojars.org/tokens) for an account with
permission to publish `net.matlux/jvm-breakglass`, preferably scoped to this
artifact. Deployment uses a token in place of the account password.

Add this server entry inside `<settings><servers>` in the user's Maven
`~/.m2/settings.xml`, preserving any existing configuration:

```xml
<server>
  <id>clojars</id>
  <username>${env.CLOJARS_USERNAME}</username>
  <password>${env.CLOJARS_DEPLOY_TOKEN}</password>
</server>
```

Provide those environment variables through a secret manager or an interactive
terminal prompt. Keep the token out of source control and shell history. The
server ID must match `clojars` in the POM's `distributionManagement` section.
An alternative settings file can be supplied with Maven's `-s` option.

## Publish and check

With authentication configured, run:

```sh
mvn -B -f bootloader/pom.xml -Prelease clean deploy
```

This runs the tests, installs locally, and uploads the POM plus the library,
source and Javadoc JARs to Clojars. The profile does not sign artifacts; Clojars
accepts unsigned uploads. If signatures are added, every artifact must be signed.

Confirm the new version appears on the
[Clojars project page](https://clojars.org/net.matlux/jvm-breakglass). Verify a
fresh consumer download, the published POM's dependencies, and remote evaluation
against the downloaded JAR. Compare the downloaded artifacts with the release
build output. A published stable version cannot be overwritten; inspect the
repository state before retrying a failed deployment.

After confirmation, record the release date in `CHANGELOG.md`, remove the README's
unreleased notice, and create a GitHub release for the existing tag using those
notes. In a follow-up development commit, advance the POM to the next `-SNAPSHOT`
version and set its SCM tag to `HEAD`. Keep consumer examples on the stable version.

## Updating hosted documentation after publication

`docs/cljdoc.edn` selects the README, changelog and historical examples for
cljdoc. Maintainer procedures and preparation records remain on GitHub, outside
the hosted documentation's article navigation.

For article or navigation corrections to an existing release, commit the changes
and create a separate `cljdoc-<version>` tag at that commit (for example,
`cljdoc-0.1.0`). Push that tag, then use **Build** on the version's cljdoc page,
or **Rebuild** if documentation already exists. Cljdoc reads the articles and
table of contents from this override tag while analysing the published library.
Leave the original `R_<version>` tag unchanged; no Clojars redeployment is needed.
Changes to code or API docstring content require a new library release.

## References

- [Clojars publishing and validations](https://github.com/clojars/clojars-web/wiki/Pushing)
- [Deploy tokens](https://github.com/clojars/clojars-web/wiki/Deploy-Tokens)
- [Maven deployment repository override](https://maven.apache.org/plugins/maven-deploy-plugin/deploy-mojo.html)
- [Cljdoc documentation overrides](https://github.com/cljdoc/cljdoc/blob/master/doc/userguide/for-library-authors.adoc#overriding-cljdoc-config--articles)
