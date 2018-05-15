package ru.android.cyfral.servisnik.model.tmc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;

public class TmcAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<String> objects;

    public TmcAdapter(Context context, List<String> listTmc) {
        ctx = context;
        objects = listTmc;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //колво элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public String getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_tmc, parent, false);
        }
        String tmc = getTMC(position);
        ((TextView) view.findViewById(R.id.tmc_title)).setText(tmc);
        return view;
    }
    // тмц по позиции
    String getTMC(int position) {
        return objects.get(position);
    }
}
