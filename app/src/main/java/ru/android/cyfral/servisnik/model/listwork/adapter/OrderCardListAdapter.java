package ru.android.cyfral.servisnik.model.listwork.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist.Data;

public class OrderCardListAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Data> objects;

    public OrderCardListAdapter(Context context, List<Data> listData) {
        ctx = context;
        objects = listData;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView mWorks, mCityStreet, mNumberhome, mDateDeadLine;
        View mLine_is_view;

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_repair_request_from_search, parent, false);
        }

        final Data data = getData(position);
        mWorks = (TextView) view.findViewById(R.id.works);
        mCityStreet = (TextView) view.findViewById(R.id.citystreet);
        mNumberhome = (TextView) view.findViewById(R.id.numberhome);
        mDateDeadLine = (TextView)  view.findViewById(R.id.date_deadline_search);
        mLine_is_view = (View)  view.findViewById(R.id.line_is_view);

        String city         = "";
        String cityType     = "";
        String street       = "";
        String streetType   = "";
        String number       = "";
        String letter       = "";
        String building     = "";
        String entrance     = "";
        String floor        = "";
        String apartment    = "";
        String room         = "";

        String group        = "";
        String element      = "";
        String type         = "";

        String mWorksStr    = "";

        try{
            if (!data.getAddress().getCity().equals("")
                    &!data.getAddress().getCity().equals("null")) {
                city = data.getAddress().getCity();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getCityType().equals("")
                    &!data.getAddress().getCityType().equals("null")) {
                cityType = data.getAddress().getCityType();
            }
        } catch (java.lang.NullPointerException ex) {}


        try{
            if (!data.getAddress().getStreet().equals("")
                    &!data.getAddress().getStreet().equals("null")) {
                street = data.getAddress().getStreet();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getStreetType().equals("")
                    &!data.getAddress().getStreetType().equals("null")) {
                streetType = data.getAddress().getStreetType();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getNumber().equals("")
                    &!data.getAddress().getNumber().equals("null")) {
                number = "д."+data.getAddress().getNumber()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getLetter().equals("")
                    &!data.getAddress().getLetter().equals("null")) {
                letter = "л."+data.getAddress().getLetter()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getBuilding().equals("")
                    &!data.getAddress().getBuilding().equals("null")) {
                building = "к" +data.getAddress().getBuilding()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getEntrance().equals("")
                    &!data.getAddress().getEntrance().equals("null")) {
                entrance ="п."+ data.getAddress().getEntrance()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getFloor().equals("")
                    &!data.getAddress().getFloor().equals("null")) {
                floor = "эт."+ data.getAddress().getFloor()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getApartment().equals("")
                    &!data.getAddress().getApartment().equals("null")) {
                apartment = "кв."+data.getAddress().getApartment()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!data.getAddress().getRoom().equals("")
                    &!data.getAddress().getRoom().equals("null")) {
                room = "к."+data.getAddress().getRoom()+" ";
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
            if (type.equals("")) {
                mWorksStr = group + " | "+element;
            }
            if (type.equals("") & element.equals("")) {
                mWorksStr = group;
            }
        }
        mWorks.setText(mWorksStr.replace(" |  | ", ""));
        mCityStreet.setText(cityType + " "+
                city + " "+
                streetType + " " +
                street + " ");

        mNumberhome.setText(
                        number+
                        letter+
                        building+
                        entrance +
                        floor +
                        apartment +
                        room
        );

        if(data.getIsViewed().equals("false")) {
            mLine_is_view.setVisibility(View.VISIBLE);
        } else if(data.getIsViewed().equals("true")) {
            mLine_is_view.setVisibility(View.INVISIBLE);
        }

        Date dateToday = new Date();
        Date deadLine = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
        SimpleDateFormat format_data = new SimpleDateFormat("dd.MM.yyyy");
        try {
            deadLine = format.parse(data.getDeadline());
            if (deadLine.before(format.parse(format.format(dateToday)))) {
                mDateDeadLine.setText("Просрочена");
                mDateDeadLine.setTextColor(Color.parseColor("#000000"));
            } else if (deadLine.equals(format.parse(format.format(dateToday)))){
                mDateDeadLine.setText("Сегодня");
                mDateDeadLine.setTextColor(Color.parseColor("#CF1D1D"));
            } else {
                mDateDeadLine.setText(format_data.format(deadLine));
                mDateDeadLine.setTextColor(Color.parseColor("#4F7AB4"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    // по позиции
    Data getData(int position) {
        return ((Data) getItem(position));
    }
}
