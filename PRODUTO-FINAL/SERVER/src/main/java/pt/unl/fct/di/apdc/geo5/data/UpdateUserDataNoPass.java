package pt.unl.fct.di.apdc.geo5.data;

public class UpdateUserDataNoPass {

	//public String username;
	public String name;
	public String email;	
	public String password;
	public String street;
	public String place;
	public String country;
	public String birthday;
	public String zipCode;
	//public String role;
	//public boolean isActive;

	public UpdateUserDataNoPass() {
		
	}
	
	public UpdateUserDataNoPass(String name, String email, String street, String place, String country, String birthday, String zipCode) {
		
			//this.username = username;
			this.name = name;
			this.email = email;
			//this.role = role;
			this.street = street;
			this.place = place;
			this.country = country;
			//this.isActive = isActive;
			this.birthday = birthday;
			this.zipCode = zipCode;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(this.name) &&
			   validEmail() &&
			   validField(this.password);
	}
	
    private boolean validEmail() {
        return this.email.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

}