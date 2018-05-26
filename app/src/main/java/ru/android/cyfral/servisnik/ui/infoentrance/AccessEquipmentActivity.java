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
import ru.android.cyfral.servisnik.model.InfoEntrance.Contacts;
import ru.android.cyfral.servisnik.model.InfoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.InfoEntrance.adapters.AccessEquipmentAdapter;

public class AccessEquipmentActivity extends AppCompatActivity {

    private List<Contacts> listContacts;
    private AccessEquipmentAdapter accessEquipmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_equipment);
        setContentView(R.layout.activity_video_service);
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
        accessEquipmentAdapter = new AccessEquipmentAdapter(this,listContacts);

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
