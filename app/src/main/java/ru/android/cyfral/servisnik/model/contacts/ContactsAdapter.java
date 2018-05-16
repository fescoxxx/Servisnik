package ru.android.cyfral.servisnik.model.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import ru.android.cyfral.servisnik.model.OrderCard.Contacts;

public class ContactsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Contacts> objects;

    public ContactsAdapter(Context context, List<Contacts> listContacts) {
        ctx = context;
        objects = listContacts;
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




        return view;
    }
}
