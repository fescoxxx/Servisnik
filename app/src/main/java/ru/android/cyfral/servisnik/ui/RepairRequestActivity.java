package ru.android.cyfral.servisnik.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchListener;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.model.OrderCard.adapter.DataCategoryAdapter;
import ru.android.cyfral.servisnik.model.OrderCard.adapter.ExpandableRecyclerAdapter;
import ru.android.cyfral.servisnik.model.OrderCard.adapter.RepairRequestCategory;
import ru.android.cyfral.servisnik.model.repairRequests.Data;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class RepairRequestActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static DataCategoryAdapter mAdapter;
    private static RecyclerView mRecyclerView;
    private static List<RepairRequestCategory> datasCategories;
    private static DataCategoryAdapter.ItemClickListener itemClickListener;

    static ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    static TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private static TextView mTexrView;
    private static LinearLayout linearNoConnectionInternet;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private static DataDatabase mDatabase;
    private static Call<RefreshToken> callRedresh;
    private static Call<RepairRequest> repairRequestCall;
    private static Call<OrderCard> orderCardCall;
    private static ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        linearNoConnectionInternet = (LinearLayout) findViewById(R.id.linearNoConnectionInternet);
        mTexrView = new TextView(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mDatabase = new DataDatabase(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repair_request, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.findStreet) {
            Intent intent = new Intent("ru.android.cyfral.servisnik.search");
            intent.putExtra("searchHint", Constants.SEARCH.NAME_STREET);
            startActivity(intent);
        } else if (id == R.id.numberDesk) {
            Intent intent = new Intent("ru.android.cyfral.servisnik.search");
            intent.putExtra("searchHint", Constants.SEARCH.NUMBER_ZN);
            startActivity(intent);
        } else if(id == R.id.numberMobile) {
            Intent intent = new Intent("ru.android.cyfral.servisnik.search");
            intent.putExtra("searchHint", Constants.SEARCH.NUMBER_PHONE);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class RepairRequestFragment extends Fragment implements DataCategoryAdapter.ItemClickListener, DataFetchListener {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public RepairRequestFragment() {
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            try {repairRequestCall.cancel(); } catch (java.lang.NullPointerException ex) {}
            try {callRedresh.cancel();} catch (java.lang.NullPointerException ex) {}

        }

        public void getFeed() {
            mSwipeRefreshLayout.setRefreshing(true);
            SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
            String token = sPref.getString(Constants.SETTINGS.TOKEN, "");
            repairRequestCall = serviceApiClient.repairRequests("Bearer "+token);
            Log.d("Splash_call", "");
            repairRequestCall.enqueue(new Callback<RepairRequest>() {
                @Override
                public void onResponse(Call<RepairRequest> call, Response<RepairRequest> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getIsSuccess().equals("true")){
                            showRepairRequest(response.body().getData(), true);
                        } else {
                            showErrorDialog(response.body().getErrors().getCode());
                            getFeedFromDatabase();
                        }
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        int sc = response.code();
                        switch (sc) {
                            case 401:
                                Log.d("case 401", response.message());
                                //Токен просрочен, пробуем получить новый
                                SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
                                callRedresh = tokenClient.refreshToken("refresh_token",
                                        "mpservisnik",
                                        "secret",
                                        sPref.getString("token_refresh", ""));

                                callRedresh.enqueue(new Callback<RefreshToken>() {
                                    @Override
                                    public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                                        if (response.isSuccessful()) {
                                            SharedPreferences myPrefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor ed = myPrefs.edit();
                                            ed.putString(Constants.SETTINGS.TOKEN,  response.body().getAccess_token());
                                            ed.putString(Constants.SETTINGS.REFRESH_TOKEN,  response.body().getRefresh_token());
                                            ed.apply();
                                        } else {
                                            showErrorDialog(String.valueOf(response.code()));
                                            startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                            getActivity().finish();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<RefreshToken> call, Throwable t) {
                                        showErrorDialog("");
                                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                        getActivity().finish();
                                    }
                                });
                                break;
                            default:
                        }
                    }
                }
                @Override
                public void onFailure(Call<RepairRequest> call, Throwable t) {
                    //не отправить запрос
                    mSwipeRefreshLayout.setRefreshing(false);
                    showErrorDialog("");
                    Log.d("repairRequests", t.getMessage());
                }
            });

        }

        private void showRepairRequest(List<Data> Listdata, Boolean saveDataBase) {
            datasCategories = new ArrayList<>();
            List<Data> mData = Listdata;
            if (saveDataBase) {
                Log.d("Delete_db", Listdata.get(0).getAddress().getApartment());
                mDatabase.clearDataBase();
            }
            List<Data> overdueData = new ArrayList<>();
            List<Data> todayData  = new ArrayList<>();
            List<Data> manyday = new ArrayList<>();
            Date dateToday = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
            for (int i = 0; i < mData.size(); i++) {
                Data data = mData.get(i);
                Log.d("Tatelog", "dateToday " +dateToday.toString());
                try {
                    Date dateDeadline = format.parse(data.getDeadline());
                    Log.d("Tatelog", "dateDeadline " +dateDeadline.toString());
                    if(dateDeadline.before(format.parse(format.format(dateToday)))) {
                        overdueData.add(data);

                    } else if (dateDeadline.equals(format.parse(format.format(dateToday)))){
                        todayData.add(data);

                    } else if (dateDeadline.after(format.parse(format.format(dateToday)))) {
                        manyday.add(data);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (saveDataBase) {
                    SaveIntoDatabaseRequest task = new SaveIntoDatabaseRequest();
                    task.execute(data);
                }
            }
            RepairRequestCategory one_cat = new RepairRequestCategory("Просроченные ("+overdueData.size()+")", overdueData);
            RepairRequestCategory two_cat = new RepairRequestCategory("Выполнить сегодня ("+todayData.size()+")", todayData);
            RepairRequestCategory tree_cat = new RepairRequestCategory("Более одного дня ("+manyday.size()+")", manyday);
            if(overdueData.size() != 0) {datasCategories.add(one_cat);}
            if (todayData.size() != 0) {datasCategories.add(two_cat);}
            if (manyday.size() != 0) {datasCategories.add(tree_cat);}
            mAdapter = new DataCategoryAdapter(getActivity(), datasCategories);
            mAdapter.setClickListener(itemClickListener);
            mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                @Override
                public void onListItemExpanded(int position) {
                    RepairRequestCategory expandedMovieCategory = datasCategories.get(position);
                    String toastMsg = getResources().getString(R.string.expanded, expandedMovieCategory.getName());
                }
                @Override
                public void onListItemCollapsed(int position) {
                    RepairRequestCategory collapsedMovieCategory = datasCategories.get(position);
                    String toastMsg = getResources().getString(R.string.collapsed, collapsedMovieCategory.getName());
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        private void showErrorDialog(String code) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Ошибка "+code);
            builder.setMessage("Произошла ошибка при выполнении запроса к серверу. Повторите попытку позже.");
            builder.setNeutralButton("Отмена",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    });
            builder.show();

        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RepairRequestFragment newInstance(int sectionNumber) {
            RepairRequestFragment fragment = new RepairRequestFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private void getFeedFromDatabase() {
            mDatabase.fetchDatas(this);
        }

        public static class SaveIntoDatabaseRequest extends AsyncTask<Data, Void, Void> {
            private final String TAG = SaveIntoDatabaseRequest.class.getSimpleName();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Data... params) {
                Data data = params[0];
                try {
                    mDatabase.addDataRequest(data);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                return null;
            }
        }

        public void refreshList() {
            if (Utils.isNetworkAvailable(getActivity())) {
                linearNoConnectionInternet.removeView(mTexrView);
                getFeed();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                linearNoConnectionInternet.removeView(mTexrView);
                mTexrView.setText("Нет доступа к сети.\n" +
                        "Проверьте, есть ли доступ к Интернет через Ваше мобильное устройство");
                mTexrView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mTexrView.setPadding(7,7,7,7);
                mTexrView.setTextSize(15);
                linearNoConnectionInternet.addView(mTexrView);
                getFeedFromDatabase();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_repair_request, container, false);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_container);

                // указываем слушатель свайпов пользователя
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshList();
                    }
                });
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            getFeed();
            itemClickListener = this;
            return rootView;
        }


        @Override
        public void onClick(View view, Data data, int position) {
            if (itemClickListener != null) {
                Intent intent = new Intent("ru.android.cyfral.servisnik.card");
                intent.putExtra(Constants.SETTINGS.GUID, data.getId());
                startActivityForResult(intent, 10);
            }
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data == null) {return;}
            if(requestCode == 10) {
                Log.d("onActivityResult_10", "All ok");
                refreshList();
            }


        }
        @Override
        public void onDeliverAllDatas(List<Data> datas) {
            showRepairRequest(datas, false);
        }

        @Override
        public void onDeliverData(Data data) {

        }

        @Override
        public void onDeliverOrderCard(OrderCard orderCard) {

        }

        @Override
        public void onHideDialog() {

        }



       /* @Override
        public void onClick(int position) {
            Data selectedData = mRepairRequestAdapter.getSelectedData(position);
           // Intent intent = new Intent(MainActivity.this, DetailActivity.class );
          //  intent.putExtra(Constants.REFERENCE.FLOWER, selectedFlower);
          //  startActivity(intent);
        }*/


    }

    public static class ListPprFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number2";
        public ListPprFragment() {
        }

        public static ListPprFragment newInstance(int sectionNumber) {
            ListPprFragment fragment = new ListPprFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_ppr, container, false);

            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Log.d("getItem_log", String.valueOf(position));
            switch (position) {
                case 0:
                    return RepairRequestFragment.newInstance(position + 1);
                case 1:
                    return ListPprFragment.newInstance(position + 1);
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    }
}
