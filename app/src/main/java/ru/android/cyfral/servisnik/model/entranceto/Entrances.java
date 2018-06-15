package ru.android.cyfral.servisnik.model.entranceto;

import java.io.Serializable;

public class Entrances implements Serializable {

    private String id;
    private String number;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    @Override
    public String toString()
    {
        return "{\"id\" = "+"\""+id+"\""+", \"number\" = "+"\""+number+"\""+"}";
    }
}
