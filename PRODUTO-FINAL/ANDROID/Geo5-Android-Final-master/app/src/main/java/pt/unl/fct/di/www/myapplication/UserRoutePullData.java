package pt.unl.fct.di.www.myapplication;

import java.util.HashSet;
import java.util.Set;

public class UserRoutePullData {
    public String id;
    public String username;
    public String title;
    public String description;
    public String travelMode;
    public PointerData origin;
    public PointerData destination;
    public Set<PointerData> intermidiatePoints = new HashSet<>();
    public boolean isTracked;

    public UserRoutePullData(){}

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public PointerData getOrigin() {
        return origin;
    }

    public PointerData getDestination() {
        return destination;
    }

    public Set<PointerData> getIntermidiatePoints() {
        return intermidiatePoints;
    }

    public boolean isTracked() {
        return isTracked;
    }
}
