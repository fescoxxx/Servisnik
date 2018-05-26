package ru.android.cyfral.servisnik.model.executionresult.choiseelement;

import java.io.Serializable;

public class Data implements Serializable {
    private String id;

    private String name;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }
}
