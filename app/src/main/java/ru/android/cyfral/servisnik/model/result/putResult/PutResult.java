package ru.android.cyfral.servisnik.model.result.putResult;

import java.io.Serializable;
import java.util.List;

public class PutResult implements Serializable {

    private Works works;
    private List<String> TMAs;
    private Long latitude;
    private Long longitude;
    private boolean isClosed;

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

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
