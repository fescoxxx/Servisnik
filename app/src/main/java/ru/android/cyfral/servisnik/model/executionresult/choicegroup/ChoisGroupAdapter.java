package ru.android.cyfral.servisnik.model.executionresult.choicegroup;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.GetResult;

public class ChoisGroupAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Data> objects = new ArrayList<Data>();
    GetResult currentResult;

    public ChoisGroupAdapter(Context context, GetResult getResult) {
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentResult = getResult;
    }

    public void addData(final Data data) {
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
            view = lInflater.inflate(R.layout.row_item_choice_group, null);
        }

        try {
            if (currentResult.getData().getWorks().getGroup().getName().equals(data.getName())) {
                TextView textView = (TextView) view.findViewById(R.id.choice_group_text);
                textView.setText(data.getName());
                textView.setTextColor(Color.parseColor("#6288AD"));
             } else {
                ((TextView) view.findViewById(R.id.choice_group_text)).setText(data.getName());
             }
        } catch (NullPointerException ex) {
                ((TextView) view.findViewById(R.id.choice_group_text)).setText(data.getName());
        }

        return view;
    }
    Data getData(int position) {
        return ((Data) getItem(position));
    }
}
