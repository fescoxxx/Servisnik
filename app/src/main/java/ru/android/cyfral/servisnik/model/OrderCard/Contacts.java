package ru.android.cyfral.servisnik.model.OrderCard;

import java.util.ArrayList;
import java.util.List;

public class Contacts
{
    private String middleName;
    private String name;
    private String familyName;
    private String type;
    private List<String> phones;

    public String getMiddleName ()
    {
        return middleName;
    }

    public void setMiddleName (String middleName)
    {
        this.middleName = middleName;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFamilyName ()
    {
        return familyName;
    }

    public void setFamilyName (String familyName)
    {
        this.familyName = familyName;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public List<String> getPhones ()
    {
        return phones;
    }

    public void setPhones (List<String>  phones)
    {
        this.phones = phones;
    }

    @Override
    public String toString()
    {
        final char dm = (char) 34;
        List<String> newPhones = new ArrayList<>();
        for (int i =0; i<phones.size(); i++) {
            newPhones.add(dm+phones.get(i)+dm);
        }
        setPhones(newPhones);

        return "{\"middleName\" : "+"\""+middleName+"\""+", \"name\" : "+"\""+name+"\""+", \"familyName\" : "+"\""+familyName+"\""+", \"type\" : "+"\""+type+"\""+", \"phones\" : "+phones+"}";
    }
}
