package net.matlux;



import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;
/**
 * Hello world!
 *
 */
public class ReplStartup 
{
	
	final static public int a=1224;
	
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET = Symbol.intern("server.socket");
    final static private Var CREATE_REPL_SERVER = RT.var("server.socket","create-repl-server");


    public static void main(String[] args) throws Exception {
    	USE.invoke(SERVER_SOCKET);
         CREATE_REPL_SERVER.invoke(1111);
    }
}
