package ru.android.cyfral.servisnik.ui.executionresult;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.StandartAnswer;
import ru.android.cyfral.servisnik.model.executionresult.result.TmcResultAtapter;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.executionresult.result.putResult.PutResult;
import ru.android.cyfral.servisnik.model.executionresult.result.putResult.Works;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class ExecutionResultActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
    private ProgressDialog mDialog; //анимация загрузки
    private static Call<GetResult> getResultCall;
    private String guid;
    SharedPreferences sPref;
    private TmcResultAtapter mAdapter;
    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private TextView group_result;
    private TextView element_result;
    private TextView type_result;
    private Button put_result_button; //отправка результатов ЗН
    private ImageButton works_result_button; //выбор работ
    private ImageButton choise_tmc_button; //выбор ТМЦ

    private Double latitude;
    private Double longitude;

    private GetResult currentResult;
    private GetResult newResult;
    private LocationManager locationManager;
    Call<StandartAnswer> putResultCall;
    private static final int PERMISSION_REQUEST = 1;
    public Location location;
    Context mContext;

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;//1000 * 60 * 1; // 1 minute

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //изменение работ
        if (requestCode == 1) {

            if (data == null) {
                return;
            }
            currentResult = (GetResult) data.getExtras().getSerializable("currentResult");
            group_result.setText(currentResult.getData().getWorks().getGroup().getName());
            element_result.setText(currentResult.getData().getWorks().getElement().getName());
            type_result.setText(currentResult.getData().getWorks().getType().getName());
            currentResult.getData().getTmas().clear();
            mAdapter.removeAll();
        }
        //выбор ТМЦ
        else if (requestCode == 4) {
            if (data == null) {
                return;
            }
            currentResult = (GetResult) data.getExtras().getSerializable("currentResult");
            mAdapter.setCurrentResult(currentResult);
            mAdapter.removeAll();
            for (int i = 0; i < currentResult.getData().getTmas().size(); i++) {
                mAdapter.addItem(currentResult.getData().getTmas().get(i));
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution_result);
        mContext = this;
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_execution_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_execution_result);
        setTitle("Результаты выполнения ЗН");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);

        group_result = (TextView) findViewById(R.id.group_result);
        element_result = (TextView) findViewById(R.id.element_result);
        type_result = (TextView) findViewById(R.id.type_result);
        put_result_button = (Button) findViewById(R.id.put_result_button);
        works_result_button = (ImageButton) findViewById(R.id.works_result_button);
        choise_tmc_button = (ImageButton) findViewById(R.id.choise_tmc_button);



        Intent intent = getIntent();

        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");
        guid = orderCard.getData().getId();
        getResult(guid);
        put_result_button.setOnClickListener(this);
        works_result_button.setOnClickListener(this);
        choise_tmc_button.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            locationManager.removeUpdates(locationListener);
        } catch (NullPointerException ex) {}
    }
    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Геолокация отключена");
            alertDialog.setMessage("Параметры местоположения не включены. Пожалуйста, включите их в меню настроек.");
            alertDialog.setPositiveButton("Перейти в настройки", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor ed = myPrefs.edit();
            ed.putString(Constants.SETTINGS.LATITUDE, String.valueOf(latitude));
            ed.putString(Constants.SETTINGS.LONGITUDE, String.valueOf(longitude));
            ed.apply();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            //Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("loc_", provider);
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("loc_", provider);

        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("loc_", provider);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isLocationEnabled();
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("isGPSEnabled", String.valueOf(isGPSEnabled));
            Log.d("isNetworkEnabled", String.valueOf(isNetworkEnabled));

            if (loadTextPref(Constants.SETTINGS.LATITUDE).equals("") |
                    loadTextPref(Constants.SETTINGS.LONGITUDE).equals("")) {
                SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor ed = myPrefs.edit();
                ed.putString(Constants.SETTINGS.LATITUDE, "0.0");
                ed.putString(Constants.SETTINGS.LONGITUDE, "0.0");
                ed.apply();
            }


            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        10000,
                        0, locationListener);
                if (locationManager != null)   {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }//end if

            if (isGPSEnabled)  {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,
                        0, locationListener);

                if (locationManager != null)  {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //   latitude = location.getLatitude();
                    //  longitude = location.getLongitude();
                }
            }

        }


            /*Log.d("isGPSEnabled", String.valueOf(isGPSEnabled));
            Log.d("isNetworkEnabled", String.valueOf(isNetworkEnabled));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            showLocation(location);*/





        // checkEnabled();
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


    private boolean validadatorPutResult() {

        boolean errorGroup = true;
        boolean errorElement= true;
        boolean errorType= true;


        try {
            if (currentResult.getData().getWorks().getGroup() != null) {
                errorGroup = false;
            } else {
                this.group_result.setHintTextColor(Color.parseColor("#FF0606"));
            }
        } catch (NullPointerException ex) {
            this.group_result.setHintTextColor(Color.parseColor("#FF0606"));
        }
        try {
            if (currentResult.getData().getWorks().getElement() != null) {
                errorElement = false;
            } else {
                this.element_result.setHintTextColor(Color.parseColor("#FF0606"));
            }
        } catch (NullPointerException ex) {
            this.element_result.setHintTextColor(Color.parseColor("#FF0606"));
        }
        try {
            if (currentResult.getData().getWorks().getType() != null) {
                errorType = false;
            } else {
                this.type_result.setHintTextColor(Color.parseColor("#FF0606"));
            }
        } catch (NullPointerException ex) {
            this.type_result.setHintTextColor(Color.parseColor("#FF0606"));
        }
        if (errorGroup == false & errorElement == false & errorType == false) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //отправка результатов
            case R.id.put_result_button:

                if(validadatorPutResult()) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(this);
                    ad.setMessage("Вы уверены, что хотите отправить результаты выполнения заказ-наряда?"); // сообщение
                    ad.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            mDialog = new ProgressDialog(ExecutionResultActivity.this);
                            mDialog.setMessage("Отправка данных...");
                            mDialog.setCancelable(true);
                            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mDialog.setIndeterminate(true);
                            mDialog.show();
                            PutResult putResult = new PutResult();
                            Works works = new Works();
                            works.setGroupID(
                                    currentResult
                                            .getData()
                                            .getWorks()
                                            .getGroup()
                                            .getId());
                            works.setElementID(
                                    currentResult
                                    .getData()
                                    .getWorks()
                                    .getElement()
                                    .getId());
                            works.setTypeID(
                                    currentResult
                                    .getData()
                                    .getWorks()
                                    .getType()
                                    .getId());
                            putResult.setWorks(works);
                            putResult.setClosed("true");
                            List<String> listtmas = new ArrayList<>();
                            for(int i =0; i<currentResult.getData().getTmas().size(); i++) {
                                listtmas.add(currentResult.getData().getTmas().get(i).getId());
                            }
                            putResult.setTMAs(listtmas);

                            putResult.setLatitude(loadTextPref(Constants.SETTINGS.LATITUDE));
                            putResult.setLongitude(loadTextPref(Constants.SETTINGS.LONGITUDE));
                            Log.d("res_log",loadTextPref(Constants.SETTINGS.LATITUDE));
                            Log.d("res_log",loadTextPref(Constants.SETTINGS.LONGITUDE));
                            String token = loadTextPref(Constants.SETTINGS.TOKEN);
                            putResultCall = serviceApiClient.putResult(putResult,"Bearer " + token, guid);
                            putResultCall.enqueue(new Callback<StandartAnswer>() {
                                @Override
                                public void onResponse(Call<StandartAnswer> call, Response<StandartAnswer> response) {
                                    if (response.isSuccessful()) {
                                        mDialog.dismiss();
                                        if(response.body().getIsSuccess().equals("true")){
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            mDialog.dismiss();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<StandartAnswer> call, Throwable t) {
                                    mDialog.dismiss();
                                    Log.d("res_log_error", t.toString());
                                }
                            });

                        }
                    });
                    ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            Log.d("validadatorPutResult", "не Отправлено");
                        }
                    });
                    ad.show();
                } else {
                    Log.d("validadatorPutResult", "Ошибка");
                }


                break;
            //изменение работ
            case R.id.works_result_button:
                Intent intent = new Intent("ru.android.cyfral.servisnik.choisegroup");
                intent.putExtra("currentResult", currentResult);
                startActivityForResult(intent, 1);
                break;
            //Выбор ТМЦ
            case R.id.choise_tmc_button:
                if (validadatorPutResult()) {
                    Intent intenttmc = new Intent("ru.android.cyfral.servisnik.choistmc");
                    intenttmc.putExtra("currentResult", currentResult);
                    startActivityForResult(intenttmc, 4);
                }
                break;
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


    private void loadResult() {
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        getResultCall = serviceApiClient.getResult(guid, "Bearer " + token);
        getResultCall.enqueue(new Callback<GetResult>() {
            @Override
            public void onResponse(Call<GetResult> call, Response<GetResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        currentResult = response.body();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mLinearLayout.setVisibility(View.VISIBLE);
                        try {
                            group_result.setText(currentResult.getData().getWorks().getGroup().getName());
                        } catch (java.lang.NullPointerException ex) {}

                        try {
                            element_result.setText(currentResult.getData().getWorks().getElement().getName());
                        } catch (java.lang.NullPointerException ex) {}

                        try {
                            type_result.setText(currentResult.getData().getWorks().getType().getName());
                        } catch (java.lang.NullPointerException ex) {}

                        List<ru.android.cyfral.servisnik.model.executionresult.result.getResult.Tmas> listTmas =currentResult.getData().getTmas();
                        mAdapter = new TmcResultAtapter(ExecutionResultActivity.this, currentResult);
                        ListView lv_tmc_result = (ListView) findViewById(R.id.lv_tmc_result);

                        if (!listTmas.isEmpty()) {
                            for (int i = 0; i<listTmas.size(); i++) {
                                mAdapter.addItem(listTmas.get(i));
                            }
                        }
                        lv_tmc_result.setAdapter(mAdapter);

                    } else {
                        //сервер вернул ошибку от АПИ
                        mProgressBar.setVisibility(View.INVISIBLE);
                        showErrorDialog(response.body().getErrors().getCode());
                    }
                } else {
                    //сервер вернул ошибку
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showErrorDialog(String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<GetResult> call, Throwable t) {
                //Произошла непредвиденная ошибка
                mProgressBar.setVisibility(View.INVISIBLE);
                showErrorDialog("");
            }
        });

    }

    private void getResult(String guid) {
        //mProgressBar.setVisibility(View.INVISIBLE);
        //mLinearLayout.setVisibility(View.VISIBLE);

        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        String token_ref = loadTextPref(Constants.SETTINGS.REFRESH_TOKEN);
        String life_time_token = loadTextPref(Constants.SETTINGS.DATE_TOKEN);
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
                Log.d("life_time_date_token4", " Новая дата позже"+date_now.toString() + "      "+ date_ltt.toString());
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
                            ed.putString(Constants.SETTINGS.TOKEN,  response.body().getAccess_token());
                            ed.putString(Constants.SETTINGS.REFRESH_TOKEN,  response.body().getRefresh_token());
                            Calendar date = Calendar.getInstance();
                            long t = date.getTimeInMillis();
                            Date life_time_date_token =
                                    new Date(t+(Constants.SETTINGS.ONE_SECUNDE_IN_MILLIS
                                            *Integer.valueOf(response.body().getExpires_in())));
                            ed.putString(Constants.SETTINGS.DATE_TOKEN, getFormatDate(life_time_date_token));
                            ed.apply();
                            loadResult();
                        } else {
                            showErrorDialog(String.valueOf(response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<RefreshToken> call, Throwable t) {
                        showErrorDialog("");

                    }
                });

            } else {
                //токен живой
                loadResult();
            }
        }
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

}
