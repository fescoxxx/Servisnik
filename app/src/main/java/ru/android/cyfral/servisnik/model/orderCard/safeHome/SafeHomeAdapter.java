package ru.android.cyfral.servisnik.model.orderCard.safeHome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.orderCard.Items;

public class SafeHomeAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    Context ctx;
    LayoutInflater lInflater;
    List<Items> objects = new ArrayList<Items>();

    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    public SafeHomeAdapter(Context context) {
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final Items item) {
        objects.add(item);
        notifyDataSetChanged();
    }

    public void deleteItem(final Items item) {
        objects.remove(item);
        notifyDataSetChanged();
    }
    public void addSectionHeaderItem(final Items item) {
        objects.add(item);
        sectionHeader.add(objects.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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

        int rowType = getItemViewType(position);

        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    final Items item = getItems(position);
                    convertView = lInflater.inflate(R.layout.row_item_safe_house, null);
                    ((TextView) convertView.findViewById(R.id.safe_house_title)).setText(item.getTitle());
                    ((TextView) convertView.findViewById(R.id.safe_house_body)).setText(item.getBody());
                    break;
                case TYPE_SEPARATOR:
                    final Items itemHedaer = getItems(position);
                    convertView = lInflater.inflate(R.layout.header_row_item_safe_house, null);
                    ((TextView) convertView.findViewById(R.id.header_safe_house)).setText(itemHedaer.getTitle());
                    break;
            }
        }


        return convertView;

        // View view = convertView;



      /*  if (view == null) {
            view = lInflater.inflate(R.layout.row_item_safe_house, parent, false);
        }
        final Items item = getItems(position);
        ((TextView) view.findViewById(R.id.safe_house_title)).setText(item.getTitle());
        ((TextView) view.findViewById(R.id.safe_house_body)).setText(item.getBody());*/
       // return null;
    }


    public static class ViewHolder {
        public TextView textView;
    }
    // по позиции
    Items getItems(int position) {
        return ((Items) getItem(position));
    }
}
