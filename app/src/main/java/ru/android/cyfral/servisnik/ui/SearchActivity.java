package ru.android.cyfral.servisnik.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchListener;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.OrderCard.adapter.RepairRequestAdapter;
import ru.android.cyfral.servisnik.model.repairRequests.Data;

public class SearchActivity extends AppCompatActivity  implements  RepairRequestAdapter.RepairRequestClickListener, DataFetchListener {
    private SearchView searchView;
    private String searchHint;
    private RepairRequestAdapter mRepairRequestAdapter;
    private RecyclerView mRecyclerView;
    private static DataDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        searchHint = intent.getStringExtra("searchHint");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        mDatabase = new DataDatabase(this);
        mRepairRequestAdapter = new RepairRequestAdapter(this);
        getFeedFromDatabase();
        mRecyclerView.setAdapter(mRepairRequestAdapter);

/*        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(this, "Oops! Your device doesn’t support Speech to Text",Toast.LENGTH_SHORT);
            t.show();
        }*/
    }

    private void getFeedFromDatabase() {
        mDatabase.fetchDatas(this);
    }

    private void getFeedFromDatabaseSearchFiltr(String filtr) {
        mDatabase.fetchDatasForFiltr(this, filtr, searchHint);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    searchView.setIconified(false);
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(text.get(0), true);
                    searchView.requestFocus(SearchView.FOCUS_RIGHT);
                }
                break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.listsearch).getActionView();
        searchView.setQueryHint(searchHint);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        if(searchHint.equals(Constants.SEARCH.NAME_STREET)) {
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else if (searchHint.equals(Constants.SEARCH.NUMBER_ZN)) {
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else if (searchHint.equals(Constants.SEARCH.NUMBER_PHONE)) {
            searchView.setInputType(InputType.TYPE_CLASS_PHONE);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                        mRepairRequestAdapter.clearData();
                        getFeedFromDatabaseSearchFiltr(newText);
                    }
                };
                cntr.start();
                return false;
            }
        });
        return true;
    }

    private void showToast(String text){
        Toast.makeText(this,
                text,
                Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onDeliverAllDatas(List<Data> datas) {

    }

    @Override
    public void onDeliverData(Data data) {
        mRepairRequestAdapter.addData(data);
    }

    @Override
    public void onDeliverOrderCard(OrderCard orderCard) {

    }

    @Override
    public void onHideDialog() {

    }

    @Override
    public void onClick(int position) {
        Data selectedData = mRepairRequestAdapter.getSelectedData(position);
        Intent intent = new Intent("ru.android.cyfral.servisnik.card");
        intent.putExtra(Constants.SETTINGS.GUID, selectedData.getId());
        startActivity(intent);
    }
}
