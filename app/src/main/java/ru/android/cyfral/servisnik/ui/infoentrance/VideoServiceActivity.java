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
import ru.android.cyfral.servisnik.model.infoEntrance.VideoService;
import ru.android.cyfral.servisnik.model.infoEntrance.adapters.VideoServiceAdapter;
//Видеообслуживание
public class VideoServiceActivity extends AppCompatActivity {
    private List<VideoService> listVideoService;
    private VideoServiceAdapter videoServiceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_service);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Видеообслуживание");

        Intent intent = getIntent();
        InfoEntrance infoEntrance = (InfoEntrance) intent.getExtras().getSerializable("infoentrance");

        listVideoService = infoEntrance.getData().getVideoService();
        videoServiceAdapter = new VideoServiceAdapter(this, listVideoService);

        // настраиваем список
        ListView list_video_service = (ListView) findViewById(R.id.list_video_service);
        list_video_service.setAdapter(videoServiceAdapter);
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
