# How to release with signature with maven:

* change <version>0.0.7</version>
* change <tag> to current git revision
* commit
* add tag R_0.0.7
```
mvn verify gpg:sign install:install deploy:deploy
```


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


