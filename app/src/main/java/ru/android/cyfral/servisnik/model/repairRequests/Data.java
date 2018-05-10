package ru.android.cyfral.servisnik.model.repairRequests;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    private String id;
    private Address address;
    private String isViewed;
    private String number;
    private String deadline;
    private List<Contacts> contacts;
    private Works works;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Address getAddress ()
    {
        return address;
    }

    public void setAddress (Address address)
    {
        this.address = address;
    }

    public String getIsViewed ()
    {
        return isViewed;
    }

    public void setIsViewed (String isViewed)
    {
        this.isViewed = isViewed;
    }

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    public String getDeadline ()
    {
        return deadline;
    }

    public void setDeadline (String deadline)
    {
        this.deadline = deadline;
    }

    public List<Contacts> getContacts ()
    {
        return contacts;
    }

    public void setContacts (List<Contacts>  contacts)
    {
        this.contacts = contacts;
    }

    public Works getWorks ()
    {
        return works;
    }

    public void setWorks (Works works)
    {
        this.works = works;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", address = "+address+", isViewed = "+isViewed+", number = "+number+", deadline = "+deadline+", contacts = "+contacts+", works = "+works+"]";
    }

}
