package ru.android.cyfral.servisnik.model.executionresult.choicegroup;

import java.io.Serializable;

public class Errors implements Serializable {

    private String code;
    private String message;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
