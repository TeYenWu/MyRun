package com.teyenwu.myrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.teyenwu.myrun.model.ExerciseEntry;
import com.teyenwu.myrun.model.ExerciseEntryDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements ServiceConnection, OnMapReadyCallback {
    final static float METERTOMILE = 0.000621371f;
    private GoogleMap mMap;
    public Marker startMaker;
    public Marker curMaker;
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView activityTypeTextView;
    TextView avgSpeedTextView;
    TextView curSpeedTextView;
    TextView climbTextView;
    TextView calorieTextView;
    TextView distanceTextView;
    PolylineOptions rectOptions;
    Polyline polyline;

    ExerciseEntry entry;
    float curSpeed = 0;
    float avgSpeed = 0;
    float distance = 0;
    long duration = 0;
    int calories = 0;
    int activityType = 0;

    double lastAltitude = 0;
    LatLng lastLatlng;

    Date date;

    Boolean isTracking = true;
    Boolean isBind = false;

    MapsActivity.trackingServiceHandler trackingServiceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        date = new Date();

        activityTypeTextView = findViewById(R.id.activityTypeTextView);
        avgSpeedTextView = findViewById(R.id.avgSpeedTextView);
        curSpeedTextView = findViewById(R.id.curSpeedTextView);
        climbTextView = findViewById(R.id.climbTextView);
        calorieTextView = findViewById(R.id.calorieTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        long id = getIntent().getLongExtra("id", -1);
        if(id != -1){
            ExerciseEntryDbHelper dpHelper = new ExerciseEntryDbHelper(this);
            entry = dpHelper.fetchEntryByIndex(id);
            if(entry != null){
                String[] activityTypeStringArray = getResources().getStringArray(R.array.activity_type_array);
                activityTypeTextView.setText("Type: " + activityTypeStringArray[entry.getActivityType()]);
                curSpeedTextView.setText("Current speed: N/A" + " km/h");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String unitPreference = sp.getString("unit_preference","Miles");

                float distance = entry.getDistance();
                if(!unitPreference.equals("Miles")) {
                    distance = distance * 1.61f;
                    distanceTextView.setText("Distance: " + String.valueOf(distance) + " km");
                    avgSpeedTextView.setText("Average speed: " + String.valueOf(entry.getAvgSpeed()) + " km/h");
                }
                else {
                    distanceTextView.setText("Distance: " + String.valueOf(entry.getDistance()) + " miles");
                    avgSpeedTextView.setText("Average speed: " + String.valueOf(entry.getAvgSpeed()/1.61) + " miles/h");
                }


                climbTextView.setText("Climb: " + String.valueOf(entry.getClimb()));
                calorieTextView.setText("Calories: " + String.valueOf(entry.getCalorie()));
                //getActionBar().show();
                saveButton.setVisibility(View.INVISIBLE);

                cancelButton.setVisibility(View.INVISIBLE);
            }
            isTracking = false;
        } else {
            activityType = getIntent().getIntExtra("activityType", -1);
            if(activityType != -1){
                String[] activityTypeStringArray = getResources().getStringArray(R.array.activity_type_array);
                activityTypeTextView.setText("Type: " + activityTypeStringArray[activityType]);
            } else{
                activityTypeTextView.setText("Type: unKnown");
            }
            getSupportActionBar().hide();
            isTracking = true;
        }

        trackingServiceHandler = new trackingServiceHandler();
    }

    private LatLng fromLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service){
        TrackingService.MyBinder myBinder = (TrackingService.MyBinder) service;
        myBinder.getUIMsgHandler(trackingServiceHandler);
    }

    @Override
    public void onServiceDisconnected(ComponentName name){
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isTracking) return false;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteMenuItem:
                ExerciseEntryDbHelper dpHelper = new ExerciseEntryDbHelper(this);
                dpHelper.removeEntry(entry.getId());
                Intent data = new Intent();
                data.putExtra("id", entry.getId());

                setResult(RESULT_OK, data);
                finish();
                break;
            default:
                return false;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable();
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
    private void updateWithNewLocation(Location location) {
        TextView myLocationText;
//        myLocationText = (TextView)findViewById(R.id.myLocationText);

        if (location != null) {
            // Update the map location.
            LatLng latlng = fromLocationToLatLng(location);


            if (startMaker == null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                startMaker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)).title("Start point"));
                curMaker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)).title("Where I am"));
                rectOptions = new PolylineOptions().add(latlng);
                rectOptions.color(Color.BLUE);
                polyline = mMap.addPolyline(rectOptions);
                lastLatlng = latlng;
                lastAltitude = location.getAltitude();
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                if (curMaker != null) {
                    curMaker.remove();
                    curMaker = null;
                }
                curMaker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)).title("Where I am"));
                List<LatLng> list = polyline.getPoints();
                list.add(latlng);
                polyline.setPoints(list);

                float speed = location.getSpeed() * 3.6f;
//            float climb = climb

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String unitPreference = sp.getString("unit_preference","Miles");



                if (speed != 0) {
                    curSpeed = speed;
                }

                distance += calDistance(latlng, lastLatlng);

                Date curDate = new Date();
                duration = curDate.getTime() - date.getTime();
                avgSpeed = (distance/1000)/((float)duration/(3600000));

                if(!unitPreference.equals("Miles")) {
                    distanceTextView.setText("Distance: " + String.valueOf(distance/1000) + " km");
                    avgSpeedTextView.setText("Average speed: " + String.valueOf(avgSpeed) + " km/h");
                    curSpeedTextView.setText("Current speed: " + curSpeed + " km/h");
                }
                else {
                    distanceTextView.setText("Distance: " + String.valueOf(distance/1000/1.61) + " miles");
                    avgSpeedTextView.setText("Average speed: " + String.valueOf(avgSpeed/1.61) + " miles/h");
                    curSpeedTextView.setText("Current speed: " + curSpeed/1.61 + " miles/h");
                }

                double climb = location.getAltitude() - lastAltitude;
                if (climb != 0){
                    climbTextView.setText("Climb: " + String.valueOf(climb));
                }

                calories = Util.getCalories(activityType, distance, duration);
                calorieTextView.setText("Calories: " + String.valueOf(calories));
                lastLatlng = latlng;

            }

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!isTracking) {
            List<LatLng> latlngs = entry.getLocationList();
            if(latlngs.size() >= 2){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(0), 17));
                startMaker = mMap.addMarker(new MarkerOptions().position(latlngs.get(0)).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)).title("Start point"));
                curMaker = mMap.addMarker(new MarkerOptions().position(latlngs.get(latlngs.size()-1)).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)).title("Where I am"));
                rectOptions = new PolylineOptions().add(latlngs.get(0));
                for(int i = 1; i < latlngs.size(); i++){
                    rectOptions.add(latlngs.get(i));
                }
                rectOptions.color(Color.BLUE);
                mMap.addPolyline(rectOptions);
            }
        } else{

            if (!checkPermission())
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            else {
                upDateMap();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBind){
            Intent intent = new Intent(getApplicationContext(), TrackingService.class);
            getApplicationContext().stopService(intent);
            getApplicationContext().unbindService(this);
        }
    }

    private void upDateMap() {
        if (mMap == null) return;


        if(checkPermission()) {
            Intent intent = new Intent(getApplicationContext(), TrackingService.class);
            getApplicationContext().startService(intent);
            getApplicationContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
            isBind = true;
//            if (l != null){
//                LatLng latlng = fromLocationToLatLng(l);
//
//                startMaker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
//                        BitmapDescriptorFactory.HUE_GREEN)));//set position and icon for the marker
//                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                // Zoom in
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17)); //17: the desired zoom level, in the range of 2.0 to 21.0
//                rectOptions = new PolylineOptions().add(latlng);
//                lastLatlng = latlng;
//                lastAltitude = l.getAltitude();
//                updateWithNewLocation(l);
//
//            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            upDateMap();
        } else {
            finish();
        }
    }

    //******** Check run time permission for locationManager. This is for v23+  ********
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        upDateMap();

    }

    public float calDistance (LatLng a, LatLng b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(b.latitude-a.latitude);
        double lngDiff = Math.toRadians(b.longitude-a.longitude);
        double s = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(s), Math.sqrt(1-s));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    void save(){
        ExerciseEntryDbHelper dpHelper = new ExerciseEntryDbHelper(this);
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
        ExerciseEntry entry = new ExerciseEntry();
        entry.setInputType(ExerciseEntry.InputType.TYPE_GPS.ordinal());
        entry.setDateTimeFromString(sdf.format(date.getTime()));
        entry.setDistance(distance*METERTOMILE);
        entry.setDuration((int)duration/60000);
        entry.setCalorie(calories);
        entry.setAvgSpeed(avgSpeed);
        entry.setLocationList(polyline.getPoints());
        long id = dpHelper.insertEntry(entry);
        entry.setId(id);

        Intent data = new Intent();
        data.putExtra("id",id);

        setResult(RESULT_OK, data);
        finish();
    }


    public class trackingServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            if(msg.what == TrackingService.MSG_INT_VALUE) {
                Bundle bundle = msg.getData();
                Location l = bundle.getParcelable(TrackingService.LOCATION_KEY);
                updateWithNewLocation(l);
            }
        }
    }

}
