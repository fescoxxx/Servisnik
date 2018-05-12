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
        String mCityStreetStr="";
        String mNumberhomeStr="";
        String litera="";
        String building="";
        String floor="";
        String room="";

        try{
            litera = data.getAddress().getLetter();
        } catch (java.lang.NullPointerException ex) {}
        try{
            building = data.getAddress().getBuilding();
        } catch (java.lang.NullPointerException ex) {}
        try{
            floor = " эт. " +data.getAddress().getFloor();
        } catch (java.lang.NullPointerException ex) {}
        try{
            room  = " ком. "+data.getAddress().getRoom();
        } catch (java.lang.NullPointerException ex) {}

        try {
            if (!data.getWorks().getElement().equals("")) {
                mWorksStr = data.getWorks().getElement();
            }
        } catch (java.lang.NullPointerException ex){
            mWorksStr = "";
        }
        try {
            if (!data.getWorks().getGroup().equals("")) {
                mWorksStr = mWorksStr + " | " + data.getWorks().getGroup();
            }

        } catch (java.lang.NullPointerException ex){}
        try {
            if (!data.getWorks().getType().equals("")) {
                mWorksStr = mWorksStr + " | " + data.getWorks().getType();
            }
        } catch (java.lang.NullPointerException ex){}



        mWorks.setText(mWorksStr);
        mCityStreet.setText(data.getAddress().getCityType()+ " "+
                data.getAddress().getCity() + " "+
                data.getAddress().getStreetType() + " " +
                data.getAddress().getStreet() + " ");
        mNumberhome.setText("д."+ data.getAddress().getNumber()+ litera+" "+ " п."+
                        data.getAddress().getEntrance() + " кв."+
                        data.getAddress().getApartment() + " "+building + floor + room);

        selectDate = data;
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) clickListener.onClick(view, selectDate, getAdapterPosition());
    }

}