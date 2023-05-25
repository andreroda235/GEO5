package pt.unl.fct.di.apdc.geo5.data;

import java.util.UUID;

public class AuthToken {
	
	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h
	
	public String username;
	public String tokenID;
	public String role;
	public long creationData;
	public long expirationData;

	public AuthToken() {
		
	}
	
	public AuthToken(String username, String role) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
		this.role = role;
	}
	
    public AuthToken(String user, String id, long creationData, long expirationData, String role) {
        this.username = user;
        this.tokenID = id;
        this.creationData = creationData;
        this.expirationData = expirationData;
        this.role = role;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthToken))
            return false;
        AuthToken t = (AuthToken) o;
        if (!t.username.equals(this.username))
            return false;
        if (!t.tokenID.equals(this.tokenID))
            return false;
        if (t.creationData != this.creationData)
            return false;
        if (t.expirationData != this.expirationData)
            return false;
        return true;
    }
    
    public boolean validToken() {
        return validField(this.username) &&
                validField(this.tokenID) &&
                validData() &&
                !expiredToken();
    }
   
    private boolean validField(String value) {
        return value != null && !value.equals("");
    }
    
    private boolean validData() {
        return this.creationData < this.expirationData;
    }
    
    private boolean expiredToken() {
    	return System.currentTimeMillis() >= this.expirationData;
    }
}
