package ru.android.cyfral.servisnik.model.entranceto.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.entranceto.Data;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;

public class EntranceToRecycleAdapter extends RecyclerView.Adapter<EntranceToRecycleAdapter.Holder> {
    private final EntranceToClickListener mListener;
    private List<Data> mData;
    private Context ctx;

    public EntranceToRecycleAdapter(EntranceToClickListener listener, Context context) {
        mData = new ArrayList<Data>();
        mListener = listener;
        ctx = context;
    }


    public void clearData(){
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_entrance_to, parent, false);
        return new Holder(row);
    }

    public void  allAddData(List<Data> dataList) {
        mData.clear();
        mData = dataList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(EntranceToRecycleAdapter.Holder holder, int position) {
        holder.linearlayout_entrance_to.removeAllViews();
        for(int i =0; i<mData.get(position).getEntrances().size(); i++)
        {
            View content = LayoutInflater.from(ctx).inflate(R.layout.row_item_works_at, null);
            Button btn = (Button) content.findViewById (R.id.button_entrance);
            btn.setText(mData.get(position).getEntrances().get(i).getNumber());
            btn.setTag(mData.get(position).getEntrances().get(i).getId());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag() != null) {
                        OrderCard currentOrderCard = new OrderCard();
                        ru.android.cyfral.servisnik.model.orderCard.Data data = new ru.android.cyfral.servisnik.model.orderCard.Data();
                        data.setEntranceId(view.getTag().toString());
                        currentOrderCard.setData(data);
                        Intent intent = new Intent("ru.android.cyfral.servisnik.infoentrance");
                        intent.putExtra("ordercard", currentOrderCard);
                        ctx.startActivity(intent);

                    };
                }
            });
            holder.linearlayout_entrance_to.addView(content);
        }

        holder.strit_atribut.setText(mData.get(position).getAddress().getStreet() + " "+mData.get(position).getAddress().getStreetType());
        holder.house_atribut.setText("д." +mData.get(position).getAddress().getNumber());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public Data getSelectedData(int position) {
        return mData.get(position);
    }

    public class Holder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        TextView strit_atribut, house_atribut;
        LinearLayout linearlayout_entrance_to;
        public Holder(View itemView) {
            super(itemView);
            strit_atribut = (TextView) itemView.findViewById(R.id.strit_atribut);
            house_atribut = (TextView) itemView.findViewById(R.id.house_atribut);
            linearlayout_entrance_to = (LinearLayout) itemView.findViewById(R.id.linearlayout_entrance_to);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(getLayoutPosition());
        }
    }
    public interface EntranceToClickListener {
        void onClick(int position);
    }
}
