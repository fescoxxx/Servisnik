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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.model.listwork.adapter.OrderCardListAdapter;
import ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist.Data;
import ru.android.cyfral.servisnik.model.listwork.worksat.entrancelist.EntranceList;
import ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist.OrderCardList;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

//активти работы по адресу
public class WorksAtActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout mLinearLayout;
    private SharedPreferences sPref;
    private String guid; //houseID текущий
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar_works_at;
    private ListView list_view_works_at; //список заказов по дому
    private TextView text_view_header_list_order_card;

    private View header_list_work_at;
    private View divider_bootm;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<EntranceList> entranceListCall;
    private Call<OrderCardList> orderCardListCall;
    private Call<RefreshToken> callRedresh;

    private LinearLayout linearLayout_entranceto;
    private Button btnEntrance;
    private Context mContect;
    private TextView textView_adress;
    private TextView textView_number;
    private TextView mTexrView;

    private LinearLayout linearNoConnectionInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works_at);
        mContect = this;
        setTitle("Работы по адресу");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_works_at);
        refreshLayout = (SwipeRefreshLayout)  findViewById(R.id.srl_workat);
        progressBar_works_at = (ProgressBar) findViewById(R.id.progressBar_works_at);
        list_view_works_at = (ListView)  findViewById(R.id.list_view_works_at);
        list_view_works_at.addHeaderView(createHeader());
        linearNoConnectionInternet = (LinearLayout)  findViewById(R.id.linearNoConnectionInternet);


        Intent intent = getIntent();
        guid = intent.getStringExtra("GUID");

        progressBar_works_at.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.INVISIBLE);


        actionIsInternet();

        // указываем слушатель свайпов пользователя
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionIsInternet();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {entranceListCall.cancel(); } catch (Exception ex) {}
        try {orderCardListCall.cancel(); } catch (Exception ex) {}
    }

    private void actionIsInternet() {
        if (Utils.isNetworkAvailable(this)) {
            list_view_works_at.setVisibility(View.VISIBLE);
            linearNoConnectionInternet.removeView(mTexrView);
            getListEntrance();
        } else {
            linearNoConnectionInternet.removeView(mTexrView);
            refreshLayout.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
            list_view_works_at.setVisibility(View.INVISIBLE);
            progressBar_works_at.setVisibility(View.INVISIBLE);
            mTexrView = new TextView(this);
            linearNoConnectionInternet.removeView(mTexrView);
            mTexrView.setText("Нет доступа к сети.\n" +
                    "Проверьте, есть ли доступ к Интернет через Ваше мобильное устройство");
            mTexrView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mTexrView.setPadding(7,7,7,7);
            mTexrView.setTextSize(15);
            linearNoConnectionInternet.addView(mTexrView);
        }

    }

    // создание шапки
    View createHeader() {
        View view = getLayoutInflater().inflate(R.layout.header_list_view_work_at, null);
        textView_adress = (TextView) view.findViewById(R.id.textView_adress);
        textView_number = (TextView) view.findViewById(R.id.textView_number);
        text_view_header_list_order_card = (TextView) view.findViewById(R.id.text_view_header_list_order_card);
        linearLayout_entranceto = (LinearLayout) view.findViewById(R.id.linearLayout_entranceto);
        divider_bootm = (View) view.findViewById(R.id.divider_bootm);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if(requestCode == 10) {
            Log.d("onActivityResult_10", "All ok");
            progressBar_works_at.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.INVISIBLE);
            getListEntrance();
        }
    }

    //подгатовка запроса
    private void getListEntrance() {

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
                            loadListEntrance();
                            LoadListOrderCard();
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
                loadListEntrance();
                LoadListOrderCard();
            }
        }
    }

    //сортировка массива
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

    //запрос списка
    private void LoadListOrderCard() {
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        orderCardListCall = serviceApiClient
                .getOrderCardList(guid,
                        "Bearer " + token);
        orderCardListCall.enqueue(new Callback<OrderCardList>() {
            @Override
            public void onResponse(Call<OrderCardList> call, Response<OrderCardList> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        OrderCardList orderCardList = response.body();
                        List<Data> dataList;
                        final OrderCardListAdapter orderCardListAdapter;
                        //если пришел пустой массив списка ЗН - прячем надпись "Список заказ-нарядов"
                        if(orderCardList.getData() != null & !orderCardList.getData().isEmpty()) {
                            dataList = orderCardList.getData();
                        } else {
                            dataList = new ArrayList<>();
                            text_view_header_list_order_card.setVisibility(View.GONE);
                            divider_bootm.setVisibility(View.GONE);
                        }
                        if (dataList != null) {

                            String DATE_FORMAT_NOW = "yyyy-MM-dd'T'HH:mm:ss";
                            final DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);

                            //сортировка по дате, адресу и номеру дома
                            Collections.sort(dataList, new Comparator<Data>() {
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

                            orderCardListAdapter = new OrderCardListAdapter(mContect, sortListData(dataList));
                            list_view_works_at.setAdapter(orderCardListAdapter);
                            //Для коректной работы list_view и refreshLayout вместе
                            list_view_works_at.setOnScrollListener(new AbsListView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(AbsListView view, int scrollState) {
                                }
                                @Override
                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    if (list_view_works_at.getChildAt(0) != null) {
                                        refreshLayout.setEnabled(list_view_works_at.getFirstVisiblePosition() == 0 && list_view_works_at.getChildAt(0).getTop() == 0);
                                    }
                                }
                            });
                            list_view_works_at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    View mLine_is_view = (View)  view.findViewById(R.id.line_is_view);
                                    mLine_is_view.setBackgroundColor(Color.parseColor("#ffffff"));
                                    Intent intent = new Intent("ru.android.cyfral.servisnik.card");
                                    intent.putExtra(Constants.SETTINGS.GUID, orderCardListAdapter.getData(position-1).getId());
                                    startActivityForResult(intent, 10);
                                }
                            });

                        }
                        progressBar_works_at.setVisibility(View.INVISIBLE);
                        refreshLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                    } else {
                        //сервер вернул ошибку от АПИ
                        refreshLayout.setRefreshing(false);
                        progressBar_works_at.setVisibility(View.INVISIBLE);
                        showErrorDialog(response.body().getErrors().getCode());
                    }
                } else {
                    //сервер вернул ошибку
                    refreshLayout.setRefreshing(false);
                    progressBar_works_at.setVisibility(View.INVISIBLE);
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
                progressBar_works_at.setVisibility(View.INVISIBLE);
                //refreshLayout.setVisibility(View.VISIBLE);
                showErrorDialog("");
            }
        });


    }

    private void showCityAndAdress(EntranceList entranceList) {


        String city = "";
        String cityType = "";
        String street = "";
        String streetType = "";
        String number = "";
        String letter = "";
        String building = "";

        try{
            if (!entranceList.getData().get(0).getAddress().getCity().equals("")
                    &!entranceList.getData().get(0).getAddress().getCity().equals("null")) {
                city = entranceList.getData().get(0).getAddress().getCity();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!entranceList.getData().get(0).getAddress().getCityType().equals("")
                    &!entranceList.getData().get(0).getAddress().getCityType().equals("null")) {
                cityType = entranceList.getData().get(0).getAddress().getCityType();
            }
        } catch (java.lang.NullPointerException ex) {}


        try{
            if (!entranceList.getData().get(0).getAddress().getStreet().equals("")
                    &!entranceList.getData().get(0).getAddress().getStreet().equals("null")) {
                street = entranceList.getData().get(0).getAddress().getStreet();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!entranceList.getData().get(0).getAddress().getStreetType().equals("")
                    &!entranceList.getData().get(0).getAddress().getStreetType().equals("null")) {
                streetType = entranceList.getData().get(0).getAddress().getStreetType();
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!entranceList.getData().get(0).getAddress().getNumber().equals("")
                    &!entranceList.getData().get(0).getAddress().getNumber().equals("null")) {
                number = "д."+entranceList.getData().get(0).getAddress().getNumber()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!entranceList.getData().get(0).getAddress().getLetter().equals("")
                    &!entranceList.getData().get(0).getAddress().getLetter().equals("null")) {
                letter = "л."+entranceList.getData().get(0).getAddress().getLetter()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        try{
            if (!entranceList.getData().get(0).getAddress().getBuilding().equals("")
                    &!entranceList.getData().get(0).getAddress().getBuilding().equals("null")) {
                building = "к." +entranceList.getData().get(0).getAddress().getBuilding()+" ";
            }
        } catch (java.lang.NullPointerException ex) {}

        textView_adress.setText(city + " "+
                cityType + " "+
                street + " " +
                streetType + " ");

        textView_number.setText(
                        number+
                        letter+
                        building
        );

    }

    private void loadListEntrance() {
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        entranceListCall = serviceApiClient
                .getListEntrance(guid,
                        "Bearer " + token);
        entranceListCall.enqueue(new Callback<EntranceList>() {
            @Override
            public void onResponse(Call<EntranceList> call, Response<EntranceList> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        linearLayout_entranceto.removeAllViews(); //отчищаем списко подъездов
                        EntranceList entranceList = response.body();
                        showCityAndAdress(entranceList);
                        progressBar_works_at.setVisibility(View.INVISIBLE);
                        refreshLayout.setVisibility(View.VISIBLE);
                        for(int i =0; i<entranceList.getData().get(0).getEntrances().size(); i++)
                        {
                            View content = LayoutInflater.from(mContect).inflate(R.layout.row_item_works_at, null);
                            btnEntrance = (Button) content.findViewById (R.id.button_entrance);
                            btnEntrance.setText(entranceList.getData().get(0).getEntrances().get(i).getNumber());
                            btnEntrance.setTag(entranceList.getData().get(0).getEntrances().get(i).getId());
                            btnEntrance.setOnClickListener(WorksAtActivity.this);
                            linearLayout_entranceto.addView(content);
                        }
                        refreshLayout.setRefreshing(false);

                    } else {
                        //сервер вернул ошибку от АПИ
                        refreshLayout.setRefreshing(false);
                        progressBar_works_at.setVisibility(View.INVISIBLE);
                        //refreshLayout.setVisibility(View.VISIBLE);
                        showErrorDialog(response.body().getErrors().getCode());
                    }
                } else {
                    //сервер вернул ошибку
                    refreshLayout.setRefreshing(false);
                    progressBar_works_at.setVisibility(View.INVISIBLE);
                   // refreshLayout.setVisibility(View.VISIBLE);
                    int rc = response.code();
                    if (rc == 401) {
                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<EntranceList> call, Throwable t) {
                //Произошла непредвиденная ошибка
                refreshLayout.setRefreshing(false);
                progressBar_works_at.setVisibility(View.INVISIBLE);
                //refreshLayout.setVisibility(View.VISIBLE);
                showErrorDialog("");
            }
        });
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
        } catch (Exception ex) {}
    }

    @Override
    public void onClick(View view) {

        if (view.getTag() != null) {
            OrderCard currentOrderCard = new OrderCard();
            ru.android.cyfral.servisnik.model.orderCard.Data data = new ru.android.cyfral.servisnik.model.orderCard.Data();
            data.setEntranceId(view.getTag().toString());
            currentOrderCard.setData(data);
            Intent intent = new Intent("ru.android.cyfral.servisnik.infoentrance");
            intent.putExtra("ordercard", currentOrderCard);
            startActivityForResult(intent, 10);

        };

    }
}
