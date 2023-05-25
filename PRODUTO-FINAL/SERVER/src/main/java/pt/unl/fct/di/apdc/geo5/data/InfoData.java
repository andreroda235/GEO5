package pt.unl.fct.di.apdc.geo5.data;

public class InfoData {

	public String title;
	public String description;
	public String mapLink;
	public String noticeLink;
	public PointerData location;
	
	public InfoData() {
		
	}
	
	public InfoData(String title, String description, String mapLink, String noticeLink, PointerData location) {
		this.title = title;
		this.description = description;
		this.mapLink = mapLink;
		this.noticeLink = noticeLink;
		this.location = location;
	}
}
