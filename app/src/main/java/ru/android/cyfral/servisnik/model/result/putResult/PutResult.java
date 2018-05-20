package ru.android.cyfral.servisnik.model.result.putResult;

import java.io.Serializable;
import java.util.List;

public class PutResult implements Serializable {

    private Works works;
    private List<String> TMAs;
    private String latitude;
    private String longitude;
    private String isClosed;

    public Works getWorks() {
        return works;
    }

    public void setWorks(Works works) {
        this.works = works;
    }

    public List<String> getTMAs() {
        return TMAs;
    }

    public void setTMAs(List<String> TMAs) {
        this.TMAs = TMAs;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String isClosed() {
        return isClosed;
    }

    public void setClosed(String closed) {
        isClosed = closed;
    }
}
