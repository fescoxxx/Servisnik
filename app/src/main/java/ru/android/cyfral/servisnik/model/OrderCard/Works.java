package ru.android.cyfral.servisnik.model.OrderCard;

import java.io.Serializable;

public class Works implements Serializable {
    private String element;
    private String type;
    private String group;

    public String getElement ()
    {
        return element;
    }

    public void setElement (String element)
    {
        this.element = element;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getGroup ()
    {
        return group;
    }

    public void setGroup (String group)
    {
        this.group = group;
    }

    @Override
    public String toString()
    {
        return "{\"element\" : "+"\""+element+"\""+", \"type\" : "+"\""+type+"\""+", \"group\" : "+"\""+group+"\""+"}";
    }
}

