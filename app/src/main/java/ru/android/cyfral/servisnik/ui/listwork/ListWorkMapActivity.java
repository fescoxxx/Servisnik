package ru.android.cyfral.servisnik.ui.listwork;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Utils;
import ru.android.cyfral.servisnik.model.listwork.listworkmap.ListWorks;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class ListWorkMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public Location location;
    private LocationManager locationManager;
    Context mContext;
    private Double latitude;
    private Double longitude;
    private Marker mMarker;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    private Call<ListWorks> listWorksCall;

    private FloatingActionButton floatingActionButtonCenterMap;
    private FloatingActionButton floatingActionButtonNearList;
    private ListWorks currentListWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_work_map);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Список работ");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        floatingActionButtonCenterMap = (FloatingActionButton) findViewById(R.id.floatingActionButtonCenterMap);
        floatingActionButtonNearList = (FloatingActionButton) findViewById(R.id.floatingActionButtonNearList);
        floatingActionButtonCenterMap.setOnClickListener(this);
        floatingActionButtonNearList.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (loadTextPref(Constants.SETTINGS.LATITUDE).equals("") |
                loadTextPref(Constants.SETTINGS.LONGITUDE).equals("")) {
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor ed = myPrefs.edit();
            ed.putString(Constants.SETTINGS.LATITUDE, "0.0");
            ed.putString(Constants.SETTINGS.LONGITUDE, "0.0");
            ed.apply();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Геолокация отключена");
            alertDialog.setMessage("Параметры местоположения не включены. Пожалуйста, включите их в меню настроек.");
            alertDialog.setPositiveButton("Перейти в настройки", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            locationManager.removeUpdates(locationListener);
        } catch (NullPointerException ex) {}
    }
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor ed = myPrefs.edit();
            ed.putString(Constants.SETTINGS.LATITUDE, String.valueOf(latitude));
            ed.putString(Constants.SETTINGS.LONGITUDE, String.valueOf(longitude));
            ed.apply();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            //  Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("loc_", provider);
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("loc_", provider);

        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("loc_", provider);
        }
    };
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        centerMeMap();

    }
    private String loadTextPref(String prefStr) {
        SharedPreferences sPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sPref.getString(prefStr, "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isLocationEnabled();
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("isGPSEnabled", String.valueOf(isGPSEnabled));
            Log.d("isNetworkEnabled", String.valueOf(isNetworkEnabled));

            if (loadTextPref(Constants.SETTINGS.LATITUDE).equals("") |
                    loadTextPref(Constants.SETTINGS.LONGITUDE).equals("")) {
                SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor ed = myPrefs.edit();
                ed.putString(Constants.SETTINGS.LATITUDE, "0.0");
                ed.putString(Constants.SETTINGS.LONGITUDE, "0.0");
                ed.apply();
            }


            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        10000,
                        0, locationListener);
                if (locationManager != null)   {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }//end if

            if (isGPSEnabled)  {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,
                        0, locationListener);

                if (locationManager != null)  {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //   latitude = location.getLatitude();
                    //  longitude = location.getLongitude();
                }
            }

            if (Utils.isNetworkAvailable(this)) {
                getListWorks();
            } else {
                //getInfoEntranceDataBase();
            }
        }
    }

    private void getListWorks() {
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
                            loadListWork();
                        } else {
                            //сервер вернул ошибку
                          //  mProgressBar.setVisibility(View.INVISIBLE);
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
                loadListWork();
            }
        }
    }

    private void showListWorks(ListWorks currentListWorks){
        this.currentListWorks = currentListWorks;
        for (int i=0; i<currentListWorks.getData().size(); i++) {
            Date dateToday = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'");
            try {
                Date deadLine = format.parse(currentListWorks.getData().get(i).getRepairRequestDeadline());
                if (deadLine.before(format.parse(format.format(dateToday)))) {
                    pinBlackMap(Double.parseDouble(currentListWorks.getData().get(i).getLatitude()),
                            Double.parseDouble(currentListWorks.getData().get(i).getLongitude()), "BLACK",
                            currentListWorks.getData().get(i).getHouseId());
                } else if (deadLine.equals(format.parse(format.format(dateToday)))){
                    pinBlackMap(Double.parseDouble(currentListWorks.getData().get(i).getLatitude()),
                            Double.parseDouble(currentListWorks.getData().get(i).getLongitude()), "RED",
                            currentListWorks.getData().get(i).getHouseId());
                } else {
                    pinBlackMap(Double.parseDouble(currentListWorks.getData().get(i).getLatitude()),
                            Double.parseDouble(currentListWorks.getData().get(i).getLongitude()), "BLUE",
                            currentListWorks.getData().get(i).getHouseId());
                }
            }  catch (NullPointerException ex) {
                pinBlackMap(Double.parseDouble(currentListWorks.getData().get(i).getLatitude()),
                        Double.parseDouble(currentListWorks.getData().get(i).getLongitude()), "NULL",
                        currentListWorks.getData().get(i).getHouseId());
            }
            catch (ParseException e) {
                 e.printStackTrace();
            }
        }

    }

    private void pinBlackMap(Double latitude,
                             Double longitude,
                             String color,
                             String houseID) {
        LatLng myPin = new LatLng(latitude, longitude);
        if (color.equals("BLACK")) {
            mMarker = mMap.addMarker(new MarkerOptions().position(myPin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_rr_black)));
            mMarker.setTag(houseID);
        } else if (color.equals("RED")) {
            mMarker = mMap.addMarker(new MarkerOptions().position(myPin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_rr_red)));
            mMarker.setTag(houseID);
        } else if (color.equals("BLUE")) {
            mMarker = mMap.addMarker(new MarkerOptions().position(myPin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_rr_blue)));
            mMarker.setTag(houseID);
        } else if (color.equals("NULL")) {
            mMarker =mMap.addMarker(new MarkerOptions().position(myPin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_big_dildo)));;
            mMarker.setTag(houseID);
        }

    }

    private void loadListWork() {

        String token = loadTextPref(Constants.SETTINGS.TOKEN);
        listWorksCall = serviceApiClient
                .getListWorks("Bearer " + token);
        listWorksCall.enqueue(new Callback<ListWorks>() {
            @Override
            public void onResponse(Call<ListWorks> call, Response<ListWorks> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsSuccess().equals("true")){
                        //корректное получение объекта
                        ListWorks currentListWorks = response.body();
                        showListWorks(currentListWorks);

                    } else {
                        //сервер вернул ошибку от АПИ
                       // mProgressBar.setVisibility(View.INVISIBLE);
                        showErrorDialog(response.body().getErrors().getCode());
                    }
                } else {
                    //сервер вернул ошибку
                   // mProgressBar.setVisibility(View.INVISIBLE);
                    int rc = response.code();
                    if (rc == 401) {
                        startActivity(new Intent("ru.android.cyfral.servisnik.login"));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListWorks> call, Throwable t) {
                //Произошла непредвиденная ошибка
              //  mProgressBar.setVisibility(View.INVISIBLE);
                showErrorDialog("");
            }
        });

    }

    private void centerMeMap() {
        mMap.clear();
        LatLng myTown = new LatLng(Double.parseDouble(loadTextPref(Constants.SETTINGS.LATITUDE)),
                Double.parseDouble(loadTextPref(Constants.SETTINGS.LONGITUDE)));
        mMap.addMarker(new MarkerOptions().position(myTown)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_iam_person2)));;
        float zoomLevel = 17.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myTown, zoomLevel));
        if(currentListWorks != null) {
            showListWorks(currentListWorks);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonNearList:
                Intent intent = new Intent("ru.android.cyfral.servisnik.nearlistactivity");
                intent.putExtra(Constants.SETTINGS.LATITUDE,
                        Double.parseDouble(loadTextPref(Constants.SETTINGS.LATITUDE)));
                intent.putExtra(Constants.SETTINGS.LONGITUDE,
                        Double.parseDouble(loadTextPref(Constants.SETTINGS.LONGITUDE)));
                startActivity(intent);
                break;

            case R.id.floatingActionButtonCenterMap:
                centerMeMap();
                break;

        }
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
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            Intent intent = new Intent("ru.android.cyfral.servisnik.worksatactivity");
            intent.putExtra("GUID", marker.getTag().toString());
            startActivity(intent);
        }
        return false;
    }
}
