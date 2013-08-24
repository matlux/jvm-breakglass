package net.matlux.testobjects;

import java.util.LinkedList;
import java.util.List;

public class Department {
	
	private List<Employee> employees = new LinkedList<Employee>();
	private String name;
	private long id;
	
	public Department(String name,long id) {
		this.name = name;
		this.id = id;
	}

	public void add(Employee employee) {
		employees.add(employee);
		
	}
	
	
	
	

}
