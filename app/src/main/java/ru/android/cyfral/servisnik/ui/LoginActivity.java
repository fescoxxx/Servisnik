package ru.android.cyfral.servisnik.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.Token;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.TokenClient;

//Тестовый
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Snackbar snackbar; //снекбар
    private ProgressDialog mDialog; //анимация загрузки
    private TextView username;
    private TextView password;

    View.OnClickListener mOnClickListener;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            getSupportActionBar().hide();
        } catch (NullPointerException ex) {}
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_main);
        //скрыть snackbar
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        };
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById(R.id.btn_login).setOnClickListener(this);

        username = findViewById(R.id.text_username);
        password = findViewById(R.id.editText2);
    }

    private void login() {
        Call<Token> call = tokenClient.login("password",
                "mpservisnik",
                "secret",
                "s.sidorov",
                "!Qwerty7",
                "");
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Проверка данных...");
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
        mDialog.show();
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    mDialog.dismiss();
                    Log.d("login", response.body().getAccess_token());
                    SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor ed = myPrefs.edit();
                    ed.putString("token",  response.body().getAccess_token());
                    ed.putString("token_refresh",  response.body().getRefresh_token());
                    ed.apply();
                    startActivity(new Intent("ru.android.cyfral.servisnik.repair"));
                    finish();
                } else {
                    mDialog.dismiss();
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            snackbar = Snackbar
                                    .make(findViewById(android.R.id.content), "Введены не правильные логин или пароль", Snackbar.LENGTH_LONG)
                                    .setAction("Скрыть", mOnClickListener);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundColor(Color.WHITE);
                            snackbar.show();
                            break;
                        default:
                            showErrorDialog(String.valueOf(sc));
                            break;
                    }
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                mDialog.dismiss();
                showErrorDialog("");
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            default:
                // TODO Auto-generated method stub
                break;
        }
    }

    private void showErrorDialog(String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
