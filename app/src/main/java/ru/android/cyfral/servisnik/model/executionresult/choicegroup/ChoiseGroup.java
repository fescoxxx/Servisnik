package ru.android.cyfral.servisnik.model.executionresult.choicegroup;

import java.io.Serializable;
import java.util.List;

public class ChoiseGroup implements Serializable {
    private Errors errors;
    private List<Data> data;
    private String isSuccess;

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

    public void setData ( List<Data> data)
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

}
