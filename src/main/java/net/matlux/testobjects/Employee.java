package net.matlux.testobjects;

public class Employee {
	
	private String firstname;
	private String lastname;
	
	private Address address;
	
	public Employee(String firstname, String lastname, Address address) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.address = address;
	}
	
}
