package ru.android.cyfral.servisnik.model.executionresult.result.putResult;

import java.io.Serializable;

public class Works implements Serializable {

    private String groupID;
    private String elementID;
    private String typeID;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getElementID() {
        return elementID;
    }

    public void setElementID(String elementID) {
        this.elementID = elementID;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }
}
