package net.matlux.testserver;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.matlux.NreplServer;
import net.matlux.testobjects.Department;
import net.matlux.testobjects.Employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SpringServerExample implements ApplicationContextAware
{
	
    private static final Logger LOG = LoggerFactory.getLogger(SpringServerExample.class);

	static public SpringServerExample instance=null;
	

    @Autowired
	private ApplicationContext ctx;



    public static void main(String[] args) throws Exception {
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring/server-test.xml");
    	while (true) {
    		
    		Thread.sleep(2500);
    		System.out.println("Retrieve Employees from NY or London:");
    		displayTransactionContent();
    	}
    }

	@SuppressWarnings("unchecked")
	private static void displayTransactionContent() {
		Department dep = (Department)NreplServer.instance.get("department");
		List<Employee> emps = dep.getEmployees();
		Collections.sort(emps,new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				Employee e1 = (Employee)o1;
				Employee e2 = (Employee)o2;
				return e1.getAddress().getCity().compareTo(e2.getAddress().getCity());
			}
			
		});
		for(Employee e:emps) {
			if("london".equalsIgnoreCase(e.getAddress().getCity()) || "NY".equalsIgnoreCase(e.getAddress().getCity())) {
				System.out.println(e.getFirstname());
			}
			
		}
		
	}
    
	public Object getObj(String beanName) {
		return ctx.getBean(beanName);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;

	}
	
	public ApplicationContext getApplicationContext() {
		return ctx;

	}


}
