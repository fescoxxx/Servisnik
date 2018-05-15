package ru.android.cyfral.servisnik.model.adapter;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.repairRequests.Data;

public class DataViewHolder extends ChildViewHolder implements View.OnClickListener {

    private TextView mWorks, mCityStreet, mNumberhome;
    private DataCategoryAdapter.ItemClickListener clickListener;
    private Data selectDate;
    public DataViewHolder(View itemView,
                          DataCategoryAdapter.ItemClickListener clickListener) {
        super(itemView);

        mWorks = (TextView) itemView.findViewById(R.id.works);
        mCityStreet = (TextView) itemView.findViewById(R.id.citystreet);
        mNumberhome = (TextView) itemView.findViewById(R.id.numberhome);
        itemView.setOnClickListener(this); // bind the listener
        this.clickListener = clickListener;
        this.selectDate = selectDate;
    }

    public void bind(Data data) {

        String mWorksStr ="";
        String litera="";
        String building="";
        String floor="";
        String room="";
        String dom = "";
        String entrance = "";
        String apartment = "";

        String group = "";
        String element = "";
        String type = "";

        try{
            if(!data.getAddress().getNumber().equals("")) {
                dom = "д."+data.getAddress().getNumber() + " ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getEntrance().equals("")) {
                entrance = "п."+data.getAddress().getEntrance()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getApartment().equals("")) {
                apartment =  "кв."+ data.getAddress().getApartment();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if(!data.getAddress().getLetter().equals("")) {
                litera =  "л."+data.getAddress().getLetter()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}
        try{
            if (!data.getAddress().getBuilding().equals("")) {
                building = "к."+data.getAddress().getBuilding()+ " ";
            }
       } catch (java.lang.NullPointerException ex) {}
        try{
            if(!data.getAddress().getFloor().equals("")){
                floor = "эт."+ data.getAddress().getFloor()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}
        try{
            if(!data.getAddress().getRoom().equals("")) {
                room  = "к."+data.getAddress().getRoom()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}


        try {
            if (!data.getWorks().getGroup().equals("")) {
                group = data.getWorks().getGroup();
            }
        } catch (java.lang.NullPointerException ex){}
        try {
            if (!data.getWorks().getElement().equals("")) {
                element = data.getWorks().getElement();
            }
        } catch (java.lang.NullPointerException ex){}
        try {
            if (!data.getWorks().getType().equals("")) {
                type = data.getWorks().getType();
            }
        } catch (java.lang.NullPointerException ex){}

        if (!group.equals("") & !element.equals("") & !type.equals("")) {
            mWorksStr = element + " | "+ type;
        } else {

            mWorksStr = group + " | "+element + " | "+ type;
        }


        mWorks.setText(mWorksStr.replace(" |  | ", ""));
        mCityStreet.setText(data.getAddress().getCityType()+ " "+
                data.getAddress().getCity() + " "+
                data.getAddress().getStreetType() + " " +
                data.getAddress().getStreet() + " ");
        mNumberhome.setText(
                        dom+
                        litera+
                        building+
                        entrance +
                        floor +
                        apartment +
                        room);
        selectDate = data;
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) clickListener.onClick(view, selectDate, getAdapterPosition());
    }

}