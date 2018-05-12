package ru.android.cyfral.servisnik.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.database.DataDatabase;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchListener;
import ru.android.cyfral.servisnik.model.OrderCard.Data;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

//123
public class OrderCardActivity extends AppCompatActivity implements DataFetchListener {

    private SharedPreferences sPref;
    private String guid;
    private static Call<OrderCard> orderCardCall;
    private static DataDatabase mDatabase;
    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);


    //Данные карточки
    private TextView header_street;
    private TextView about_title;
    private TextView adressTitle;
    private TextView about_desc;
    private TextView date_deadline;
    private TextView angreed_date_text;
    private TextView angreed_time_text;
    private ImageButton btn_date_agreed;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Dialog dialogAgreed;


    private static ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_card_);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDatabase = new DataDatabase(this);
        header_street = (TextView) findViewById(R.id.header_street);
        adressTitle = (TextView) findViewById(R.id.adress_title);
        about_title = (TextView) findViewById(R.id.about_title);
        about_desc = (TextView) findViewById(R.id.about_desc);
        date_deadline = (TextView) findViewById(R.id.date_deadline);
        angreed_date_text =  (TextView) findViewById(R.id.agree_date);
        angreed_time_text = (TextView) findViewById(R.id.angree_time);
        btn_date_agreed = (ImageButton) findViewById(R.id.btn_date_agreed);

        Intent intent = getIntent();
        guid = intent.getStringExtra(Constants.SETTINGS.GUID);

        if (Utils.isNetworkAvailable(this)) {
            getFeed();
        } else {
            getFeedFromDatabase();
        }


        btn_date_agreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAgreed = new Dialog(OrderCardActivity.this);
                dialogAgreed.setContentView(R.layout.date_time_agreed_picker);
                dialogAgreed.setTitle("Назначить дату и время");
                timePicker = (TimePicker) dialogAgreed.findViewById(R.id.timePicker_agreed);
                datePicker = (DatePicker) dialogAgreed.findViewById(R.id.datePicker_argeed);
                timePicker.setIs24HourView(true);
                dialogAgreed.show();
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


    public static class SaveIntoDatabaseOdrerCard extends AsyncTask<OrderCard, Void, Void> {
        private final String TAG = SaveIntoDatabaseOdrerCard.class.getSimpleName();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(OrderCard... params) {
            OrderCard orderCard = params[0];
            try {
                mDatabase.addDataOrderCard(orderCard);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return null;
        }
    }

    private String loadTextPref(String prefStr) {
        sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }

    public void getFeed() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Отправляем данные...");
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
        mDialog.show();
        SharedPreferences sPref = getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
        String token = sPref.getString(Constants.SETTINGS.TOKEN, "");
        orderCardCall = serviceApiClient.getOrderCard(guid, "Bearer " + token);
        orderCardCall.enqueue(new Callback<OrderCard>() {
            @Override
            public void onResponse(Call<OrderCard> call, Response<OrderCard> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")) {
                        showOrderCard(response.body());
                        SaveIntoDatabaseOdrerCard task = new SaveIntoDatabaseOdrerCard();
                        task.execute(response.body());
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
                                            ed.apply();
                                            getFeed();
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
                        }
                       // showErrorDialog(response.body().getErrors().getCode());
                     //   getFeedFromDatabase();
                    }

                    mDialog.cancel();
                } else {

                    mDialog.cancel();
                    startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<OrderCard> call, Throwable t) {
                Log.d("orderCardCall", t.getMessage());
                mDialog.cancel();
                startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                finish();
            }
        });
    }
    private void showOrderCard(OrderCard orderCard) {
        String str = "";
        String group = orderCard.getData().getWorks().getGroup();
        String element = orderCard.getData().getWorks().getElement();
        String type = orderCard.getData().getWorks().getType();

        String litera="";
        String building="";
        String floor="";
        String room="";
        String dom = "";
        String entrance = "";
        String apartment = "";

        try {
            if (!orderCard.getData().getAddress().getLetter().equals("null")) {
                litera = orderCard.getData().getAddress().getLetter();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!orderCard.getData().getAddress().getBuilding().equals("null")) {
                building = orderCard.getData().getAddress().getBuilding();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!orderCard.getData().getAddress().getFloor().equals("null")) {
                floor = orderCard.getData().getAddress().getFloor();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!orderCard.getData().getAddress().getRoom().equals("null")) {
                room = orderCard.getData().getAddress().getRoom();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!orderCard.getData().getAddress().getNumber().equals("null")) {
                dom = orderCard.getData().getAddress().getNumber();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!orderCard.getData().getAddress().getEntrance().equals("null")) {
                entrance = orderCard.getData().getAddress().getEntrance();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!orderCard.getData().getAddress().getApartment().equals("null")) {
                apartment = orderCard.getData().getAddress().getApartment();
            }
        } catch (java.lang.NullPointerException ex) {}
        try {
            if (!group.equals("null")) {
                    str = str + group;
                }
            }
        catch (java.lang.NullPointerException ex) {}

        try {
            if (!element.equals("null")) {
                    str = str + " | "+element;
                }
            }
        catch (java.lang.NullPointerException ex) {}

        try {
            if (!type.equals("null")) {
                str = str + " | "+type;
            }
        } catch (java.lang.NullPointerException ex) {}

        about_title.setText(str);

        header_street.setText(orderCard.getData().getAddress().getCityType()+ " "+
                orderCard.getData().getAddress().getCity() + " "+
                orderCard.getData().getAddress().getStreetType() + " " +
                orderCard.getData().getAddress().getStreet() + " ");
        adressTitle.setText("д."+ dom+litera+ " п."+
                entrance + " кв."+
                apartment + " "+building + floor + room);
        about_desc.setText(orderCard.getData().getComment());

        Date dateToday = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
        SimpleDateFormat format_full = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat format_data = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
        Date deadLine = null;
        Date angree_date = null;
        Date angree_time = null;

        try {
            deadLine = format.parse(orderCard.getData().getDeadline());
            if (deadLine.before(format.parse(format.format(dateToday)))) {
                date_deadline.setText("Просрочена");
            } else if (deadLine.equals(format.parse(format.format(dateToday)))){
                date_deadline.setText("Сегодня");
            } else {
                date_deadline.setText(format_data.format(deadLine));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            angree_date = format_full.parse(orderCard.getData().getAgreedDate());
            Log.d("angree_date", angree_date.toString());
            angreed_date_text.setText(format_data.format(angree_date));
            angreed_time_text.setText(format_time.format(angree_date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("dead_line_log", orderCard.getData().getDeadline());
    }

    private void getFeedFromDatabase(){
        mDatabase.fetchDatasForOrderCard(this, guid);
        mDialog.cancel();
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
    public void onDeliverAllDatas(List<ru.android.cyfral.servisnik.model.repairRequests.Data> datas) {

    }

    @Override
    public void onDeliverData(ru.android.cyfral.servisnik.model.repairRequests.Data data) {

    }

    @Override
    public void onDeliverOrderCard(OrderCard orderCard) {
        showOrderCard(orderCard);
    }

    @Override
    public void onHideDialog() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
