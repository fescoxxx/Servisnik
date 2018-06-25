package ru.android.cyfral.servisnik.ui.entranceto;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.DataFetchEntranceTo;
import ru.android.cyfral.servisnik.model.entranceto.Data;
import ru.android.cyfral.servisnik.model.entranceto.EntranceTo;
import ru.android.cyfral.servisnik.model.entranceto.adapter.EntranceToAdapter;
import ru.android.cyfral.servisnik.model.entranceto.adapter.EntranceToRecycleAdapter;

//поиск по подъездам
public class EntranceSearchActivity extends AppCompatActivity implements DataFetchEntranceTo, EntranceToRecycleAdapter.EntranceToClickListener {
    SearchView searchView;
    private static DataDatabase mDatabase;
    private String filtrText;
    private EntranceTo entranceTo;
    private RecyclerView rv_list_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_search);


        rv_list_search = (RecyclerView) findViewById(R.id.rv_list_search);
        rv_list_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        mDatabase = new DataDatabase(this);
        mDatabase.fetchDatasForEntranceTo(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.listsearch).getActionView();
        searchView.setQueryHint("Название улицы");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.requestFocus();

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor_search); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
                    }
                };
                cntr.start();
                return false;
            }
        });

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(text.get(0).trim(), true);
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

    //Фильтр по поиску
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

    //показать список
    private void showListEntranceTo(List<Data> listData) {
        final EntranceToRecycleAdapter entranceToAdapter;
        entranceToAdapter = new EntranceToRecycleAdapter(this ,this);
        entranceToAdapter.allAddData(listData);
        rv_list_search.setAdapter(entranceToAdapter);
    }

    @Override
    public void onDeliverData(EntranceTo entranceTo) {
        this.entranceTo = entranceTo;
        showListEntranceTo(entranceTo.getData());
        Log.d(" this.entranceTo ",  this.entranceTo.getId());
    }

    @Override
    public void onClick(int position) {

    }
}
