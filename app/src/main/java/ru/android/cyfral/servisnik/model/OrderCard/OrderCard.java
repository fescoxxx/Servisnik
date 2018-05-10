package ru.android.cyfral.servisnik.model.OrderCard;

public class OrderCard {
    private Errors errors;
    private Data data;
    private String isSuccess;

    public Errors getErrors ()
    {
        return errors;
    }

    public void setErrors (Errors errors)
    {
        this.errors = errors;
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getIsSuccess ()
    {
        return isSuccess;
    }

    public void setIsSuccess (String isSuccess)
    {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString()
    {
        return "{\"errors\": "+errors+", \"data\": "+data+", \"isSuccess\" : "+isSuccess+"}";
    }
}