package net.matlux.testobjects;

public class Employee {
	
	private String firstname;
	private String lastname;
	
	private Address address;
	
	public Employee(String firstname, String lastname, Address address) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.setAddress(address);
	}

	public String getFirstname() {
		return firstname;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	public String toString() {
		return firstname + " " +lastname;
	}
	
	
	@Override public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Employee) {
        	Employee that = (Employee) other;
            result = (this.firstname.equals(that.firstname) &&
            		this.lastname.equals(that.lastname) &&
            		this.address.equals(that.address) && 	
            		super.equals(that));
        }
        return result;
    }
	
}
