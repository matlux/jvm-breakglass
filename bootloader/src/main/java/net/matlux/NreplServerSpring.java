package net.matlux;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;
/**
 * Hello world!
 *
 */
public class NreplServerSpring extends NreplServer implements ApplicationContextAware
{
	

	//static public NreplServerSpring instance=null;
	
	final static public int DEFAULT_PORT=1111;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET = Symbol.intern("net.matlux.server.nrepl");
    final static private Var CREATE_REPL_SERVER = RT.var("net.matlux.server.nrepl","start-server-now");

    @Autowired
	private ApplicationContext ctx;

    public NreplServerSpring(int port) {
    	super(port);
    	/*LOG.info("starting ReplStartup on Port=" + port);
    	System.out.println("starting ReplStartup on Port=" + port);
    	try {
        	USE.invoke(SERVER_SOCKET);
    		CREATE_REPL_SERVER.invoke(port);
    		LOG.info("Repl started successfully");
    		System.out.println("Repl started successfully");
    	} catch (Throwable t) {
    		LOG.error("Repl startup caught an error: " + t);
    		System.out.println("Repl startup caught an error: " + t);
    	}
    	
        instance=this;*/
    }

    public static void main(String[] args) throws Exception {
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring/server-test.xml");
    }

	public Object getObj(String beanName) {
		return get(beanName);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;

	}
	
	public ApplicationContext getApplicationContext() {
		return ctx;

	}

	@Override
	public int size() {
		return ctx.getBeanDefinitionCount() + super.size();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && ctx.getBeanDefinitionCount() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String && ctx.containsBean((String)key)) {
			return true;
		} else {
			return super.containsKey(key);
		}
		
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String && ctx.containsBean((String)key)) {
			return ctx.getBean((String)key);
		} else {
			return super.get(key);
		}
	}
	

}
