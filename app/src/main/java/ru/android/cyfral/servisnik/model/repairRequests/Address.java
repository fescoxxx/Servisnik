package ru.android.cyfral.servisnik.model.repairRequests;

public class Address {
    private String building;
    private String floor;
    private String letter;
    private String cityType;
    private String street;
    private String apartment;
    private String number;
    private String streetType;
    private String entrance;
    private String room;
    private String city;

    public String getBuilding ()
    {
        return building;
    }

    public void setBuilding (String building)
    {
        this.building = building;
    }

    public String getFloor ()
    {
        return floor;
    }

    public void setFloor (String floor)
    {
        this.floor = floor;
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

    public String getApartment ()
    {
        return apartment;
    }

    public void setApartment (String apartment)
    {
        this.apartment = apartment;
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

    public String getEntrance ()
    {
        return entrance;
    }

    public void setEntrance (String entrance)
    {
        this.entrance = entrance;
    }

    public String getRoom ()
    {
        return room;
    }

    public void setRoom (String room)
    {
        this.room = room;
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
        return "ClassPojo [building = "+building+", floor = "+floor+", letter = "+letter+", cityType = "+cityType+", street = "+street+", apartment = "+apartment+", number = "+number+", streetType = "+streetType+", entrance = "+entrance+", room = "+room+", city = "+city+"]";
    }
}