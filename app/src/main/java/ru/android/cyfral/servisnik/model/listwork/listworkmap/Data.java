package ru.android.cyfral.servisnik.model.listwork.listworkmap;

public class Data {
    private String repairRequestDeadline;

    private String longitude;

    private String latitude;

    private String houseId;

    public String getRepairRequestDeadline ()
    {
        return repairRequestDeadline;
    }

    public void setRepairRequestDeadline (String repairRequestDeadline)
    {
        this.repairRequestDeadline = repairRequestDeadline;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getHouseId ()
    {
        return houseId;
    }

    public void setHouseId (String houseId)
    {
        this.houseId = houseId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [repairRequestDeadline = "+repairRequestDeadline+", longitude = "+longitude+", latitude = "+latitude+", houseId = "+houseId+"]";
    }
}