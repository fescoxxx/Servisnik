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
import ru.android.cyfral.servisnik.model.executionresult.choiseelement.ChoiseElement;
import ru.android.cyfral.servisnik.model.executionresult.choiseelement.ChoiseElementAdapter;
import ru.android.cyfral.servisnik.model.executionresult.choiseelement.Data;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.GetResult;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.Element;

public class ChoiceElementsActivity extends AppCompatActivity  {

    private ProgressBar mProgressBar;
    private ListView lv_choice_element;
    private GetResult currentResult;
    private SharedPreferences sPref;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<ChoiseElement> getChoiseElement;
    private Call<RefreshToken> callRedresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_elements);
        setTitle("Выберите элемент работ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_choice_element);
        lv_choice_element = (ListView) findViewById(R.id.lv_choice_element);
        Intent intent = getIntent();
        GetResult getResult = (GetResult) intent.getExtras().getSerializable("currentResult");
        currentResult = getResult;
        getChoiseElement();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {getChoiseElement.cancel(); } catch (Exception ex) {}
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

    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }

    private List<Data> getSortArray(List<Data> listData) {
        try {
            List<Data> newArray = new ArrayList<>();
            for (int i=0; i<listData.size(); i++) {
                if(listData.get(i).getName().equals(currentResult.getData().getWorks().getElement().getName())) {
                    newArray.add(listData.get(i));
                }
            }
            for (int i=0; i<listData.size(); i++) {
                if(!listData.get(i).getName().equals(currentResult.getData().getWorks().getElement().getName())) {
                    newArray.add(listData.get(i));
                }
            }
            return newArray;
        } catch (NullPointerException ex) {
            return listData;
        }
    }

    private void loadChoiseElement(){
        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        getChoiseElement = serviceApiClient
                .getChoiseElement(
                        currentResult
                                .getData()
                                .getWorks()
                                .getGroup()
                                .getId(),
                        "Bearer " + token);


        getChoiseElement.enqueue(new Callback<ChoiseElement>() {
            @Override
            public void onResponse(Call<ChoiseElement> call, Response<ChoiseElement> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        lv_choice_element.setVisibility(View.VISIBLE);
                        List<Data> listData = new ArrayList<>();
                        listData = getSortArray(response.body().getData());
                        final ChoiseElementAdapter mAdapter = new ChoiseElementAdapter(ChoiceElementsActivity.this, currentResult);
                        ListView lv_choice_element = (ListView) findViewById(R.id.lv_choice_element);
                        if (!listData.isEmpty()) {
                            for (int i =0; i<listData.size(); i++) {
                                mAdapter.addData(listData.get(i));
                            }
                        }
                        lv_choice_element.setAdapter(mAdapter);
                        //обработчик listView
                        lv_choice_element.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                Element element = new Element();
                                element.setId(mAdapter.getItem(position).getId());
                                element.setName(mAdapter.getItem(position).getName());
                                currentResult.getData().getWorks().setElement(element);
                                Intent intent = new Intent("ru.android.cyfral.servisnik.choisetype");
                                intent.putExtra("currentResult", currentResult);
                                startActivityForResult(intent, 3);

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
            public void onFailure(Call<ChoiseElement> call, Throwable t) {
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
    private void getChoiseElement() {

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
                            loadChoiseElement();
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
                loadChoiseElement();
            }
        }
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


}
