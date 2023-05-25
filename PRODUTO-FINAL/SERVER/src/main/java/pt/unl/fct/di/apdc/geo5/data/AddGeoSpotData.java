package pt.unl.fct.di.apdc.geo5.data;

public class AddGeoSpotData {
	
	public PointerData location;
	public String geoSpotName;
	public String description;
	public String tags;
	
	public AddGeoSpotData() {
		
	}
	
	public AddGeoSpotData(PointerData location, String geoSpotName, String description, String tags) {
		this.location = location;
		this.geoSpotName = geoSpotName;
		this.description = description;
		this.tags = tags;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(geoSpotName);	
	}
}
