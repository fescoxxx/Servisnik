package ru.android.cyfral.servisnik.model.InfoEntrance;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    private String id;

    private List<VideoService> videoService;

    private String lastPprDate;

    private Address address;

    private List<SpecialApartments> specialApartments;

    private List<Contacts> contacts;

    private List<CallingDevice> callingDevice;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public List<VideoService> getVideoService ()
    {
        return videoService;
    }

    public void setVideoService (List<VideoService> videoService)
    {
        this.videoService = videoService;
    }

    public String getLastPprDate ()
    {
        return lastPprDate;
    }

    public void setLastPprDate (String lastPprDate)
    {
        this.lastPprDate = lastPprDate;
    }

    public Address getAddress ()
    {
        return address;
    }

    public void setAddress (Address address)
    {
        this.address = address;
    }

    public List<SpecialApartments> getSpecialApartments ()
    {
        return specialApartments;
    }

    public void setSpecialApartments (List<SpecialApartments> specialApartments)
    {
        this.specialApartments = specialApartments;
    }

    public List<Contacts> getContacts ()
    {
        return contacts;
    }

    public void setContacts (List<Contacts> contacts)
    {
        this.contacts = contacts;
    }

    public List<CallingDevice> getCallingDevice ()
    {
        return callingDevice;
    }

    public void setCallingDevice (List<CallingDevice> callingDevice)
    {
        this.callingDevice = callingDevice;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", videoService = "+videoService+", lastPprDate = "+lastPprDate+", address = "+address+", specialApartments = "+specialApartments+", contacts = "+contacts+", callingDevice = "+callingDevice+"]";
    }
}
