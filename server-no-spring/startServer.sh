REPO=~/.m2/repository


CLOJURE=$CLASSPATH:$REPO/org/clojure/clojure/1.4.0/clojure-1.4.0.jar
CLOJURE=$CLOJURE:$REPO/org/clojure/tools.nrepl/0.2.1/tools.nrepl-0.2.1.jar
CLOJURE=$CLOJURE:./src/test
CLOJURE=$CLOJURE:${PWD}
CLOJURE=$CLOJURE:$REPO/me/raynes/fs/1.4.5/fs-1.4.5.jar
CLOJURE=$CLOJURE:$REPO/net/matlux/repl-bootloader/0.0.3/repl-bootloader-0.0.3.jar
CLOJURE=$CLOJURE:./target/repl-server-no-spring-test-1.0-SNAPSHOT.jar


java -cp "$CLOJURE" net.matlux.testserver.SimpleServerExample "$@"
