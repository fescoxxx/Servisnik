package ru.android.cyfral.servisnik.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private ImageView image_logo; //лого
    private TextView login_text_error;
    private TextView psw_text_error;

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
        } catch (NullPointerException ex) {

        }
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
        password = findViewById(R.id.text_password);
        image_logo  = (ImageView) findViewById(R.id.image_logo);
        login_text_error =(TextView) findViewById(R.id.login_text_error);
        psw_text_error = (TextView) findViewById(R.id.psw_text_error);

        login_text_error.setVisibility(View.INVISIBLE);
        psw_text_error.setVisibility(View.INVISIBLE);

        final ConstraintLayout rootView = (ConstraintLayout) findViewById(R.id.constants_root);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    image_logo.setVisibility(View.INVISIBLE);
                }
                else {
                    image_logo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private void login() {
        Call<Token> call = tokenClient.login("password",
                "mpservisnik",
                "secret",
                "s.sidorov",
                "!Qwerty7",
                "");

/*         Call<Token> call = tokenClient.login("password",
                "mpservisnik",
                "secret",
                username.getText().toString(),
                password.getText().toString(),
        "");*/



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
                    mDialog.dismiss();
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            snackbar = Snackbar
                                    .make(findViewById(android.R.id.content), "Введены неправильные логин или пароль", Snackbar.LENGTH_LONG)
                                    .setAction("Скрыть", mOnClickListener)
                                    .setActionTextColor(Color.WHITE);
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
    }/**/


    public void validator() {
        boolean error = false;
        if (username.getText().toString().length() == 0) {
            login_text_error.setVisibility(View.VISIBLE);
            username.getBackground().mutate().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_ATOP);
            error = true;
        } else {
            login_text_error.setVisibility(View.INVISIBLE);
            username.getBackground().mutate().setColorFilter(getResources().getColor(R.color.dark), PorterDuff.Mode.SRC_ATOP);
            error = false;
        }

        if (password.getText().toString().length() == 0) {
            psw_text_error.setVisibility(View.VISIBLE);
            password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_ATOP);
            error = true;
        } else {
            psw_text_error.setVisibility(View.INVISIBLE);
            password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.dark), PorterDuff.Mode.SRC_ATOP);
            error = false;
        }

        if (!error) {login();}

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                validator();
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
