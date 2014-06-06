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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public void add(Employee employee) {
		employees.add(employee);
		
	}
	
	
	
	

}
