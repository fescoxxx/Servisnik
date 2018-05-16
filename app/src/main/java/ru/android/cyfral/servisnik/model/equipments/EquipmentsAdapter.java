package ru.android.cyfral.servisnik.model.equipments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.OrderCard.InstalledEquipments;

public class EquipmentsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<InstalledEquipments> objects;

    public EquipmentsAdapter(Context context, List<InstalledEquipments> listnsIstalledEquipments) {
        ctx = context;
        objects = listnsIstalledEquipments;
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
            view = lInflater.inflate(R.layout.row_item_equipment, parent, false);
        }
        final InstalledEquipments installedEquipments = getInstalledEquipments(position);
        ((TextView) view.findViewById(R.id.equipment_title)).setText(installedEquipments.getTitle());
        ((TextView) view.findViewById(R.id.equipment_comment)).setText(installedEquipments.getBody());
        return view;
    }

    // по позиции
    InstalledEquipments getInstalledEquipments(int position) {
        return ((InstalledEquipments) getItem(position));
    }
}
