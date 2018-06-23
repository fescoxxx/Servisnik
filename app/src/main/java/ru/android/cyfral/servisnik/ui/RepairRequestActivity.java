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
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchEntranceTo;
import ru.android.cyfral.servisnik.model.DataFetchSearchActivity;
import ru.android.cyfral.servisnik.model.entranceto.EntranceTo;
import ru.android.cyfral.servisnik.model.entranceto.adapter.EntranceToAdapter;
import ru.android.cyfral.servisnik.model.entranceto.adapter.EntranceToRecycleAdapter;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.model.orderCard.adapter.DataCategoryAdapter;
import ru.android.cyfral.servisnik.model.orderCard.adapter.ExpandableRecyclerAdapter;
import ru.android.cyfral.servisnik.model.orderCard.adapter.RepairRequestCategory;
import ru.android.cyfral.servisnik.model.repairRequests.Data;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class RepairRequestActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static RecyclerView entrance_to_recyclerView;
    private static EntranceToRecycleAdapter entranceToRecycleAdapter;
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
    private static TabLayout tabLayout;
    private static TextView mTexrView;
    private static LinearLayout linearNoConnectionInternet;
    private static SwipeRefreshLayout srlRepairReques;
    private static SwipeRefreshLayout srlEntranceTo;
    private static DataDatabase mDatabase;
    private static Call<RefreshToken> callRedresh;
    private static Call<RepairRequest> repairRequestCall;
    private static Call<OrderCard> orderCardCall;
    private static ProgressDialog mDialog;
    private static RepairRequestFragment.SaveIntoDatabaseRequest taskRepairRequest;
    private static ListPprFragment.SaveIntoDatabaseRequest taskEntanceTo;
    private static TabLayout.OnTabSelectedListener selectedListener;

    //пункты меню для поиска
    private static MenuItem findStreet;
    private static MenuItem numberDesk;
    private static MenuItem numberMobile;
    private static Toolbar toolbar;

    final String LOG_TAG = "myLogs2";

    private static Call<EntranceTo> entranceToCall; //запрос на список подъездов на ТО

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_request);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        findStreet = menu.findItem(R.id.findStreet);
        numberDesk = menu.findItem(R.id.numberDesk);
        numberMobile = menu.findItem(R.id.numberMobile);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(Constants.FIRST_LOAD_APP.TAB_GENERAL_APP == 0) {
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
            } else if (id == R.id.numberMobile) {
                Intent intent = new Intent("ru.android.cyfral.servisnik.search");
                intent.putExtra("searchHint", Constants.SEARCH.NUMBER_PHONE);
                startActivity(intent);
            } else if (id == R.id.listworkmap) {
                Intent intent = new Intent("ru.android.cyfral.servisnik.ui.listwork");
                startActivity(intent);
            }
        } else if (Constants.FIRST_LOAD_APP.TAB_GENERAL_APP == 1) {
             int id = item.getItemId();
             if (id == R.id.listworkmap) {
                Intent intent = new Intent("ru.android.cyfral.servisnik.ui.listwork");
                startActivity(intent);
             } else if (id == R.id.menusearch) {
                 Intent intent = new Intent("ru.android.cyfral.servisnik.entrancesearchactivity");
                 startActivity(intent);
             }
        }
        return super.onOptionsItemSelected(item);
    }

     public static class RepairRequestFragment extends Fragment implements DataCategoryAdapter.ItemClickListener, DataFetchSearchActivity {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public RepairRequestFragment() {
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Constants.FIRST_LOAD_APP.TAB_GENERAL_APP = 0;
            Constants.FIRST_LOAD_APP.ENTRANCE_TO_FIRST = true;
            try {repairRequestCall.cancel(); } catch (Exception ex) {}
            try {callRedresh.cancel();} catch (Exception ex) {}
            try {entranceToCall.cancel();} catch (Exception ex) {}
            try {mDatabase.close();} catch (Exception ex) {}

        }

       /* public void getFeed() {

            srlRepairReques.setRefreshing(true);
            SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
            String token = sPref.getString(Constants.SETTINGS.TOKEN, "");
            repairRequestCall = serviceApiClient.repairRequests("Bearer "+token);
            Log.d("Splash_call", "");
            repairRequestCall.enqueue(new Callback<RepairRequest>() {
                @Override
                public void onResponse(Call<RepairRequest> call, Response<RepairRequest> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getIsSuccess().equals("true")){
                            showRepairRequest(sortListData(response.body().getData()),
                                    true);
                        } else {
                            showErrorDialog(response.body().getErrors().getCode());
                            getFeedFromDatabase();
                        }
                    } else {
                        srlRepairReques.setRefreshing(false);
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
                    srlRepairReques.setRefreshing(false);
                    showErrorDialog("");
                    Log.d("repairRequests", t.getMessage());
                }
            });

        }*/

        private void showRepairRequest(List<Data> Listdata, Boolean saveDataBase) {
            datasCategories = new ArrayList<>();
            List<Data> mData = Listdata;
            if (saveDataBase) {
//                Log.d("Delete_db", Listdata.get(0).getAddress().getApartment());
                if(mDatabase != null) {
                    mDatabase.clearDataBase();
                }
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

            }

            if (saveDataBase) {
                try {
                    taskRepairRequest = new SaveIntoDatabaseRequest();
                    taskRepairRequest.execute(mData);
                } catch (Exception ex) {

                }

            }
            String DATE_FORMAT_NOW = "yyyy-MM-dd'T'HH:mm:ss";
            final DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);


            //сортировка по дате, адресу и номеру дома
            Collections.sort(overdueData, new Comparator<Data>() {
                @Override
                public int compare(Data date1, Data date2) {
                    try {

                        Date a1 = null;
                        Date b1 = null;

                        String a2 = date1.getAddress().getStreet();
                        String b2 = date2.getAddress().getStreet();

                        String a3 = date1.getAddress().getNumber();
                        String b3 = date2.getAddress().getNumber();

                        a1 = df.parse(date1.getDeadline());
                        b1 = df.parse(date2.getDeadline());

                        int result = a1.compareTo(b1);

                        if (result == 0) {
                            result = a2.compareTo(b2);
                        }
                        if (result == 0) {
                            result = a3.compareTo(b3);
                        }
                       return result;
                    } catch (Exception e) {
                        return 1;
                    }
                }
            });
            //сортировка по дате, адресу и номеру дома
            Collections.sort(todayData, new Comparator<Data>() {
                @Override
                public int compare(Data date1, Data date2) {
                    try {

                        Date a1 = null;
                        Date b1 = null;

                        String a2 = date1.getAddress().getStreet();
                        String b2 = date2.getAddress().getStreet();

                        String a3 = date1.getAddress().getNumber();
                        String b3 = date2.getAddress().getNumber();

                        a1 = df.parse(date1.getDeadline());
                        b1 = df.parse(date2.getDeadline());

                        int result = a1.compareTo(b1);

                        if (result == 0) {
                            result = a2.compareTo(b2);
                        }
                        if (result == 0) {
                            result = a3.compareTo(b3);
                        }
                        return result;
                    } catch (Exception e) {
                        return 1;
                    }
                }
            });
            // сортировка по дате, адресу и номеру дома
            Collections.sort(manyday, new Comparator<Data>() {
                @Override
                public int compare(Data date1, Data date2) {
                    try {

                        Date a1 = null;
                        Date b1 = null;

                        String a2 = date1.getAddress().getStreet();
                        String b2 = date2.getAddress().getStreet();

                        String a3 = date1.getAddress().getNumber();
                        String b3 = date2.getAddress().getNumber();

                        a1 = df.parse(date1.getDeadline());
                        b1 = df.parse(date2.getDeadline());

                        int result = a1.compareTo(b1);

                        if (result == 0) {
                            result = a2.compareTo(b2);
                        }
                        if (result == 0) {
                            result = a3.compareTo(b3);
                        }
                        return result;
                    } catch (Exception e) {
                        return 1;
                    }
                }
            });




            for(int i=0; i<overdueData.size(); i++) {
                Log.d("overdueData 2", overdueData.get(i).getDeadline());
            }


            RepairRequestCategory one_cat = new RepairRequestCategory("Просроченные ("+overdueData.size()+")", sortListData(overdueData));
            RepairRequestCategory two_cat = new RepairRequestCategory("Выполнить сегодня ("+todayData.size()+")", sortListData(todayData));
            RepairRequestCategory tree_cat = new RepairRequestCategory("Более одного дня ("+manyday.size()+")", sortListData(manyday));
            if(overdueData.size() != 0) {datasCategories.add(one_cat);}
            if (todayData.size() != 0) {datasCategories.add(two_cat);}
            if (manyday.size() != 0) {datasCategories.add(tree_cat);}
            mAdapter = new DataCategoryAdapter(getActivity(), datasCategories);
            mAdapter.setClickListener(itemClickListener);
            mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                @Override
                public void onListItemExpanded(int position) {
                    RepairRequestCategory expandedCategory = datasCategories.get(position);
                    //сохранение сосстояния категории
                    if (expandedCategory.getName().contains("Просроченные")) {
                        Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_1 = true;
                    } else if (expandedCategory.getName().contains("Выполнить сегодня")) {
                        Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_2 = true;
                    }else if (expandedCategory.getName().contains("Более одного дня")) {
                        Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_3 = true;
                    }
                }
                @Override
                public void onListItemCollapsed(int position) {
                    RepairRequestCategory collapsedCategory = datasCategories.get(position);
                    //сохранение сосстояния категории
                    if (collapsedCategory.getName().contains("Просроченные")) {
                        Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_1 = false;
                    } else if (collapsedCategory.getName().contains("Выполнить сегодня")) {
                        Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_2 = false;
                    }else if (collapsedCategory.getName().contains("Более одного дня")) {
                        Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_3 = false;
                    }
                }
            });

            mRecyclerView.setAdapter(mAdapter);
            //expand or collapse категории
            if (Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_1) {
                mAdapter.expandParent(one_cat);
            } else {
                mAdapter.collapseParent(one_cat);
            }

            if (Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_2) {
                mAdapter.expandParent(two_cat);
            } else {
                mAdapter.collapseParent(two_cat);
            }

            if (Constants.FIRST_LOAD_APP.REPAIR_REQUEST_EXPAND_CAT_3) {
                mAdapter.expandParent(tree_cat);
            } else {
                mAdapter.collapseParent(tree_cat);
            }

        }


        private void showErrorDialog(String code) {
            try{
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
            } catch (java.lang.NullPointerException ex) {

            }

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

        //запись списка в БД
        public static class SaveIntoDatabaseRequest extends AsyncTask<List<Data>, Void, Integer> {

            private final String TAG = SaveIntoDatabaseRequest.class.getSimpleName();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                srlRepairReques.setRefreshing(true);
            }
            @Override
            protected Integer doInBackground(List<Data>... params) {
                List<Data> data = params[0];
                try {
                    if (mDatabase != null) {
                        mDatabase.addDataRequest(data);
                    }
                } catch (Exception e) {
                    return 1;
                }
                return 1;
            }
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (result == 1) {
                    srlRepairReques.setRefreshing(false);
                }
            }
        }

       /* public void refreshList() {
            if (Utils.isNetworkAvailable(getActivity())) {
                linearNoConnectionInternet.removeView(mTexrView);
                getFeed();
            } else {
                srlRepairReques.setRefreshing(false);
                linearNoConnectionInternet.removeView(mTexrView);
                mTexrView.setText("Нет доступа к сети.\n" +
                        "Проверьте, есть ли доступ к Интернет через Ваше мобильное устройство");
                mTexrView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mTexrView.setPadding(7,7,7,7);
                mTexrView.setTextSize(15);
                linearNoConnectionInternet.addView(mTexrView);
                getFeedFromDatabase();
            }
        }*/

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_repair_request, container, false);
            srlRepairReques = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_container);

                // указываем слушатель свайпов пользователя
                srlRepairReques.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        if (Utils.isNetworkAvailable(getActivity())) {
                            linearNoConnectionInternet.removeView(mTexrView);
                            getListRepairRequest();
                        } else {
                            srlRepairReques.setRefreshing(false);
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
                });
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         //   getFeed();
            getListRepairRequest();
            itemClickListener = this;
            return rootView;
        }

        private void getListRepairRequest() {
            String token = loadTextPref(Constants.SETTINGS.TOKEN);
            String token_ref = loadTextPref(Constants.SETTINGS.REFRESH_TOKEN);
            String life_time_token = loadTextPref(Constants.SETTINGS.DATE_TOKEN);
            //токенов нет
            if (token.equals("")) {
                startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                getActivity().finish();
            } else {
                String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);
                Date date_ltt = null;
                try {
                    date_ltt = df.parse(life_time_token);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date_now = new Date();
                if (date_now.after(date_ltt)) {
                    Log.d("life_time_date_token4", " Новая дата позже" + date_now.toString() + "      " + date_ltt.toString());
                    //Токен просрочен, пробуем получить новый
                    Call<RefreshToken> callRedresh = tokenClient.refreshToken("refresh_token",
                            "mpservisnik",
                            "secret",
                            loadTextPref("token_refresh"));
                    callRedresh.enqueue(new Callback<RefreshToken>() {
                        @Override
                        public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                            if (response.isSuccessful()) {
                                SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString(Constants.SETTINGS.TOKEN, response.body().getAccess_token());
                                ed.putString(Constants.SETTINGS.REFRESH_TOKEN, response.body().getRefresh_token());
                                Calendar date = Calendar.getInstance();
                                long t = date.getTimeInMillis();
                                Date life_time_date_token =
                                        new Date(t + (Constants.SETTINGS.ONE_SECUNDE_IN_MILLIS
                                                * Integer.valueOf(response.body().getExpires_in())));
                                ed.putString(Constants.SETTINGS.DATE_TOKEN, getFormatDate(life_time_date_token));
                                ed.apply();
                                loadListRepairRequest();
                            } else {
                                //сервер вернул ошибку
                                //   mProgressBar.setVisibility(View.INVISIBLE);
                                int rc = response.code();
                                if (rc == 401) {
                                    srlRepairReques.setRefreshing(false);
                                    startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                    getActivity().finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshToken> call, Throwable t) {
                            srlRepairReques.setRefreshing(false);
                            showErrorDialog("");
                        }
                    });

                } else {
                    //токен живой
                    loadListRepairRequest();
                }
            }

        }

         private void loadListRepairRequest() {
             srlRepairReques.setRefreshing(true);
             String token = loadTextPref(Constants.SETTINGS.TOKEN);
             final EntranceToRecycleAdapter entranceToRecycleAdapter;
             repairRequestCall = serviceApiClient.repairRequests("Bearer "+token);
             repairRequestCall.enqueue(new Callback<RepairRequest>() {
                 @Override
                 public void onResponse(Call<RepairRequest> call, Response<RepairRequest> response) {
                     if (response.isSuccessful()) {
                         if (response.body().getIsSuccess().equals("true")){
                             if (response.body().getData() != null) {
                                 showRepairRequest(response.body().getData(), true);
                             } else {
                                 srlRepairReques.setRefreshing(false);
                                 datasCategories = new ArrayList<>();
                                 mAdapter = new DataCategoryAdapter(getActivity(), datasCategories);
                                 mRecyclerView.setAdapter(mAdapter);
                             }
                         } else {
                             //сервер вернул ошибку от АПИ
                             srlRepairReques.setRefreshing(false);
                             datasCategories = new ArrayList<>();
                             mAdapter = new DataCategoryAdapter(getActivity(), datasCategories);
                             mRecyclerView.setAdapter(mAdapter);
                             showErrorDialog(response.body().getErrors().getCode());
                         }
                     } else {
                         //сервер вернул ошибку
                         srlRepairReques.setRefreshing(false);
                         int rc = response.code();
                         if (rc == 401) {
                             startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                             getActivity().finish();
                         }
                     }
                 }

                 @Override
                 public void onFailure(Call<RepairRequest> call, Throwable t) {
                     //Произошла непредвиденная ошибка
                     srlRepairReques.setRefreshing(false);
                     datasCategories = new ArrayList<>();
                     mAdapter = new DataCategoryAdapter(getActivity(), datasCategories);
                     mRecyclerView.setAdapter(mAdapter);
                     showErrorDialog("");
                 }
             });

         }

         private String loadTextPref(String prefStr) {
             SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
             return sPref.getString(prefStr, "");
         }

         public static String getFormatDate(Date date) {
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             return sdf.format(date);
         }

        @Override
        public void onClick(View view, Data data, int position) {
            if (itemClickListener != null) {
                mDatabase.updateIsViewedDataRepairRequest(data); //обновляем в БД метку ISVIEWED
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
                getListRepairRequest();
            }


        }
        @Override
        public void onDeliverAllDatas(List<Data> datas) {
            showRepairRequest(datas, false);
        }


        private List<Data> sortListData(List<Data> notSortListData){
            List<Data> result = new ArrayList<>();

            for(int i=0; i<notSortListData.size(); i++) {
                if(notSortListData.get(i).getIsViewed().equals("false")) {
                    result.add(notSortListData.get(i));
                }
            }
            for(int i=0; i<notSortListData.size(); i++) {
                if(notSortListData.get(i).getIsViewed().equals("true")) {
                    result.add(notSortListData.get(i));
                }
            }

            return result;
        }

    }

    public static class ListPprFragment extends Fragment implements  EntranceToRecycleAdapter.EntranceToClickListener, DataFetchEntranceTo {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        final String LOG_TAG = "myLogs2";

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

        public void onDestroy() {
            super.onDestroy();
            Constants.FIRST_LOAD_APP.ENTRANCE_TO_FIRST = true;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_ppr, container, false);
            srlEntranceTo = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_entrance_to);
            entrance_to_recyclerView = (RecyclerView) rootView.findViewById(R.id.entrance_to_recyclerView);
            entrance_to_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // указываем слушатель свайпов пользователя
            srlEntranceTo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (Utils.isNetworkAvailable(getActivity())) {
                        linearNoConnectionInternet.removeView(mTexrView);
                        getListEntranceTo();
                    } else {
                        srlEntranceTo.setRefreshing(false);
                        linearNoConnectionInternet.removeView(mTexrView);
                        mTexrView.setText("Нет доступа к сети.\n" +
                                "Проверьте, есть ли доступ к Интернет через Ваше мобильное устройство");
                        mTexrView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        mTexrView.setPadding(7,7,7,7);
                        mTexrView.setTextSize(15);
                        linearNoConnectionInternet.addView(mTexrView);
                        getListEntranceToDataBase();
                    }

                }
            });

            selectedListener = new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Constants.FIRST_LOAD_APP.TAB_GENERAL_APP = tab.getPosition();
                    if (tab.getPosition() == 1) {
                        try {
                            findStreet.setVisible(false);
                            numberDesk.setVisible(false);
                            numberMobile.setVisible(false);
                        } catch (NullPointerException ex) {
                        }

                        toolbar.setTitle("Подъезды на ТО");
                        if (Constants.FIRST_LOAD_APP.ENTRANCE_TO_FIRST) {
                            Log.d(LOG_TAG, "tab.onTabSelected() первое нажатие ");
                            srlEntranceTo.setRefreshing(true);
                            getListEntranceTo();
                            Constants.FIRST_LOAD_APP.ENTRANCE_TO_FIRST = false;
                        } else {
                            Log.d(LOG_TAG, "tab.onTabSelected() второе и более нажатие ");
                        }
                    } else if (tab.getPosition() == 0){
                        try {
                            findStreet.setVisible(true);
                            numberDesk.setVisible(true);
                            numberMobile.setVisible(true);
                        } catch (NullPointerException ex) {}
                        toolbar.setTitle("Список ЗН");
                    }
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            };
            tabLayout.addOnTabSelectedListener(selectedListener);
            return rootView;
        }

        private void getListEntranceToDataBase(){
            mDatabase.fetchDatasForEntranceTo(this);
        }

        private void getListEntranceTo() {


            String token = loadTextPref(Constants.SETTINGS.TOKEN);
            String token_ref = loadTextPref(Constants.SETTINGS.REFRESH_TOKEN);
            String life_time_token = loadTextPref(Constants.SETTINGS.DATE_TOKEN);
            //токенов нет
            if (token.equals("")) {
                startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                getActivity().finish();
            } else {
                String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);
                Date date_ltt = null;
                try {
                    date_ltt = df.parse(life_time_token);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date_now = new Date();
                if (date_now.after(date_ltt)) {
                    Log.d("life_time_date_token4", " Новая дата позже" + date_now.toString() + "      " + date_ltt.toString());
                    //Токен просрочен, пробуем получить новый
                    Call<RefreshToken> callRedresh = tokenClient.refreshToken("refresh_token",
                            "mpservisnik",
                            "secret",
                            loadTextPref("token_refresh"));
                    callRedresh.enqueue(new Callback<RefreshToken>() {
                        @Override
                        public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                            if (response.isSuccessful()) {
                                SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString(Constants.SETTINGS.TOKEN, response.body().getAccess_token());
                                ed.putString(Constants.SETTINGS.REFRESH_TOKEN, response.body().getRefresh_token());
                                Calendar date = Calendar.getInstance();
                                long t = date.getTimeInMillis();
                                Date life_time_date_token =
                                        new Date(t + (Constants.SETTINGS.ONE_SECUNDE_IN_MILLIS
                                                * Integer.valueOf(response.body().getExpires_in())));
                                ed.putString(Constants.SETTINGS.DATE_TOKEN, getFormatDate(life_time_date_token));
                                ed.apply();
                                loadListEntranceTo();
                            } else {
                                //сервер вернул ошибку
                                //   mProgressBar.setVisibility(View.INVISIBLE);
                                int rc = response.code();
                                if (rc == 401) {
                                    srlEntranceTo.setRefreshing(false);
                                    startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                    getActivity().finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshToken> call, Throwable t) {
                            srlEntranceTo.setRefreshing(false);
                            showErrorDialog("");
                        }
                    });

                } else {
                    //токен живой
                    loadListEntranceTo();

                }
            }
        }

        @Override
        public void onClick(int position) {

        }

        @Override
        public void onDeliverData(EntranceTo entranceTo) {
            List<ru.android.cyfral.servisnik.model.entranceto.Data> dataList = entranceTo.getData();
            entrance_to_recyclerView.setAdapter(entranceToRecycleAdapter);
            entranceToRecycleAdapter.allAddData(dataList);
            srlEntranceTo.setRefreshing(false);
        }

        //запись списка в БД
        public static class SaveIntoDatabaseRequest extends AsyncTask<EntranceTo, Void, Integer> {

            private final String TAG = SaveIntoDatabaseRequest.class.getSimpleName();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                srlEntranceTo.setRefreshing(true);
            }
            @Override
            protected Integer doInBackground(EntranceTo... params) {
                EntranceTo data = params[0];
                try {
                    if (mDatabase != null) {
                        mDatabase.addDataEntranceTo(data);
                    }
                } catch (Exception e) {
                    return 1;
                }
                return 1;
            }
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (result == 1) {
                    srlEntranceTo.setRefreshing(false);
                }
            }
        }

        private void loadListEntranceTo() {
            String token = loadTextPref(Constants.SETTINGS.TOKEN);

            entranceToRecycleAdapter = new EntranceToRecycleAdapter(this,getActivity());
            entranceToCall = serviceApiClient
                    .getEntranceToList("Bearer " + token);
            entranceToCall.enqueue(new Callback<EntranceTo>() {
                @Override
                public void onResponse(Call<EntranceTo> call, Response<EntranceTo> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getIsSuccess().equals("true")){
                            if (response.body().getData() != null) {
                                EntranceTo entranceList = response.body();
                                List<ru.android.cyfral.servisnik.model.entranceto.Data> dataList = entranceList.getData();
                                entrance_to_recyclerView.setAdapter(entranceToRecycleAdapter);
                                entranceToRecycleAdapter.allAddData(dataList);
                                srlEntranceTo.setRefreshing(false);
                                taskEntanceTo = new SaveIntoDatabaseRequest();
                                taskEntanceTo.execute(entranceList);
                            } else {
                                srlEntranceTo.setRefreshing(false);
                                entrance_to_recyclerView.setAdapter(entranceToRecycleAdapter);
                            }
                        } else {
                            //сервер вернул ошибку от АПИ
                            srlEntranceTo.setRefreshing(false);
                            entrance_to_recyclerView.setAdapter(entranceToRecycleAdapter);
                            showErrorDialog(response.body().getErrors().getCode());
                        }
                    } else {
                        //сервер вернул ошибку
                        srlEntranceTo.setRefreshing(false);
                        entrance_to_recyclerView.setAdapter(entranceToRecycleAdapter);
                        int rc = response.code();
                        if (rc == 401) {
                            startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                            getActivity().finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<EntranceTo> call, Throwable t) {
                    //Произошла непредвиденная ошибка
                    srlEntranceTo.setRefreshing(false);
                    entrance_to_recyclerView.setAdapter(entranceToRecycleAdapter);
                    showErrorDialog("");
                }
            });
        }

        private String loadTextPref(String prefStr) {
            SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
            return sPref.getString(prefStr, "");
        }

        public static String getFormatDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
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

        public void onStart() {
            super.onStart();
            Log.d(LOG_TAG, "Fragment2 onStart");
        }

        public void onResume() {
            super.onResume();
            Log.d(LOG_TAG, "Fragment2 onResume");
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
            Log.d("", String.valueOf(position));
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
