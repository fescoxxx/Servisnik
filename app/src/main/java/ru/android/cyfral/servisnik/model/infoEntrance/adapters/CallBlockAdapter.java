package ru.android.cyfral.servisnik.model.infoEntrance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.infoEntrance.CallingDevice;

public class CallBlockAdapter  extends BaseAdapter{
    Context ctx;
    LayoutInflater lInflater;
    List<CallingDevice> objects;

    public CallBlockAdapter(Context context,  List<CallingDevice> listCallBlock) {
        ctx = context;
        objects = listCallBlock;
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
            view = lInflater.inflate(R.layout.row_item_call_block, parent, false);
        }
        CallingDevice callingDevice = getCallingDevice(position);

        ((TextView) view.findViewById(R.id.call_block_title)).setText(callingDevice.getTitle());
        ((TextView) view.findViewById(R.id.call_block_comment)).setText(callingDevice.getBody());

        return view;
    }

    // по позиции
    CallingDevice getCallingDevice(int position) {
        return objects.get(position);
    }
}
