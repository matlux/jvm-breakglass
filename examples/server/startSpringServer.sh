REPO=~/.m2/repository


CLOJURE=$CLASSPATH:$REPO/org/clojure/clojure/1.4.0/clojure-1.4.0.jar
CLOJURE=$CLOJURE:$REPO/org/clojure/tools.nrepl/0.2.1/tools.nrepl-0.2.1.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-web/3.0.4.RELEASE/spring-web-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-context/3.0.4.RELEASE/spring-context-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-core/3.0.4.RELEASE/spring-core-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-beans/3.0.4.RELEASE/spring-beans-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-asm/3.0.4.RELEASE/spring-asm-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-expression/3.0.4.RELEASE/spring-expression-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-asm/3.0.4.RELEASE/spring-asm-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar
CLOJURE=$CLOJURE:$REPO/org/slf4j/slf4j-api/1.6.3/slf4j-api-1.6.3.jar
CLOJURE=$CLOJURE:$REPO/me/raynes/fs/1.4.5/fs-1.4.5.jar
CLOJURE=$CLOJURE:./src/test/clojure
#CLOJURE=$CLOJURE:$REPO/net/matlux/repl-bootloader/1.0-SNAPSHOT/repl-bootloader-1.0-SNAPSHOT.jar
CLOJURE=$CLOJURE:$REPO/net/matlux/jvm-breakglass/0.0.5/jvm-breakglass-0.0.5.jar
CLOJURE=$CLOJURE:./target/server-test-1.0-SNAPSHOT.jar
CLOJURE=$CLOJURE:${PWD}

java -cp "$CLOJURE" net.matlux.testserver.SpringServerExample "$@"
