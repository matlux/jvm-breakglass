# How to release with signature with maven:

* change <version>0.0.8</version>  (pom.xml)
* change <tag> to current git revision (pom.xml)
* git commit -m "release R_0.0.8" -a 
* add tag R_0.0.8
```
mvn verify gpg:sign install:install deploy:deploy
```
* git tag -a R_0.0.8 -m "Release of version 0.0.8" 2881362
* change <version> to SNAPSHOT in pom.xml
* git commit -m "changed version to 0.0.8-SNAPSHOT" -a
* git push
* git push --tags

Make sure you add clojars to the <code>pom.xml</code>:

bc. <distributionManagement>
    <repository>
      <id>clojars</id>
      <name>Clojars repository</name>
      <url>https://clojars.org/repo</url>
    </repository>
  </distributionManagement>

Add authentication info to <code>settings.xml</code>:

bc. <server>
      <id>clojars</id>
      <username>username</username>
      <password>password</password>
    </server>


# How to release without signature:

scp -i ~/.ssh/id_rsa... pom.xml target/jvm-breakglass-0.0.5.jar clojars@clojars.org:


