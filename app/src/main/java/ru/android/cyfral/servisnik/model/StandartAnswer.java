package ru.android.cyfral.servisnik.model;

public class StandartAnswer {

    private String data;
    private Errors errors;
    private String isSuccess;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }
}
