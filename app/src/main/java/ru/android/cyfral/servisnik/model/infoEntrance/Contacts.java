package ru.android.cyfral.servisnik.model.infoEntrance;

import java.io.Serializable;
import java.util.List;

public class Contacts implements Serializable
{
    private String middleName;
    private String name;
    private String familyName;
    private String type;
    private List<PhoneNumbers> phoneNumbers;


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


    public List<PhoneNumbers> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumbers> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public String toString()
    {
        return "{\"middleName\" : "+"\""+middleName+"\""+", \"name\" : "+"\""+name+"\""+", \"familyName\" : "+"\""+familyName+"\""+", \"type\" : "+"\""+type+"\""+", \"phoneNumbers\" : "+phoneNumbers+"}";
    }

}
