package ru.android.cyfral.servisnik.model.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
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

        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_contacts, parent, false);
        }
        Contacts contacts = getProduct(position);
        ((TextView) view.findViewById(R.id.contacts_fio)).setText(contacts.getFamilyName()
                + " " +contacts.getName() + " "+contacts.getMiddleName());
        ((TextView) view.findViewById(R.id.contacts_type)).setText(contacts.getType());
        return view;
    }

    // товар по позиции
    Contacts getProduct(int position) {
        return ((Contacts) getItem(position));
    }
}
