package pt.unl.fct.di.www.myapplication;

public class CommentRouteData {

    private String routeID;
    private String content;
    private String username;
    private String commentID;

    public CommentRouteData() {

    }

    public CommentRouteData(String routeID, String content, String username, String commentID) {
        this.routeID = routeID;
        this.content = content;
        this.username = username;
        this.commentID = commentID;
    }

    public String getRouteID() {
        return routeID;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getCommentID() {
        return commentID;
    }
}
