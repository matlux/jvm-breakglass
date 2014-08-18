package net.matlux.testobjects;

public class Address {
	
	private String street;
	private String zipcode;
	private String city;

	public Address(String street, String zipcode, String city) {
		this.street = street;
		this.zipcode = zipcode;
		this.city = city;
	}
	
	public String getStreet() {
		return city;
	}

	public String getCity() {
		return street;
	}

	public String getZipcode() {
		return zipcode;
	}
	@Override public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Address) {
        	Address that = (Address) other;
            result = (this.street.equals(that.street) &&
            		this.city.equals(that.city) &&
            		this.zipcode.equals(that.zipcode) && 	
            		super.equals(that));
        }
        return result;
    }
}
