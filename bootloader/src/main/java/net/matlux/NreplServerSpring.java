package net.matlux;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * Exposes Spring beans alongside explicitly registered application objects.
 * Bean lookup takes precedence over registry lookup. Collection views inherited
 * from NreplServer contain only explicitly registered objects, not Spring beans.
 * Configure Spring destroy methods explicitly to stop and unregister the server.
 */
public class NreplServerSpring extends NreplServer implements ApplicationContextAware
{




	/** Historical Spring example port. Constructors always use their supplied port. */
	final static public int DEFAULT_PORT=1111;
    @Autowired
	private ApplicationContext ctx;

    /**
     * Creates a Spring-aware registry with explicit lifecycle options.
     * @param port listening port, or zero for automatic selection
     * @param startOnCreation whether to start immediately
     * @param registerMBeanOnCreation whether to register the JMX switch
     * @param propagateException whether to propagate start/stop errors
     * @param logExceptionStack whether to log lifecycle stack traces
     */
	public NreplServerSpring(int port, boolean startOnCreation, boolean registerMBeanOnCreation, boolean propagateException, boolean logExceptionStack) {
		super(port, startOnCreation, registerMBeanOnCreation, propagateException, logExceptionStack);
	}

    /**
     * Starts a listener and registers its JMX switch.
     * @param port listening port, or zero for automatic selection
     */
    public NreplServerSpring(int port) {
    	super(port);
    }

    /**
     * Loads an application-supplied Spring configuration.
     * @param args unused
     * @throws Exception if the context cannot be loaded
     */
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

    /**
     * Exposes the host application's Spring context to introspection clients.
     * @return the injected context, or null before Spring initialization
     */
	public ApplicationContext getApplicationContext() {
		return ctx;

	}

	@Override
	public int size() {
		return (ctx == null ? 0 : ctx.getBeanDefinitionCount()) + super.size();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && (ctx == null || ctx.getBeanDefinitionCount() == 0);
	}

	@Override
	public boolean containsKey(Object key) {
		if (ctx != null && key instanceof String && ctx.containsBean((String)key)) {
			return true;
		} else {
			return super.containsKey(key);
		}

	}

	@Override
	public Object get(Object key) {
		if (ctx != null && key instanceof String && ctx.containsBean((String)key)) {
			return ctx.getBean((String)key);
		} else {
			return super.get(key);
		}
	}


}
