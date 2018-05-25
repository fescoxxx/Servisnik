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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchInfoEntranceListener;
import ru.android.cyfral.servisnik.model.InfoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;


public class InfoEntranceActivity extends AppCompatActivity implements DataFetchInfoEntranceListener {
    private ProgressBar mProgressBar;
    private OrderCard currentOrderCard;
    private SharedPreferences sPref;
    private LinearLayout mLinearLayout;
    private Button ppr_ok_button;
    private TextView date_ppr_text; //дата ппр
    private TextView header_sity_info_entrance; //город улица
    private TextView decr_home_info_entrance; //дом литера крвартира
    private String guid;

    private static DataDatabase mDatabase; //База данных

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<InfoEntrance> getInfoEntranceCall;
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
        ppr_ok_button = (Button) findViewById(R.id.ppr_ok_button);
        date_ppr_text = (TextView) findViewById(R.id.date_ppr_text);
        header_sity_info_entrance = (TextView) findViewById(R.id.header_sity_info_entrance);
        decr_home_info_entrance = (TextView) findViewById(R.id.decr_home_info_entrance);
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

    private void getInfoEntranceDataBase() {
        mDatabase.fethcDatasForInfoEntrance(this, guid);
        try {
            mProgressBar.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.VISIBLE);
            ppr_ok_button.setVisibility(View.VISIBLE);
        } catch (java.lang.NullPointerException ex) {}
    }

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
                Call<RefreshToken> callRedresh = tokenClient.refreshToken("refresh_token",
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

    private void showInfoEntrance(InfoEntrance currentInfoEntrance){
        mProgressBar.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        ppr_ok_button.setVisibility(View.VISIBLE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
        SimpleDateFormat format_data = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date datePpr = format.parse(currentInfoEntrance.getData().getLastPprDate());
            date_ppr_text.setText(format_data.format(datePpr));
        } catch (ParseException e) {
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
        header_sity_info_entrance.setText(currentInfoEntrance.getData().getAddress().getCityType()+ " "+
                currentInfoEntrance.getData().getAddress().getCity() + " "+
                currentInfoEntrance.getData().getAddress().getStreetType() + " " +
                currentInfoEntrance.getData().getAddress().getStreet() + " ");

        decr_home_info_entrance.setText(
                        dom+
                        litera+
                        building+
                        entrance +
                        floor +
                        apartment +
                        room);

    }
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
                        SaveIntoDatabaseOdrerCard task = new SaveIntoDatabaseOdrerCard();
                        task.execute(currentInfoEntrance);
                    } else {
                        //сервер вернул ошибку от АПИ
                        mProgressBar.setVisibility(View.INVISIBLE);
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
                mProgressBar.setVisibility(View.INVISIBLE);
                showErrorDialog("");
            }
        });

    }

    private void showErrorDialog(String code) {
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
        showInfoEntrance(infoEntrance);
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
