package ru.android.cyfral.servisnik.ui.executionresult;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import ru.android.cyfral.servisnik.model.executionresult.choisetype.ChoiseType;
import ru.android.cyfral.servisnik.model.executionresult.choisetype.ChoiseTypeAdapter;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.GetResult;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;
import ru.android.cyfral.servisnik.model.executionresult.choisetype.Data;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.Type;

public class ChoiceTypeActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private ListView lv_choice_type;
    private GetResult currentResult;
    private SharedPreferences sPref;
    private Call<ChoiseType> getChoiseType;
    private  Call<RefreshToken> callRedresh;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_type);

        setTitle("Выберите вид работ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_choice_type);
        lv_choice_type = (ListView) findViewById(R.id.lv_choice_type);

        Intent intent = getIntent();
        GetResult getResult = (GetResult) intent.getExtras().getSerializable("currentResult");
        currentResult = getResult;

        getChoiseType();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {getChoiseType.cancel(); } catch (Exception ex) {}
    }
    private List<Data> getSortArray(List<Data> listData) {
        try {
            List<Data> newArray = new ArrayList<>();
            for (int i=0; i<listData.size(); i++) {
                if(listData.get(i).getName().equals(currentResult.getData().getWorks().getType().getName())) {
                    newArray.add(listData.get(i));
                }
            }
            for (int i=0; i<listData.size(); i++) {
                if(!listData.get(i).getName().equals(currentResult.getData().getWorks().getType().getName())) {
                    newArray.add(listData.get(i));
                }
            }
            return newArray;
        } catch (NullPointerException ex) {
            return listData;
        }
    }

    private void loadChoiseType() {
        String token = loadTextPref(Constants.SETTINGS.TOKEN);

        getChoiseType = serviceApiClient
                .getChoiseType(
                        currentResult
                                .getData()
                                .getWorks()
                                .getElement()
                                .getId(),
                        "Bearer " + token);

        getChoiseType.enqueue(new Callback<ChoiseType>() {
            @Override
            public void onResponse(Call<ChoiseType> call, Response<ChoiseType> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        lv_choice_type.setVisibility(View.VISIBLE);

                        List<Data> listData = new ArrayList<>();
                        listData = getSortArray(response.body().getData());
                        final ChoiseTypeAdapter mAdapter = new ChoiseTypeAdapter(ChoiceTypeActivity.this, currentResult);
                        ListView lv_choice_type = (ListView) findViewById(R.id.lv_choice_type);
                        if (!listData.isEmpty()) {
                            for (int i =0; i<listData.size(); i++) {
                                mAdapter.addData(listData.get(i));
                            }
                        }
                        lv_choice_type.setAdapter(mAdapter);
                        //обработчик listView
                        lv_choice_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                Type type = new Type();
                                type.setId(mAdapter.getItem(position).getId());
                                type.setName(mAdapter.getItem(position).getName());
                                currentResult.getData().getWorks().setType(type);

                                Intent intent = new Intent();
                                intent.putExtra("currentResult", currentResult);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });

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
            public void onFailure(Call<ChoiseType> call, Throwable t) {
                //Произошла непредвиденная ошибка
                mProgressBar.setVisibility(View.INVISIBLE);
                showErrorDialog("");
            }
        });
    }

    private void getChoiseType() {

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
                            loadChoiseType();
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
                loadChoiseType();
            }
        }
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

    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
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
