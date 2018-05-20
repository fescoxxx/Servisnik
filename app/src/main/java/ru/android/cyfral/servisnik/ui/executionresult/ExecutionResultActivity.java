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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.result.TmcResultAtapter;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.result.getResult.Tmas;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class ExecutionResultActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
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
    private Button put_result_button;
    private ImageButton works_result_button;
    private ImageButton choise_tmc_button;

    private GetResult currentResult;
    private GetResult newResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //изменение работ
        if (requestCode == 1){

            if (data == null) {return;}
            currentResult = (GetResult) data.getExtras().getSerializable("currentResult");
            group_result.setText(currentResult.getData().getWorks().getGroup().getName());
            element_result.setText(currentResult.getData().getWorks().getElement().getName());
            type_result.setText(currentResult.getData().getWorks().getType().getName());
            currentResult.getData().getTmas().clear();
            mAdapter.removeAll();
        }
        //выбор ТМЦ
        else if (requestCode == 4) {
            if (data == null) {return;}
            currentResult = (GetResult) data.getExtras().getSerializable("currentResult");
            mAdapter.removeAll();
            for (int i=0; i<currentResult.getData().getTmas().size(); i++) {
                mAdapter.addItem(currentResult.getData().getTmas().get(i));
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution_result);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_execution_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_execution_result);
        setTitle("Результаты выполнения ЗН");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);

        group_result = (TextView) findViewById(R.id.group_result);
        element_result = (TextView)  findViewById(R.id.element_result);
        type_result = (TextView)  findViewById(R.id.type_result);
        put_result_button = (Button) findViewById(R.id.put_result_button);
        works_result_button = (ImageButton) findViewById(R.id.works_result_button);
        choise_tmc_button = (ImageButton)  findViewById(R.id.choise_tmc_button);
        Intent intent = getIntent();

        try {
            Intent intent0 = getIntent();
            GetResult getResult = (GetResult) intent0.getExtras().getSerializable("currentResult");
            Log.d("onResume_ex", "MainActivity: onResume()" + getResult.getData().getWorks().getGroup());
        } catch (NullPointerException ex){}

        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");
            guid = orderCard.getData().getId();
            getResult(guid);
        put_result_button.setOnClickListener(this);
        works_result_button.setOnClickListener(this);
        choise_tmc_button.setOnClickListener(this);
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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //отправка результатов
            case R.id.put_result_button:
                for (int i = 0;i<currentResult.getData().getTmas().size(); i++) {
                    Log.d("currentResult", currentResult.getData().getTmas().get(i).getName());
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
                Intent intenttmc = new Intent("ru.android.cyfral.servisnik.choistmc");
                intenttmc.putExtra("currentResult", currentResult);
                startActivityForResult(intenttmc, 4);
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

                        List<Tmas> listTmas =currentResult.getData().getTmas();
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
