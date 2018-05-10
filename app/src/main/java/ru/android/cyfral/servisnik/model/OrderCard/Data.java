package ru.android.cyfral.servisnik.model.OrderCard;

import java.util.ArrayList;
import java.util.List;

public class Data
{
    private String id;
    private String entranceId;
    private List<SafeHome> safeHome;
    private Address address;
    private String isViewed;
    private List<String> tmas;
    private String number;
    private List<InstalledEquipments> installedEquipments;
    private String comment;
    private String deadline;
    private String agreedDate;
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

    public String getEntranceId ()
    {
        return entranceId;
    }

    public void setEntranceId (String entranceId)
    {
        this.entranceId = entranceId;
    }

    public List<SafeHome> getSafeHome ()
    {
        return safeHome;
    }

    public void setSafeHome (List<SafeHome> safeHome)
    {
        this.safeHome = safeHome;
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

    public List<String> getTmas ()
    {
        return tmas;
    }

    public void setTmas (List<String> tmas)
    {
        this.tmas = tmas;
    }

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    public List<InstalledEquipments> getInstalledEquipments ()
    {
        return installedEquipments;
    }

    public void setInstalledEquipments (List<InstalledEquipments> installedEquipments)
    {
        this.installedEquipments = installedEquipments;
    }

    public String getComment ()
    {
        return comment;
    }

    public void setComment (String comment)
    {
        this.comment = comment;
    }

    public String getDeadline ()
    {
        return deadline;
    }

    public void setDeadline (String deadline)
    {
        this.deadline = deadline;
    }

    public String getAgreedDate ()
{
    return agreedDate;
}

    public void setAgreedDate (String agreedDate)
    {
        this.agreedDate = agreedDate;
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
        final char dm = (char) 34;
        List<String> newTmas = new ArrayList<>();
        for (int i =0; i<tmas.size(); i++) {
            newTmas.add(dm+tmas.get(i)+dm);
        }
        setTmas(newTmas);

        return "{\"id\": "+"\""+id+"\""+", \"entranceId\" : "+"\""+entranceId+"\""+", \"safeHome\" : "+safeHome+", \"address\" : "+address+", \"isViewed\" : "+isViewed+", \"tmas\" : "+tmas+", \"number\" : "+number+", \"installedEquipments\" : "+installedEquipments+", \"comment\" : "+"\""+comment+"\""+", \"deadline\" : "+"\""+deadline+"\""+", \"agreedDate\" : "+"\""+agreedDate+"\""+", \"contacts\" : "+contacts+", \"works\" : "+works+"}";
    }
}