package pt.unl.fct.di.apdc.geo5.data;

public class AddGeoSpotData {
	
	public PointerData location;
	public String geoSpotName;
	public String description;
	
	public AddGeoSpotData() {
		
	}
	
	public AddGeoSpotData(PointerData location, String username, String geoSpotName, String description) {
		this.location = location;
		this.geoSpotName = geoSpotName;
		this.description = description;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(geoSpotName);	
	}
}
