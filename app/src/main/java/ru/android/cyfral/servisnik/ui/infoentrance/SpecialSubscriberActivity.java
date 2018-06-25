package ru.android.cyfral.servisnik.ui.infoentrance;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.infoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.infoEntrance.SpecialApartments;
import ru.android.cyfral.servisnik.model.infoEntrance.adapters.SpecialSubscriberAdapter;
//Особые абоненты
public class SpecialSubscriberActivity extends AppCompatActivity {
    private List<SpecialApartments> specialApartmentsList;
    private SpecialSubscriberAdapter specialSubscriberAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_subscriber);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Особые абоненты");

        Intent intent = getIntent();
        InfoEntrance infoEntrance = (InfoEntrance) intent.getExtras().getSerializable("infoentrance");

        specialApartmentsList = infoEntrance.getData().getSpecialApartments();
        specialSubscriberAdapter = new SpecialSubscriberAdapter(this,specialApartmentsList );

        ListView lv_special_subscriber = (ListView) findViewById(R.id.lv_special_subscriber);
        lv_special_subscriber.setAdapter(specialSubscriberAdapter);
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
