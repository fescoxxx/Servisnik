package ru.android.cyfral.servisnik.ui.ordercard;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.orderCard.InstalledEquipments;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.orderCard.equipments.EquipmentsAdapter;

public class EquipmentActivity extends AppCompatActivity {
    private List<InstalledEquipments> listInstalledEquipments;
    private EquipmentsAdapter installedEquipmentsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Оборудование");
        Intent intent = getIntent();
        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");

        listInstalledEquipments = orderCard.getData().getInstalledEquipments();
        installedEquipmentsAdapter = new EquipmentsAdapter(this, listInstalledEquipments);
        // настраиваем список
        ListView lv_installedEquipments = (ListView) findViewById(R.id.lv_equipment);
        lv_installedEquipments.setAdapter(installedEquipmentsAdapter);

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
