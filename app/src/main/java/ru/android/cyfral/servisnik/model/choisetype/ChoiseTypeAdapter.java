package ru.android.cyfral.servisnik.model.choisetype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;

public class ChoiseTypeAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Data> objects = new ArrayList<Data>();

    public ChoiseTypeAdapter(Context context) {
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addData(Data data) {
        objects.add(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Data getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Data data = getData(position);
        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_choice_type, null);
        }
        ((TextView) view.findViewById(R.id.choice_type_text)).setText(data.getName());
        return view;
    }

    Data getData(int position) {
        return ((Data) getItem(position));
    }
}
