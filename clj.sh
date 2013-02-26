REPO=~/.m2/repository


CLOJURE=$CLASSPATH:$REPO/org/clojure/clojure/1.4.0/clojure-1.4.0.jar
CLOJURE=$CLOJURE:./src/test
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
