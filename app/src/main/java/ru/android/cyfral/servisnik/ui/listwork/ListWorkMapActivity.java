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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.Constants;

public class ListWorkMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Location location;
    private LocationManager locationManager;
    Context mContext;
    private Double latitude;
    private Double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_work_map);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Список работ");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setBuildingsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(loadTextPref(Constants.SETTINGS.LATITUDE)),
                Double.parseDouble(loadTextPref(Constants.SETTINGS.LONGITUDE)));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Я тут "+loadTextPref(Constants.SETTINGS.LATITUDE)));

        float zoomLevel = 17.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));

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
        }
    }

}
