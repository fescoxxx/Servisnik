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
import ru.android.cyfral.servisnik.model.InfoEntrance.CallingDevice;
import ru.android.cyfral.servisnik.model.InfoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.InfoEntrance.adapters.CallBlockAdapter;

public class CallBlockActivity extends AppCompatActivity {
    private List<CallingDevice> callingDeviceList;
    private CallBlockAdapter callBlockAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_block);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Вызывной блок");

        Intent intent = getIntent();
        InfoEntrance infoEntrance = (InfoEntrance) intent.getExtras().getSerializable("infoentrance");

        callingDeviceList = infoEntrance.getData().getCallingDevice();
        callBlockAdapter = new CallBlockAdapter(this, callingDeviceList);

        // настраиваем список
        ListView lv_call_block = (ListView) findViewById(R.id.lv_call_block);
        lv_call_block.setAdapter(callBlockAdapter);

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
