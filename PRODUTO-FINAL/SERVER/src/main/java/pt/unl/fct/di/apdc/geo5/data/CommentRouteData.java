package pt.unl.fct.di.apdc.geo5.data;

public class CommentRouteData {
	
	public String routeID;
	public String content;
	public String username;
	public String commentID;
	
	public CommentRouteData() {
		
	}
	
	public CommentRouteData(String routeID, String content) {
		this.routeID = routeID;
		this.content = content;
	}

	public CommentRouteData(String commentID, String username, String routeID, String content) {
		this.commentID = commentID;
		this.username = username;
		this.routeID = routeID;
		this.content = content;
	}
}
