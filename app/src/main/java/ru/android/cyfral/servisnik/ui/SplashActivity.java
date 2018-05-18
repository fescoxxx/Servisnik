package ru.android.cyfral.servisnik.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

/**
 * Created by joe on 30.04.2018.
 */
public class SplashActivity extends AppCompatActivity {

    SharedPreferences sPref;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d("Splash_log", "Запуск splash");
        checkToken();
    }


    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }

    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private void checkToken() {
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
                //Токен просрочен
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
                            startActivity(new Intent("ru.android.cyfral.servisnik.repair"));
                            finish();
                        } else {
                            startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<RefreshToken> call, Throwable t) {
                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                        finish();
                    }
                });

            } else {
                //Токен ещё живой
                startActivity(new Intent("ru.android.cyfral.servisnik.repair"));
                finish();
                Log.d("life_time_date_token4", " Новая дата раньше"+date_now.toString() + "      "+ date_ltt.toString());
            }

/*            Call<RepairRequest> call = serviceApiClient.repairRequests("Bearer "+token);
            Log.d("Splash_call", "");
            call.enqueue(new Callback<RepairRequest>() {
                @Override
                public void onResponse(Call<RepairRequest> call, Response<RepairRequest> response) {
                    if (response.isSuccessful()) {
                        startActivity(new Intent("ru.android.cyfral.servisnik.repair"));
                        finish();
                        Log.d("repairRequests", response.message());
                    } else {
                        int sc = response.code();
                        switch (sc) {
                            case 401:
                                Log.d("case 401", response.message());
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
                                            ed.putString("token",  response.body().getAccess_token());
                                            ed.putString("token_refresh",  response.body().getRefresh_token());
                                            Calendar date = Calendar.getInstance();
                                            long t = date.getTimeInMillis();
                                            Date life_time_date_token =
                                                    new Date(t+(Constants.SETTINGS.ONE_SECUNDE_IN_MILLIS
                                                            *Integer.valueOf(response.body().getExpires_in())));
                                            ed.putString(Constants.SETTINGS.DATE_TOKEN, getFormatDate(life_time_date_token));
                                            ed.apply();
                                            startActivity(new Intent("ru.android.cyfral.servisnik.repair"));
                                            finish();
                                        } else {
                                            startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                            finish();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<RefreshToken> call, Throwable t) {
                                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                        finish();
                                    }
                                });
                                break;
                            default:
                                Log.d("default", response.message());
                                startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                                finish();
                        }
                    }
                }
                @Override
                public void onFailure(Call<RepairRequest> call, Throwable t) {
                    Log.d("repairRequests", t.getMessage());
                    startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                    finish();
                }
            });
        */
        }
    }
}