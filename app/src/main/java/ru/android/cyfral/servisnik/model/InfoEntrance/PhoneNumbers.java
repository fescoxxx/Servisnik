package ru.android.cyfral.servisnik.model.InfoEntrance;

import java.io.Serializable;

public class PhoneNumbers implements Serializable {
    private String number;

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
        return "{number = "+number+"}";
    }
}
