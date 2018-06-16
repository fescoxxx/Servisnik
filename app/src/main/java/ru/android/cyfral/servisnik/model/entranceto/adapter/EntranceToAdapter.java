package ru.android.cyfral.servisnik.model.entranceto.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import  ru.android.cyfral.servisnik.model.entranceto.Data;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;

public class EntranceToAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    List<Data> objects;

    public EntranceToAdapter(Context context, List<Data> listData) {
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
        View view = convertView;


        TextView strit_atribut, house_atribut;
        LinearLayout linearlayout_entrance_to;
        final Data data = getData(position);
        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_entrance_to, parent, false);

        }

        strit_atribut = (TextView) view.findViewById(R.id.strit_atribut);
        house_atribut = (TextView) view.findViewById(R.id.house_atribut);
        linearlayout_entrance_to = (LinearLayout) view.findViewById(R.id.linearlayout_entrance_to);

        linearlayout_entrance_to.removeAllViews();

        for(int i =0; i<data.getEntrances().size(); i++)
        {
            View content = LayoutInflater.from(ctx).inflate(R.layout.row_item_works_at, null);
            Button btn = (Button) content.findViewById (R.id.button_entrance);
            btn.setText(data.getEntrances().get(i).getNumber());
            btn.setTag(data.getEntrances().get(i).getId());
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
            linearlayout_entrance_to.addView(content);
        }

        strit_atribut.setText(data.getAddress().getStreet() + " "+data.getAddress().getStreetType());
        house_atribut.setText("д." +data.getAddress().getNumber());

        return view;
    }

    // по позиции
    public Data getData(int position) {
        return ((Data) getItem(position));
    }
}
