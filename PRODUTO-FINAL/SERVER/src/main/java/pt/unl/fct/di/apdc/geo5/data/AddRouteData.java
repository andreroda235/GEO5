package pt.unl.fct.di.apdc.geo5.data;

import java.util.HashSet;
import java.util.Set;

public class AddRouteData {

	public String id;
	public String username;
    public String title;
    public String description;
    public String travelMode;
    public PointerData origin;
    public PointerData destination;
    public Set<PointerData> intermidiatePoints = new HashSet<PointerData>();
    public boolean isTracked;
    
	public AddRouteData() {
		
	}
	
	public AddRouteData(String id, PointerData origin, PointerData destination, String title, String username, String description, String travelMode, boolean isTracked) {
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.title = title;
		this.description = description;
		this.travelMode = travelMode;
		this.username = username;
	    this.isTracked = isTracked;
	}
	
	public AddRouteData(String id, PointerData origin, PointerData destination, String title, String username, 
			String description, String travelMode, Set<PointerData> intermidiatePoints, boolean isTracked) {
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.title = title;
		this.description = description;
		this.travelMode = travelMode;
		this.username = username;
		this.intermidiatePoints = intermidiatePoints;
	    this.isTracked = isTracked;
	}
	
	private boolean validField(String value) {
		return true ; //value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(title);	
	}
}
