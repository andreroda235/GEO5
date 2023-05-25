package pt.unl.fct.di.apdc.geo5.data;

public class UserUpdateDataNoPass {

	//public String username;
	public String name;
	public String email;	
	//public String password;
	public String street;
	public String place;
	public String country;
	//public String role;
	//public boolean isActive;

	public UserUpdateDataNoPass() {
		
	}
	
	public UserUpdateDataNoPass(String name, String email, String street, String place, String country) {
		
			//this.username = username;
			//this.password = password;
			this.name = name;
			this.email = email;
			//this.password = password;
			//this.role = role;
			this.street = street;
			this.place = place;
			this.country = country;
			//this.isActive = isActive;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(this.name) &&
			   validEmail();
	}
	
    private boolean validEmail() {
        return this.email.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

}