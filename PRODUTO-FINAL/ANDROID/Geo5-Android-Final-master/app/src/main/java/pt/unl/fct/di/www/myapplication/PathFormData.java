package pt.unl.fct.di.www.myapplication;

public class PathFormData {

    int selectionID;
    String pathTitle;
    String pathDescription;

    public PathFormData(int id){
        selectionID = id;
    }

    public int getSelectionID() {
        return selectionID;
    }

    public void setSelectionID(int selectionID) {
        this.selectionID = selectionID;
    }

    public String getPathTitle() {
        return pathTitle;
    }

    public void setPathTitle(String pathTitle) {
        this.pathTitle = pathTitle;
    }

    public String getPathDescription() {
        return pathDescription;
    }

    public void setPathDescription(String pathDescription) {
        this.pathDescription = pathDescription;
    }
}
