package pt.unl.fct.di.apdc.geo5.data;

public class CommentGeoSpotData {
	
	public String geoSpotName;
	public String content;
	public String username;
	public String commentID;

	public CommentGeoSpotData() {
		
	}
	
	public CommentGeoSpotData(String geoSpotName, String content) {
		this.geoSpotName = geoSpotName;
		this.content = content;
	}
	
	public CommentGeoSpotData(String commentID, String username, String geoSpotName, String content) {
		this.commentID = commentID;
		this.username = username;
		this.geoSpotName = geoSpotName;
		this.content = content;
	}
}
