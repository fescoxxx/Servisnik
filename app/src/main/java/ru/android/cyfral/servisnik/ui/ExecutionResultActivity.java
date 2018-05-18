package ru.android.cyfral.servisnik.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class ExecutionResultActivity extends AppCompatActivity {

    private LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
    private static Call<GetResult> getResultCall;
    private String guid;
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
        setContentView(R.layout.activity_execution_result);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_execution_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_execution_result);
        setTitle("Результаты выполнения ЗН");
        mProgressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");
        guid = orderCard.getData().getId();
        getResult(guid);
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
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mLinearLayout.setVisibility(View.VISIBLE);
                        //Log.d("loadResultm", response.body().getData().getTmas());


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
            Toast toast = Toast.makeText(getApplicationContext(),
                    guid, Toast.LENGTH_SHORT);
            toast.show();
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
