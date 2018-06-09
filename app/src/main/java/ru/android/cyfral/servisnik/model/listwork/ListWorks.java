package ru.android.cyfral.servisnik.model.listwork;

import java.util.List;

public class ListWorks {
    private String id;

    private Errors errors;

    private List<Data> data;

    private String isSuccess;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Errors getErrors ()
    {
        return errors;
    }

    public void setErrors (Errors errors)
    {
        this.errors = errors;
    }

    public List<Data> getData ()
    {
        return data;
    }

    public void setData (List<Data> data)
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
        return "ClassPojo [id = "+id+", errors = "+errors+", data = "+data+", isSuccess = "+isSuccess+"]";
    }
}
