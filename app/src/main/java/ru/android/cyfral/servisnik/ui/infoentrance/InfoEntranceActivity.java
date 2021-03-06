package ru.android.cyfral.servisnik.ui.infoentrance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchInfoEntranceListener;
import ru.android.cyfral.servisnik.model.infoEntrance.CallingDevice;
import ru.android.cyfral.servisnik.model.infoEntrance.Contacts;
import ru.android.cyfral.servisnik.model.infoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.infoEntrance.SpecialApartments;
import ru.android.cyfral.servisnik.model.infoEntrance.VideoService;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

//активити информация о подъезде
public class InfoEntranceActivity extends AppCompatActivity implements DataFetchInfoEntranceListener, View.OnClickListener {
    private ProgressBar mProgressBar;
    private OrderCard currentOrderCard;
    private SharedPreferences sPref;
    private LinearLayout mLinearLayout;

    private TextView date_ppr_text; //дата ппр
    private TextView header_sity_info_entrance; //город улица
    private TextView decr_home_info_entrance; //дом литера крвартира
    private String guid;

    private View call_block_button_divider;
    private View video_dervice_button_divider;
    private View access_equipment_button_divider;
    private View list_special_subscriber_button_divider;

    private View top_two_divider;


    private static DataDatabase mDatabase; //База данных

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<InfoEntrance> getInfoEntranceCall;
    private Call<RefreshToken> callRedresh;

    //кнопки
    private Button video_dervice_button;
    private Button access_equipment_button;
    private Button list_special_subscriber_button;
    private Button call_block_button;
    private Button ppr_ok_button;

    private InfoEntrance currentInfoEntrance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_entrance);
        setTitle("Информация о подъезде");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDatabase = new DataDatabase(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_info_entrance);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_info_entrance);
        date_ppr_text = (TextView) findViewById(R.id.date_ppr_text);
        header_sity_info_entrance = (TextView) findViewById(R.id.header_sity_info_entrance);
        decr_home_info_entrance = (TextView) findViewById(R.id.decr_home_info_entrance);

        ppr_ok_button = (Button) findViewById(R.id.ppr_ok_button);
        video_dervice_button = (Button) findViewById(R.id.video_dervice_button);
        access_equipment_button = (Button) findViewById(R.id.access_equipment_button);
        list_special_subscriber_button = (Button) findViewById(R.id.list_special_subscriber_button);
        call_block_button = (Button) findViewById(R.id.call_block_button);

        call_block_button_divider = (View)  findViewById(R.id.call_block_button_divider);
        video_dervice_button_divider = (View)  findViewById(R.id.video_dervice_button_divider);
        access_equipment_button_divider  = (View)  findViewById(R.id.access_equipment_button_divider);
        list_special_subscriber_button_divider = (View)  findViewById(R.id.list_special_subscriber_button_divider);

        top_two_divider =  (View)  findViewById(R.id.top_two_divider);

        video_dervice_button.setOnClickListener(this);
        access_equipment_button.setOnClickListener(this);
        list_special_subscriber_button.setOnClickListener(this);
        call_block_button.setOnClickListener(this);
        ppr_ok_button.setOnClickListener(this);

        mProgressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        ppr_ok_button.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");
        currentOrderCard = orderCard;
        guid = currentOrderCard.getData().getEntranceId(); //текущай guid

        if (Utils.isNetworkAvailable(this)) {
            getInfoEntrance();
        } else {
            getInfoEntranceDataBase();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {getInfoEntranceCall.cancel(); } catch (Exception ex) {}
    }

    @Override
    public void onClick(View v) {
        Log.d("onClick", v.toString());
        switch (v.getId()) {
            //видеосервис
            case R.id.video_dervice_button:
                try {
                    Intent intent = new Intent("ru.android.cyfral.servisnik.videoservice");
                    intent.putExtra("infoentrance", currentInfoEntrance);
                    startActivity(intent);
                } catch (NullPointerException ex) {}
                break;
            case R.id.access_equipment_button:
                //  доступ к оборудованию
                try {
                    Intent intent = new Intent("ru.android.cyfral.servisnik.accessequipment");
                    intent.putExtra("infoentrance", currentInfoEntrance);
                    startActivity(intent);
                } catch (NullPointerException ex) {}
                break;

            case R.id.call_block_button:
                //  вызывной блок
                try {
                    Intent intent = new Intent("ru.android.cyfral.servisnik.callblock");
                    intent.putExtra("infoentrance", currentInfoEntrance);
                    startActivity(intent);
                } catch (NullPointerException ex) {}
                break;
            case R.id.list_special_subscriber_button:
                //  список особых абонентов
                try {
                    Intent intent = new Intent("ru.android.cyfral.servisnik.specialsubscriber");
                    intent.putExtra("infoentrance", currentInfoEntrance);
                    startActivity(intent);
                } catch (NullPointerException ex) {}
                break;
           }
    }

    //получить список из БД
    private void getInfoEntranceDataBase() {
        mDatabase.fethcDatasForInfoEntrance(this, guid);
        try {
            mProgressBar.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.VISIBLE);
            ppr_ok_button.setVisibility(View.VISIBLE);
        } catch (java.lang.NullPointerException ex) {}
    }

    //подготовка запроса
    private void getInfoEntrance() {


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
                            loadInfoEntrance();
                        } else {
                            //сервер вернул ошибку
                            mProgressBar.setVisibility(View.INVISIBLE);
                            int rc = response.code();
                            if (rc == 401) {
                                startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RefreshToken> call, Throwable t) {
                        showErrorDialog("");
                    }
                });

            } else {
                //токен живой
                loadInfoEntrance();
            }
        }
    }

    //видимость кнопок
    private void checkVisibleButton(InfoEntrance infoEntrance) {
        List<Contacts> contactsList = infoEntrance.getData().getContacts();
        List<VideoService> videoServiceList = infoEntrance.getData().getVideoService();
        List<CallingDevice> callingDevicesList = infoEntrance.getData().getCallingDevice();
        List<SpecialApartments> specialApartmentsList =  infoEntrance.getData().getSpecialApartments();
        if (contactsList.isEmpty()) {
            access_equipment_button.setVisibility(View.GONE);
            access_equipment_button_divider.setVisibility(View.GONE);
        }

        if (videoServiceList.isEmpty()) {
            video_dervice_button.setVisibility(View.GONE);
            video_dervice_button_divider.setVisibility(View.GONE);
        }

        if (callingDevicesList.isEmpty()) {
            call_block_button.setVisibility(View.GONE);
            call_block_button_divider.setVisibility(View.GONE);
        }

        if (specialApartmentsList.isEmpty()) {
            list_special_subscriber_button.setVisibility(View.GONE);
            list_special_subscriber_button_divider.setVisibility(View.GONE);
        }

        if (contactsList.isEmpty()
                & videoServiceList.isEmpty()
                & callingDevicesList.isEmpty()
                & specialApartmentsList.isEmpty()) {
            top_two_divider.setVisibility(View.GONE);
        }
    }

    //показать список
    private void showInfoEntrance(InfoEntrance infoEntrance){
        currentInfoEntrance = infoEntrance;
        checkVisibleButton(currentInfoEntrance);
        mProgressBar.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        ppr_ok_button.setVisibility(View.VISIBLE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
        SimpleDateFormat format_data = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date datePpr = format.parse(currentInfoEntrance.getData().getLastPprDate());
            date_ppr_text.setText(format_data.format(datePpr));
        } catch (Exception e) {
            date_ppr_text.setText("Не было");
        }
        String str = "";
        String litera="";
        String building="";
        String floor="";
        String room="";
        String dom = "";
        String entrance = "";
        String apartment = "";

        try {
            if (!currentInfoEntrance.getData().getAddress().getLetter().equals("null") &
                    !currentInfoEntrance.getData().getAddress().getLetter().equals("")
                    ) {
                litera = "л."+currentInfoEntrance.getData().getAddress().getLetter()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!currentInfoEntrance.getData().getAddress().getBuilding().equals("null") &
                    !currentInfoEntrance.getData().getAddress().getBuilding().equals("")) {
                building = "к."+currentInfoEntrance.getData().getAddress().getBuilding()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}


        try {
            if (!currentInfoEntrance.getData().getAddress().getNumber().equals("null") &
                    !currentInfoEntrance.getData().getAddress().getNumber().equals("")) {
                dom = "д."+currentInfoEntrance.getData().getAddress().getNumber() + " ";
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!currentInfoEntrance.getData().getAddress().getEntrance().equals("null") &
                    !currentInfoEntrance.getData().getAddress().getEntrance().equals("")
                    ) {
                entrance = "п."+currentInfoEntrance.getData().getAddress().getEntrance()+ " ";
            }
        } catch (java.lang.NullPointerException ex) {}

        String cityType = currentInfoEntrance.getData().getAddress().getCityType();
        String city = currentInfoEntrance.getData().getAddress().getCity();
        String streetType = currentInfoEntrance.getData().getAddress().getStreetType();
        String street = currentInfoEntrance.getData().getAddress().getStreet();

        header_sity_info_entrance.setText(city+ " "+
                cityType+ " "+
                street + " " +
                streetType + " ");

        decr_home_info_entrance.setText(
                        dom+
                        litera+
                        building+
                        entrance +
                        floor +
                        apartment +
                        room);

    }

    //запрос списка
    private void loadInfoEntrance() {

        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        getInfoEntranceCall = serviceApiClient
                .getInfoEntrance(currentOrderCard
                                .getData().getEntranceId(),
                        "Bearer " + token);
        getInfoEntranceCall.enqueue(new Callback<InfoEntrance>() {
            @Override
            public void onResponse(Call<InfoEntrance> call, Response<InfoEntrance> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        //корректное получение объекта
                        InfoEntrance currentInfoEntrance = response.body();
                        showInfoEntrance(currentInfoEntrance);
                        //сохранение объекта в БД
                        mLinearLayout.setVisibility(View.VISIBLE);
                        ppr_ok_button.setVisibility(View.VISIBLE);
                        SaveIntoDatabaseOdrerCard task = new SaveIntoDatabaseOdrerCard();
                        task.execute(currentInfoEntrance);
                    } else {
                        //сервер вернул ошибку от АПИ
                        showErrorDialog(response.body().getErrors().getCode());
                    }
                } else {
                    //сервер вернул ошибку
                    mProgressBar.setVisibility(View.INVISIBLE);
                    int rc = response.code();
                    if (rc == 401) {
                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<InfoEntrance> call, Throwable t) {
                //Произошла непредвиденная ошибка
                showErrorDialog("");
            }
        });

    }

    private void showErrorDialog(String code) {
        try {
            mProgressBar.setVisibility(View.INVISIBLE);
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

    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }

    //кнопка назад
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

    @Override
    public void onDeliverData(InfoEntrance infoEntrance) {
        if (infoEntrance.getData() == null) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.INVISIBLE);
            ppr_ok_button.setVisibility(View.INVISIBLE);
        } else {
            mLinearLayout.setVisibility(View.VISIBLE);
            ppr_ok_button.setVisibility(View.VISIBLE);
            showInfoEntrance(infoEntrance);
        }
    }



    public static class SaveIntoDatabaseOdrerCard extends AsyncTask<InfoEntrance, Void, Void> {
        private final String TAG = SaveIntoDatabaseOdrerCard.class.getSimpleName();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(InfoEntrance... params) {
            InfoEntrance infoEntrance = params[0];
            try {
                mDatabase.addDataInfoEntrance(infoEntrance);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return null;
        }
    }
}
