package ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist;

public class Works {
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
        return "ClassPojo [element = "+element+", type = "+type+", group = "+group+"]";
    }
}
