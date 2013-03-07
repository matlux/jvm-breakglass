REPO=~/.m2/repository


CLOJURE=$CLASSPATH:$REPO/org/clojure/clojure/1.4.0/clojure-1.4.0.jar
CLOJURE=$CLOJURE:$REPO/org/clojure/tools.nrepl/0.2.1/tools.nrepl-0.2.1.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-web/3.0.4.RELEASE/spring-web-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-context/3.0.4.RELEASE/spring-context-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/springframework/spring-core/3.0.4.RELEASE/spring-core-3.0.4.RELEASE.jar
CLOJURE=$CLOJURE:$REPO/org/slf4j/slf4j-api/1.6.3/slf4j-api-1.6.3.jar
CLOJURE=$CLOJURE:./src/test/clojure
CLOJURE=$CLOJURE:target/repl-bootloader-1.0-SNAPSHOT.jar
CLOJURE=$CLOJURE:${PWD}

if [ "$#" -eq 0 ]; then
    #java -cp $CLOJURE clojure.main --repl
    exec rlwrap --remember -c -b "$breakchars" \
            -f "$HOME"/.clj_completions \
             java -cp "$CLOJURE" clojure.main
else
    #java -cp $CLOJURE clojure.main "$@"
    exec java -cp "$CLOJURE" clojure.main "$@"
fi
