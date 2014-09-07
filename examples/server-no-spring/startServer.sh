REPO=~/.m2/repository


CLOJURE=$CLASSPATH:$REPO/org/clojure/clojure/1.6.0/clojure-1.6.0.jar
CLOJURE=$CLOJURE:$REPO/org/clojure/tools.nrepl/0.2.1/tools.nrepl-0.2.1.jar
CLOJURE=$CLOJURE:./src/test
CLOJURE=$CLOJURE:${PWD}
CLOJURE=$CLOJURE:$REPO/me/raynes/fs/1.4.5/fs-1.4.5.jar
CLOJURE=$CLOJURE:$REPO/net/matlux/jvm-breakglass/0.0.7/jvm-breakglass-0.0.7.jar
CLOJURE=$CLOJURE:./target/server-no-spring-test-1.0-SNAPSHOT.jar

OPTIONS=-Dcom.sun.management.jmxremote.port=9595
OPTIONS=$OPTIONS\ -Dcom.sun.management.jmxremote.ssl=false
OPTIONS=$OPTIONS\ -Dcom.sun.management.jmxremote.authenticate=false
OPTIONS=$OPTIONS\ -Djava.rmi.server.hostname=localhost
#OPTIONS=$OPTIONS\ -Djava.rmi.server.hostname=192.168.0.20

java -cp "$CLOJURE" $OPTIONS net.matlux.testserver.SimpleServerExample "$@"
