package ru.android.cyfral.servisnik.model.listwork.worksat.entrancelist;

import java.util.List;

public class Data {
    private List<Entrances> entrances;

    private Address address;

    public List<Entrances> getEntrances ()
    {
        return entrances;
    }

    public void setEntrances (List<Entrances> entrances)
    {
        this.entrances = entrances;
    }

    public Address getAddress ()
    {
        return address;
    }

    public void setAddress (Address address)
    {
        this.address = address;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [entrances = "+entrances+", address = "+address+"]";
    }
}

