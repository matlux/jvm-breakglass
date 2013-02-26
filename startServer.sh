REPO=~/.m2/repository


CLOJURE=$CLASSPATH:$REPO/org/clojure/clojure/1.4.0/clojure-1.4.0.jar
CLOJURE=$CLOJURE:$REPO/org/clojure/tools.nrepl/0.2.1/tools.nrepl-0.2.1.jar
CLOJURE=$CLOJURE:./src/test
CLOJURE=$CLOJURE:target/repl-bootloader-1.0-SNAPSHOT.jar
CLOJURE=$CLOJURE:${PWD}


java -cp "$CLOJURE" net.matlux.NreplServerStartup "$@"
