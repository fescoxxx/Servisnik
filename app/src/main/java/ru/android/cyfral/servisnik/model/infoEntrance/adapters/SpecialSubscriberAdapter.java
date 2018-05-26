package ru.android.cyfral.servisnik.model.infoEntrance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.infoEntrance.SpecialApartments;

public class SpecialSubscriberAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<SpecialApartments> objects;

    public SpecialSubscriberAdapter(Context context,  List<SpecialApartments> listSpecialApartments) {
        ctx = context;
        objects = listSpecialApartments;
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

        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_special_subscriber, parent, false);
        }
        SpecialApartments specialApartments = getSpecialApartments(position);

        ((TextView) view.findViewById(R.id.special_subscriber_title)).setText(specialApartments.getTitle());
        ((TextView) view.findViewById(R.id.special_subscriber_comment)).setText(specialApartments.getBody());

        return view;
    }

    // по позиции
    SpecialApartments getSpecialApartments(int position) {
        return objects.get(position);
    }
}
