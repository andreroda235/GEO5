package pt.unl.fct.di.apdc.geo5.data;

public class ActivateAccountData {
	
	public String username;
	public String activationCode;
	
	public ActivateAccountData() {
		
	}
	
	public ActivateAccountData(String username, String activationCode) {
		
			this.username = username;
			this.activationCode = activationCode;
	}
}
