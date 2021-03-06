package ru.android.cyfral.servisnik.model.listwork.worksat.entrancelist;

public class Address {
    private String building;

    private String letter;

    private String cityType;

    private String street;

    private String number;

    private String streetType;

    private String city;

    public String getBuilding ()
    {
        return building;
    }

    public void setBuilding (String building)
    {
        this.building = building;
    }

    public String getLetter ()
    {
        return letter;
    }

    public void setLetter (String letter)
    {
        this.letter = letter;
    }

    public String getCityType ()
    {
        return cityType;
    }

    public void setCityType (String cityType)
    {
        this.cityType = cityType;
    }

    public String getStreet ()
    {
        return street;
    }

    public void setStreet (String street)
    {
        this.street = street;
    }

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    public String getStreetType ()
    {
        return streetType;
    }

    public void setStreetType (String streetType)
    {
        this.streetType = streetType;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [building = "+building+", letter = "+letter+", cityType = "+cityType+", street = "+street+", number = "+number+", streetType = "+streetType+", city = "+city+"]";
    }
}