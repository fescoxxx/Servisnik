package ru.android.cyfral.servisnik.model.executionresult.result.getResult;

import java.io.Serializable;

public class Works implements Serializable{
    private Element element;
    private Type type;
    private Group group;
    public Element getElement ()

    {
        return element;
    }

    public void setElement (Element element)
    {
        this.element = element;
    }

    public Type getType ()
    {
        return type;
    }

    public void setType (Type type)
    {
        this.type = type;
    }

    public Group getGroup ()
    {
        return group;
    }

    public void setGroup (Group group)
    {
        this.group = group;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [element = "+element+", type = "+type+", group = "+group+"]";
    }
}
