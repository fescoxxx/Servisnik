package ru.android.cyfral.servisnik.ui.entranceto;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.DataFetchEntranceTo;
import ru.android.cyfral.servisnik.model.entranceto.Data;
import ru.android.cyfral.servisnik.model.entranceto.EntranceTo;
import ru.android.cyfral.servisnik.model.entranceto.adapter.EntranceToAdapter;

public class EntranceSearchActivity extends AppCompatActivity implements DataFetchEntranceTo {
    MaterialSearchView searchView;
    Toolbar toolbar;
    private static DataDatabase mDatabase;
    private String filtrText;
    private EntranceTo entranceTo;
    private ListView rv_list_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        rv_list_search = (ListView) findViewById(R.id.rv_list_search);
        searchView.setVoiceSearch(true); //or false
        searchView.setHint("Название улицы");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDatabase = new DataDatabase(this);
        mDatabase.fetchDatasForEntranceTo(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu_item_entrance_to, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.showVoice(true);
        searchView.showSearch();
        searchView.showSuggestions();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {

            private int waitingTime = 500;
            private CountDownTimer cntr;

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if(cntr != null){
                    cntr.cancel();
                }
                cntr = new CountDownTimer(waitingTime, 500) {
                    public void onTick(long millisUntilFinished) {
                        Log.d("TIME","seconds remaining: " + millisUntilFinished / 1000);
                    }
                    public void onFinish() {

                        filtrListEntranceTo(newText);
                        filtrText = newText;
                       // mRepairRequestAdapter.clearData();

                        // getFeedFromDatabaseSearchFiltr(newText);
                    }
                };
                cntr.start();
                return false;
            }
        });

       /* searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                filtrListEntranceTo(filtrText);
            }
        });*/

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(text.get(0), true);
                    searchView.requestFocus(SearchView.FOCUS_RIGHT);
                }
                break;
            }

        }
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


    private void filtrListEntranceTo(String filtr) {
        List<Data> listData= new ArrayList<>();
        try {
            for (int i=0; i<entranceTo.getData().size(); i++) {
                if (entranceTo.getData().get(i).getAddress().getStreet().toLowerCase().contains(filtr.toLowerCase())) {
                    listData.add(entranceTo.getData().get(i));
                }
            }
            showListEntranceTo(listData);
        } catch (Exception ex) {
            showListEntranceTo(this.entranceTo.getData());
        }


    }

    private void showListEntranceTo(List<Data> listData) {
        final EntranceToAdapter entranceToAdapter;
        entranceToAdapter = new EntranceToAdapter(this ,listData);
        rv_list_search.setAdapter(entranceToAdapter);
    }

    @Override
    public void onDeliverData(EntranceTo entranceTo) {
        this.entranceTo = entranceTo;
        showListEntranceTo(entranceTo.getData());
        Log.d(" this.entranceTo ",  this.entranceTo.getId());
    }
}
