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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
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
import ru.android.cyfral.servisnik.model.executionresult.choisetmc.ChoiseTmc;
import ru.android.cyfral.servisnik.model.executionresult.choisetmc.ChoiseTmcAdapter;
import ru.android.cyfral.servisnik.model.executionresult.choisetmc.Data;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.Tmas;
import ru.android.cyfral.servisnik.model.listwork.worksat.entrancelist.EntranceList;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;
import ru.android.cyfral.servisnik.ui.executionresult.ChoiceTMCActivity;

public class WorksAtActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout mLinearLayout;
    private SharedPreferences sPref;
    private String guid; //houseID текущий
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar_works_at;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<EntranceList> entranceListCall;
    private LinearLayout linearLayout_entranceto;
    private Button btnEntrance;
    private Context mContect;

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
        linearLayout_entranceto = (LinearLayout) findViewById(R.id.linearLayout_entranceto);

        Intent intent = getIntent();
        guid = intent.getStringExtra("GUID");

        progressBar_works_at.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.INVISIBLE);
        getListEntrance();
        // указываем слушатель свайпов пользователя
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListEntrance();
            }
        });


    }

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


    private void LoadListOrderCard() {

    }

    private void loadListEntrance() {
        linearLayout_entranceto.removeAllViews(); //отчищаем списко подъездов
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        entranceListCall = serviceApiClient
                .getListEntrance(guid,
                        "Bearer " + token);
        entranceListCall.enqueue(new Callback<EntranceList>() {
            @Override
            public void onResponse(Call<EntranceList> call, Response<EntranceList> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        EntranceList entranceList = response.body();

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

    @Override
    public void onClick(View view) {

        if (view.getTag() != null) {

            Toast toast = Toast.makeText(getApplicationContext(),
                    view.getTag().toString(), Toast.LENGTH_SHORT);
            toast.show();
        };

    }
}
