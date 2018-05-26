package ru.android.cyfral.servisnik.ui.infoentrance;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.infoEntrance.Contacts;
import ru.android.cyfral.servisnik.model.infoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.infoEntrance.PhoneNumbers;
import ru.android.cyfral.servisnik.model.infoEntrance.adapters.AccessEquipmentAdapter;

public class AccessEquipmentActivity extends AppCompatActivity {

    private List<Contacts> listContacts;
    private AccessEquipmentAdapter accessEquipmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_equipment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Доступ к оборудованию");

        Intent intent = getIntent();
        InfoEntrance infoEntrance = (InfoEntrance) intent.getExtras().getSerializable("infoentrance");

        listContacts = infoEntrance.getData().getContacts();
        Log.d("listContacts", String.valueOf(listContacts.size()));
        for(int i = 0; i<listContacts.size(); i++) {
            Log.d("listContacts", listContacts.get(i).getFamilyName());
            Log.d("listContacts", listContacts.get(i).getMiddleName());
            Log.d("listContacts", listContacts.get(i).getName());
            Log.d("listContacts", listContacts.get(i).getType());
            List<PhoneNumbers> phoneNumbersList =  listContacts.get(i).getPhoneNumbers();
            for (int x = 0; x<phoneNumbersList.size(); x++) {
                Log.d("listContacts", phoneNumbersList.get(x).getNumber());

            }

        }
        accessEquipmentAdapter = new AccessEquipmentAdapter(this, listContacts);

        ListView lv_access_equipment = (ListView) findViewById(R.id.lv_access_equipment);
        lv_access_equipment.setAdapter(accessEquipmentAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
