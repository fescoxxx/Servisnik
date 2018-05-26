package ru.android.cyfral.servisnik.model.executionresult.result.getResult;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    private List<Tmas> tmas;

    private Works works;

    public List<Tmas> getTmas ()
    {
        return tmas;
    }

    public void setTmas (List<Tmas> tmas)
    {
        this.tmas = tmas;
    }

    public Works getWorks ()
    {
        return works;
    }

    public void setWorks (Works works)
    {
        this.works = works;
    }

}
