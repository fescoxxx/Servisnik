package ru.android.cyfral.servisnik.ui.listwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.listwork.adapter.OrderCardListAdapter;
import ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist.Data;
import ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist.OrderCardList;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class NearListActivity extends AppCompatActivity {
    private Context mContect;
    private Double latitude;
    private Double longitude;
    private SharedPreferences sPref;
    private ProgressBar progressBar_near_list;
    private LinearLayout linearLayout_near_list;
    private SwipeRefreshLayout refreshLayout;
    private ListView lv_near_list;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<OrderCardList>  orderCardListCall;
    private Call<RefreshToken> callRedresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_list);
        mContect = this;
        setTitle("Список ЗН рядом");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar_near_list = (ProgressBar) findViewById(R.id.progressBar_near_list);
        linearLayout_near_list = (LinearLayout) findViewById(R.id.linearLayout_near_list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_near_list);
        lv_near_list = (ListView) findViewById(R.id.lv_near_list);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra(Constants.SETTINGS.LATITUDE, 0);
        longitude = intent.getDoubleExtra(Constants.SETTINGS.LONGITUDE, 0);

        progressBar_near_list.setVisibility(View.VISIBLE);

        getListNear();
        // указываем слушатель свайпов пользователя
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListNear();
            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {orderCardListCall.cancel(); } catch (Exception ex) {}
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if(requestCode == 10) {
            Log.d("onActivityResult_10", "All ok");
            progressBar_near_list.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.INVISIBLE);
            getListNear();
        }
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
    private void loadListNear() {
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        orderCardListCall = serviceApiClient
                .getOrderCardNearList(String.valueOf(latitude),
                        String.valueOf(longitude),
                        "Bearer " + token);
        orderCardListCall.enqueue(new Callback<OrderCardList>() {
            @Override
            public void onResponse(Call<OrderCardList> call, Response<OrderCardList> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        OrderCardList orderCardList = response.body();
                        List<Data> dataList;
                        final OrderCardListAdapter orderCardListAdapter;

                        dataList = orderCardList.getData();
                        orderCardListAdapter = new OrderCardListAdapter(mContect, sortListData(dataList));
                        lv_near_list.setAdapter(orderCardListAdapter);

                        //Для коректной работы list_view и refreshLayout вместе
                        lv_near_list.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                            }
                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                if (lv_near_list.getChildAt(0) != null) {
                                    refreshLayout.setEnabled(lv_near_list.getFirstVisiblePosition() == 0 && lv_near_list.getChildAt(0).getTop() == 0);
                                }
                            }
                        });

                        lv_near_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                View mLine_is_view = (View)  view.findViewById(R.id.line_is_view);
                                mLine_is_view.setBackgroundColor(Color.parseColor("#ffffff"));
                                Intent intent = new Intent("ru.android.cyfral.servisnik.card");
                                intent.putExtra(Constants.SETTINGS.GUID, orderCardListAdapter.getData(position).getId());
                                startActivityForResult(intent, 10);
                            }
                        });

                        progressBar_near_list.setVisibility(View.INVISIBLE);
                        refreshLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);

                    } else {
                        //сервер вернул ошибку от АПИ
                        refreshLayout.setRefreshing(false);
                        progressBar_near_list.setVisibility(View.INVISIBLE);
                        //refreshLayout.setVisibility(View.VISIBLE);
                        showErrorDialog(response.body().getErrors().getCode());
                    }
                } else {
                    //сервер вернул ошибку
                    refreshLayout.setRefreshing(false);
                    progressBar_near_list.setVisibility(View.INVISIBLE);
                    // refreshLayout.setVisibility(View.VISIBLE);
                    int rc = response.code();
                    if (rc == 401) {
                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderCardList> call, Throwable t) {
                //Произошла непредвиденная ошибка
                refreshLayout.setRefreshing(false);
                progressBar_near_list.setVisibility(View.INVISIBLE);
                //refreshLayout.setVisibility(View.VISIBLE);
                showErrorDialog("");
            }
        });

    }

    private void getListNear() {
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        String token_ref = loadTextPref(Constants.SETTINGS.REFRESH_TOKEN);
        String life_time_token = loadTextPref(Constants.SETTINGS.DATE_TOKEN);
        //токенов нет
        if (token.equals("")) {
            startActivity(new Intent("ru.android.cyfral.servisnik.login"));
            finish();
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
                callRedresh = tokenClient.refreshToken("refresh_token",
                        "mpservisnik",
                        "secret",
                        loadTextPref("token_refresh"));
                callRedresh.enqueue(new Callback<RefreshToken>() {
                    @Override
                    public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                        if (response.isSuccessful()) {
                            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor ed = myPrefs.edit();
                            ed.putString(Constants.SETTINGS.TOKEN, response.body().getAccess_token());
                            ed.putString(Constants.SETTINGS.REFRESH_TOKEN, response.body().getRefresh_token());
                            Calendar date = Calendar.getInstance();
                            long t = date.getTimeInMillis();
                            Date life_time_date_token =
                                    new Date(t + (Constants.SETTINGS.ONE_SECUNDE_IN_MILLIS
                                            * Integer.valueOf(response.body().getExpires_in())));
                            ed.putString(Constants.SETTINGS.DATE_TOKEN, getFormatDate(life_time_date_token));
                            ed.apply();
                            loadListNear();
                        } else {
                            //сервер вернул ошибку
                            //   mProgressBar.setVisibility(View.INVISIBLE);
                            int rc = response.code();
                            if (rc == 401) {
                                refreshLayout.setRefreshing(false);
                                startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RefreshToken> call, Throwable t) {
                        refreshLayout.setRefreshing(false);
                        showErrorDialog("");
                    }
                });

            } else {
                //токен живой
                loadListNear();
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

    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }
    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    private void showErrorDialog(String code) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ошибка "+code);
            builder.setMessage("Произошла ошибка при выполнении запроса к серверу. Повторите попытку позже.");
            builder.setNeutralButton("Отмена",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    });
            builder.show();
        } catch (Exception ex) {

        }

    }
}
