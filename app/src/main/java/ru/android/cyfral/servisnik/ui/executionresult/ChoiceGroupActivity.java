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
import ru.android.cyfral.servisnik.model.executionresult.choicegroup.ChoisGroupAdapter;
import ru.android.cyfral.servisnik.model.executionresult.choicegroup.ChoiseGroup;
import ru.android.cyfral.servisnik.model.executionresult.choicegroup.Data;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;
import ru.android.cyfral.servisnik.model.result.getResult.Group;

public class ChoiceGroupActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private ListView lv_choice_group;
    private GetResult currentResult;
    private SharedPreferences sPref;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private static Call<ChoiseGroup> getChoiseGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_group);
        setTitle("Выберите группу работ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_choice_group);
        lv_choice_group = (ListView)  findViewById(R.id.lv_choice_group);
        Intent intent = getIntent();
        GetResult getResult = (GetResult) intent.getExtras().getSerializable("currentResult");
        currentResult = getResult;
        getChoiseGroup();

    }
    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }

    private List<Data> getSortArray(List<Data> listData) {
        try {
            List<Data> newArray = new ArrayList<>();
            for (int i=0; i<listData.size(); i++) {
                if(listData.get(i).getName().equals(currentResult.getData().getWorks().getGroup().getName())) {
                    newArray.add(listData.get(i));
                }
            }
            for (int i=0; i<listData.size(); i++) {
                if(!listData.get(i).getName().equals(currentResult.getData().getWorks().getGroup().getName())) {
                    newArray.add(listData.get(i));
                }
            }
            return newArray;
        } catch (NullPointerException ex) {
            return listData;
        }
    }

    private void loadChoiseGroup(){
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        getChoiseGroup = serviceApiClient.getChoiseGroup("Bearer " + token);
        getChoiseGroup.enqueue(new Callback<ChoiseGroup>() {
            @Override
            public void onResponse(Call<ChoiseGroup> call, Response<ChoiseGroup> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        lv_choice_group.setVisibility(View.VISIBLE);
                        List<Data> listData = new ArrayList<>();
                        listData = getSortArray(response.body().getData());
                        final ChoisGroupAdapter mAdapter = new ChoisGroupAdapter(ChoiceGroupActivity.this, currentResult);
                        ListView lv_choice_group = (ListView) findViewById(R.id.lv_choice_group);
                        if (!listData.isEmpty()) {
                            for (int i =0; i<listData.size(); i++) {
                                mAdapter.addData(listData.get(i));
                            }
                        }
                        lv_choice_group.setAdapter(mAdapter);
                        //обраотчик listView
                        lv_choice_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                Group group = new Group();
                                group.setId(mAdapter.getItem(position).getId());
                                group.setName(mAdapter.getItem(position).getName());
                                currentResult.getData().getWorks().setGroup(group);

                                Intent intent = new Intent("ru.android.cyfral.servisnik.choiseelement");
                                intent.putExtra("currentResult", currentResult);
                                startActivityForResult(intent, 2);
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
            public void onFailure(Call<ChoiseGroup> call, Throwable t) {
                //Произошла непредвиденная ошибка
                mProgressBar.setVisibility(View.INVISIBLE);
                showErrorDialog("");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        GetResult getResult = (GetResult) data.getExtras().getSerializable("currentResult");
        Intent intent = new Intent();
        intent.putExtra("currentResult", getResult);
        setResult(RESULT_OK, intent);
        finish();
    }
    private void getChoiseGroup() {

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
                                loadChoiseGroup();
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
                    loadChoiseGroup();
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

}
