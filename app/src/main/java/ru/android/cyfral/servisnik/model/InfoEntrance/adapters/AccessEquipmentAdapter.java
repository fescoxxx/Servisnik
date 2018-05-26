package ru.android.cyfral.servisnik.model.InfoEntrance.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.InfoEntrance.Contacts;
import ru.android.cyfral.servisnik.model.InfoEntrance.PhoneNumbers;

public class AccessEquipmentAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Contacts> objects;

    public AccessEquipmentAdapter(Context context, List<Contacts> listContacts) {
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
            view = lInflater.inflate(R.layout.row_item_access_equipment, parent, false);
        }
        final Contacts contacts = getContacts(position);
        ((TextView) view.findViewById(R.id.contacts_fio)).setText(contacts.getFamilyName()
                + " " +contacts.getName() + " "+contacts.getMiddleName());
        ((TextView) view.findViewById(R.id.contacts_type)).setText(contacts.getType());

        ImageButton btn = (ImageButton) view.findViewById(R.id.contacts_button);

        final List<String> phonesClean = new ArrayList<>();
        for(int i=0; i<contacts.getPhoneNumbers().size(); i++) {
            phonesClean.add(contacts.getPhoneNumbers().get(i).getNumber());
        }

        if(phonesClean.isEmpty()) {
            btn.setVisibility(View.INVISIBLE);
        } else {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createTwoButtonsAlertDialog(contacts.getName() +
                            " "+ contacts.getMiddleName(),phonesClean);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createTwoButtonsAlertDialog(contacts.getName() +
                            " "+ contacts.getMiddleName(),phonesClean);
                }
            });
        }


        return view;
    }

    // создает диалоговое
    private void createTwoButtonsAlertDialog(String title, List<String>phones) {
        AlertDialog.Builder builder;
        final String[] selectedPhone = new String[1];
        final String[] mChooseNumberPgone = phones.toArray(new String[phones.size()]);
        builder = new AlertDialog.Builder(ctx);
        selectedPhone[0] = mChooseNumberPgone[0];
        builder.setTitle(title)

                // добавляем одну кнопку для закрытия диалога
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })

                .setPositiveButton("Звонок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Uri phoneCall = Uri.parse("tel:"  + selectedPhone[0]);
                                Intent caller = new Intent(Intent.ACTION_DIAL, phoneCall);
                                ctx.startActivity(caller);
                            }
                        })

                // добавляем переключатели
                .setSingleChoiceItems(mChooseNumberPgone, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                selectedPhone[0] = mChooseNumberPgone[item];
                            }
                        });
        builder.show();
    }

    // по позиции
    Contacts getContacts(int position) {
        return ((Contacts) getItem(position));
    }
}
