package ru.android.cyfral.servisnik.model.orderCard;

import java.io.Serializable;

public class InstalledEquipments implements Serializable {
    private String body;
    private String title;
    public String getBody ()
    {
        return body;
    }

    public void setBody (String body)
    {
        this.body = body;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    @Override
    public String toString()
    {
        return "{\"body\" : "+"\""+body+"\""+", \"title\" : "+"\""+title+"\""+"}";
    }
}