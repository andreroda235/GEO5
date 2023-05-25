package pt.unl.fct.di.www.myapplication;

import java.io.Serializable;

public class PointerData implements Serializable {

    public String lat;
    public String lng;

    public PointerData() {

    }

    public PointerData(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

}
