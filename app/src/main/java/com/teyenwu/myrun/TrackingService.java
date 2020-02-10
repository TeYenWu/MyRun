package com.teyenwu.myrun;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class TrackingService extends Service {
    NotificationManager notificationManager;
    public static final int NOTIFY_ID = 11;
    public static final int PENDINGINTENT_REQUEST_CODE = 0;
    public static final String CHANNEL_ID = "notification channel";
    private MyBinder myBinder;
    private Handler uIMsgHandler = null;
    public static final String LOCATION_KEY = "Location";
    public static final int MSG_INT_VALUE = 0;
    LocationManager locationManager;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        showNotification();
        myBinder = new MyBinder();

        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(false);
        String provider = locationManager.getBestProvider(criteria, true);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);

    }

    public void showNotification(){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDINGINTENT_REQUEST_CODE,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setContentTitle("MyRun");
        notificationBuilder.setContentText("Recording your path now");
        notificationBuilder.setSmallIcon(R.drawable.icon);
        notificationBuilder.setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // or builder.setAutoCancel(true);


        if(Build.VERSION.SDK_INT > 26) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "MyRun",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(NOTIFY_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.d("teyen", "Service onBind() called");
        return myBinder;
    }

    public class MyBinder extends Binder {
        public void getUIMsgHandler(Handler msgHandler){
            uIMsgHandler = msgHandler;
        }
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.d("teyen", "Service onUnBind() called~~~");
        uIMsgHandler = null;
        return true;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
//        Log.d("xd", "Service onDestroy");
        notificationManager.cancel(NOTIFY_ID);

    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            if(uIMsgHandler != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(LOCATION_KEY, location);
                Message message = uIMsgHandler.obtainMessage();
                message.setData(bundle);
                message.what = MSG_INT_VALUE;
                uIMsgHandler.sendMessage(message);
            }
        }
    }

}
