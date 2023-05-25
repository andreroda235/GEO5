package pt.unl.fct.di.www.myapplication;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteData implements Serializable {

    //Route Data
    private String path_id;
    private String title;
    private List<Marker> markers;
    private Polyline path;
    private String description;
    private String travelMode;
    private boolean tracked;
    private String pathLength;
    private String duration;
    private String locations;
    private String owner;

    //Route Views


    public RouteData(String path_id, String title, List<Marker> markers, String description, String travelmMode, boolean isTracked, String owner){
        this.path_id = path_id;
        this.title = title;
        this.markers = new ArrayList<>(markers);
        this.description = description;
        this.travelMode = travelmMode;
        tracked = isTracked;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
    public String getPath_id() {
        return path_id;
    }

    public String getTitle() {
        return title;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public Polyline getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setPath(Polyline path) {
        this.path = path;
    }

    public boolean isTracked() {
        return tracked;
    }

    public String getPathLength() {
        return pathLength;
    }

    public void setPathLength(String pathLength) {
        this.pathLength = pathLength;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }
}
