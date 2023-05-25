package pt.unl.fct.di.www.myapplication;

public class CommentData {

    private String geoSpotName;
    private String content;
    private String username;
    private String commentID;

    public CommentData() {

    }

    public CommentData(String geoSpotName, String content, String username, String commentID) {
        this.geoSpotName = geoSpotName;
        this.content = content;
        this.username = username;
        this.commentID = commentID;
    }

    public String getGeoSpotName() {
        return geoSpotName;
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
