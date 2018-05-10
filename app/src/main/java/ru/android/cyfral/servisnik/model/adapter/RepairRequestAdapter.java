package ru.android.cyfral.servisnik.model.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
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
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_repair_request_child, parent, false);

        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String mWorksStr ="";
        try {
            if (!mData.get(position).getWorks().getElement().equals("")) {
                mWorksStr = mData.get(position).getWorks().getElement();
            }
        } catch (java.lang.NullPointerException ex){
            mWorksStr = "";
        }
        try {
            if (!mData.get(position).getWorks().getGroup().equals("")) {
                mWorksStr = mWorksStr + " | " + mData.get(position).getWorks().getGroup();
            }

        } catch (java.lang.NullPointerException ex){}
        try {
            if (!mData.get(position).getWorks().getType().equals("")) {
                mWorksStr = mWorksStr + " | " + mData.get(position).getWorks().getType();
            }
        } catch (java.lang.NullPointerException ex){}

        holder.mWorks.setText(mWorksStr);
        holder.mCityStreet.setText(mData.get(position).getAddress().getCityType()+ " "+
                mData.get(position).getAddress().getCity() + " "+
                mData.get(position).getAddress().getStreetType() + " " +
                mData.get(position).getAddress().getStreet() + " ");
        holder.mNumberhome.setText("д."+ mData.get(position).getAddress().getNumber()+ " п."+
                mData.get(position).getAddress().getEntrance() + " кв."+
                mData.get(position).getAddress().getApartment()
        );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(Data data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    public Data getSelectedData(int position) {
        return mData.get(position);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mWorks, mCityStreet, mNumberhome;

        public Holder(View itemView) {
            super(itemView);
            mWorks = (TextView) itemView.findViewById(R.id.works);
            mCityStreet = (TextView) itemView.findViewById(R.id.citystreet);
            mNumberhome = (TextView) itemView.findViewById(R.id.numberhome);
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

