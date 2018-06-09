package ru.android.cyfral.servisnik.model.listwork.worksat.entrancelist;

public class Entrances {
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
        return "ClassPojo [id = "+id+", number = "+number+"]";
    }
}

