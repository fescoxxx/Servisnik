package ru.android.cyfral.servisnik.model.OrderCard;

import java.util.List;

public class SafeHome {
    private String title;
    private List<Items> items;
    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public List<Items> getItems ()
    {
        return items;
    }

    public void setItems (List<Items>  items)
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "{\"title\" : "+"\""+title+"\""+", \"items\" : "+items+"}";
    }
}