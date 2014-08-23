package net.matlux;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * Hello world!
 *
 */
public class NreplServerSpring extends NreplServer implements ApplicationContextAware
{
	

	//static public NreplServerSpring instance=null;
	
	final static public int DEFAULT_PORT=1111;
    @Autowired
	private ApplicationContext ctx;

	public NreplServerSpring(int port, boolean startOnCreation, boolean registerMBeanOnCreation, boolean propagateException) {
		super(port, startOnCreation, registerMBeanOnCreation, propagateException);
	}

    public NreplServerSpring(int port) {
    	super(port);
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
