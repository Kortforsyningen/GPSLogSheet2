package ref.sdfe.gpslogsheet2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by B028406 on 10/12/2017.
 *
 * Each setup fragment was initializing its own listener, and using a lot of memory
 * This handler is created so that there will only be one instance.
 */

public class LocationHandler extends Service{
    private Intent intent;
    private Context context;
    private double lat = 0.0;
    private double lon = 0.0;
    private LocationManager locationManager = null;

    //TODO: Set these to something bigger to save battery.
    int MIN_TIME_BW_UPDATES = 10;
    int MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    private boolean locationFound = false;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
//    private LocationHandler mInstance;

    LocationListener locationListener = new locationListener();

    public LocationHandler() {
    }

//    private String setCriteria() {
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        return locationManager.getBestProvider(criteria, true);
//    }

    private void updateLocation(Location location) {
        if (location != null) {
            this.lat = location.getLatitude();
            this.lon = location.getLongitude();
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("LocationHandler", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        intent = new Intent();
        context = getApplicationContext();
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        //Check Permissions:
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        } catch (SecurityException ex) {
            Log.i("LocationHandler","Location update fail",ex);
        } catch (IllegalArgumentException ex) {
            Log.i("LocationHandler","Provider does not exist",ex);
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        } catch (SecurityException ex) {
            Log.i("LocationHandler","Location update fail",ex);
        } catch (IllegalArgumentException ex) {
            Log.i("LocationHandler","Provider does not exist",ex);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null){
            try {
                locationManager.removeUpdates(locationListener);
            } catch (NullPointerException e){
                Log.i("LocationHandler","onDestroy(), no locationManger or listener to destroy.");
            }
        }
    }

    private class locationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return  ;
            }

            Log.i("getLocation()", "onLocationChanged");
            if (isGPSEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.i("LocationHandler","Using GPS.");
            }else{
                if(isNetworkEnabled) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.i("LocationHandler","Using Network.");
                }else {
                    Log.i("LocationHandler","Neither provider enabled.");
                }
            }

            updateLocation(location);
            lat = location.getLatitude();
            lon = location.getLongitude();
            if (!locationFound){
                int duration = Toast.LENGTH_SHORT;
                String toast_message = "Location is now available.";
                Toast toast = Toast.makeText(context,toast_message, duration);
                toast.show();
                locationFound = true;
            }
            Log.i("getLocation()", "Latitude: " + lat + ", Longitude: " + lon);
            intent.setAction("ref.sdfe.gpslogsheet2.LOCATION_UPDATED");
            intent.putExtra("locationFound",locationFound);
            intent.putExtra("lat",lat);
            intent.putExtra("lon",lon);
            sendBroadcast(intent);
            Log.i("getLocation()", "Broadcast sent.");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("getLocation()", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("getLocation()", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("getLocation()", "onProviderDisabled");
        }
    }

}
