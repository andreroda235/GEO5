package pt.unl.fct.di.www.myapplication;

import java.io.Serializable;

public class GeoSpotData implements Serializable {

    private PointerData location;
    private String geoSpotName;
    private String description;
    private String tags;

    public GeoSpotData(){ }

    public PointerData getLocation() {
        return location;
    }

    public String getGeoSpotName() {
        return geoSpotName;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }
}
