package ru.android.cyfral.servisnik.model.orderCard.adapter;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.repairRequests.Data;
public class RepairRequestAdapter extends RecyclerView.Adapter<RepairRequestAdapter.Holder> {


    private final RepairRequestClickListener mListener;
    private List<Data> mData;

    public RepairRequestAdapter(RepairRequestClickListener listener) {
        mData = new ArrayList<Data>();
        mListener = listener;
    }


    public void clearData(){
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_repair_request_from_search, parent, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
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
            if (!mData.get(position).getAddress().getApartment().equals("")
                    &!mData.get(position).getAddress().getApartment().equals("null")) {
                apartment = "кв."+mData.get(position).getAddress().getApartment()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!mData.get(position).getAddress().getEntrance().equals("")
                    &!mData.get(position).getAddress().getEntrance().equals("null")) {
                entrance = "п."+mData.get(position).getAddress().getEntrance()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}


        try{
            if (!mData.get(position).getAddress().getNumber().equals("")
                    &!mData.get(position).getAddress().getNumber().equals("null")) {
                dom = "д."+mData.get(position).getAddress().getNumber() +" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!mData.get(position).getAddress().getLetter().equals("")
                    &!mData.get(position).getAddress().getLetter().equals("null")) {
                litera = "л."+mData.get(position).getAddress().getLetter() +" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!mData.get(position).getAddress().getBuilding().equals("")
                    &!mData.get(position).getAddress().getBuilding().equals("null")) {
                building = "к." + mData.get(position).getAddress().getBuilding()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!mData.get(position).getAddress().getFloor().equals("")
                    &!mData.get(position).getAddress().getFloor().equals("null")) {
                floor = "эт."+ mData.get(position).getAddress().getFloor()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!mData.get(position).getAddress().getRoom().equals("")
                    &!mData.get(position).getAddress().getRoom().equals("null")) {
                room  = "к."+mData.get(position).getAddress().getRoom()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try {
            if (!mData.get(position).getWorks().getGroup().equals("")) {
                group = mData.get(position).getWorks().getGroup();
            }
        } catch (java.lang.NullPointerException ex){}
        try {
            if (!mData.get(position).getWorks().getElement().equals("")) {
                element = mData.get(position).getWorks().getElement();
            }
        } catch (java.lang.NullPointerException ex){}
        try {
            if (!mData.get(position).getWorks().getType().equals("")) {
                type = mData.get(position).getWorks().getType();
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

        holder.mWorks.setText(mWorksStr.replace(" |  | ", ""));
        holder.mCityStreet.setText(mData.get(position).getAddress().getCity()
                + " " + mData.get(position).getAddress().getCityType()
                + " " + mData.get(position).getAddress().getStreet()
                + " " + mData.get(position).getAddress().getStreetType());
        holder.mNumberhome.setText(
                        dom+
                        litera+
                        building+
                        entrance +
                        floor +
                        apartment +
                        room
        );

        if(mData.get(position).getIsViewed().equals("false")) {
            holder.mLine_is_view.setVisibility(View.VISIBLE);
        } else if(mData.get(position).getIsViewed().equals("true")) {
            holder.mLine_is_view.setVisibility(View.INVISIBLE);
        }


        Date dateToday = new Date();
        Date deadLine = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
        SimpleDateFormat format_data = new SimpleDateFormat("dd.MM.yyyy");
        try {
            deadLine = format.parse(mData.get(position).getDeadline());
            if (deadLine.before(format.parse(format.format(dateToday)))) {
                holder.mDateDeadLine.setText("Просрочена");
                holder.mDateDeadLine.setTextColor(Color.parseColor("#000000"));
            } else if (deadLine.equals(format.parse(format.format(dateToday)))){
                holder.mDateDeadLine.setText("Сегодня");
                holder.mDateDeadLine.setTextColor(Color.parseColor("#CF1D1D"));
            } else {
                holder.mDateDeadLine.setText(format_data.format(deadLine));
                holder.mDateDeadLine.setTextColor(Color.parseColor("#4F7AB4"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(Data data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    public void  allAddData(List<Data> dataList) {
        mData.clear();
        mData = dataList;
        notifyDataSetChanged();
    }

    public Data getSelectedData(int position) {
        return mData.get(position);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mWorks, mCityStreet, mNumberhome, mDateDeadLine;
        private View mLine_is_view;
        public Holder(View itemView) {
            super(itemView);
            mWorks = (TextView) itemView.findViewById(R.id.works);
            mCityStreet = (TextView) itemView.findViewById(R.id.citystreet);
            mNumberhome = (TextView) itemView.findViewById(R.id.numberhome);
            mDateDeadLine = (TextView)  itemView.findViewById(R.id.date_deadline_search);
            mLine_is_view = (View)  itemView.findViewById(R.id.line_is_view);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            mListener.onClick(getLayoutPosition());
        }
    }
    public interface RepairRequestClickListener {
        void onClick(int position);
    }
}

