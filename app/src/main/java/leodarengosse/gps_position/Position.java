package leodarengosse.gps_position;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

/**
 * Created by leodarengosse on 24/06/16.
 */
public class Position implements LocationListener, GpsStatus.Listener {

    private final Context mContext;


    // flag for GPS status
    public boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private double altitude; //altitude
    private long time; //GPS time

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters

    private static int minTimeUpdate;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0 * minTimeUpdate; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private String provider;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private String msg;
    private float bearing;
    private float speed;
    private float accuracy;
    private TextView textCoord;
    public double lat_gps = 0.0, lat_wifi = 0.0;
    private String strGpsStats;
    private TextView textSat;


    public Position(Context context, TextView textCoord, TextView textSat) {
        this.mContext = context;
        //this.minTimeUpdate = minTimeUpdate;
        this.textCoord = textCoord;
        this.textSat = textSat;
        getLocation();
    }

    public Location getLocation() {
        try {

            // Get the location manager
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            locationManager.addGpsStatusListener ( this );

            if (!this.isGPSEnabled && !this.isNetworkEnabled) {
                // no location provider is available show toast to user
                Toast.makeText(mContext, "No Location Provider is Available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services

                Criteria criteria = new Criteria();
                this.provider = locationManager.getBestProvider(criteria, false);
                locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    this.latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    altitude = location.getAltitude();
                    time = location.getTime();
                }

            }
        }
            catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    /**
     * Show a dialog to the user requesting that GPS be enabled
     */
    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Start using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(Position.this);
        }
    }


    public void startUsingGPS() {
        this.getLocation();
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        //latitude = location.getLatitude();

        // return latitude
        return this.latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {

        // return longitude
        return this.longitude;
    }

    public double getAltitude() {

        // return latitude
        return altitude;
    }

    public double getAccuracy() {

        // return Accuracy
        return accuracy;
    }

    public double getSpeed() {

        // return speed
        return speed;
    }

    public double getBearing() {

        // return bearing
        return bearing;
    }

    public String getProvider() {

        // return provider
        return this.provider;
    }


    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

// Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

// Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

// On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

// on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onLocationChanged(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.time = location.getTime();
        this.bearing = location.getBearing();
        this.provider = location.getProvider();
        this.speed = location.getSpeed();
        this.accuracy = location.getAccuracy();


        msg = String.format(
                mContext.getResources().getString(R.string.new_location), time, provider, latitude,
                longitude, altitude, accuracy, speed, bearing);
        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        textCoord.setText(msg);
        Log.d("GPS", msg);
    }


    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(mContext, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(mContext, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

}

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event)
        {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.d("Sat : ", "onGpsStatusChanged GPS_EVENT_FIRST_FIX");
                //textSat.setText("onGpsStatusChanged GPS_EVENT_FIRST_FIX");
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.d("Sat : ", "onGpsStatusChanged GPS_EVENT_SATELLITE_STATUS");
                strGpsStats = "Number: Prn,usedInFix(),Snr,Azimuth,Elevation\n";
                GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                if(gpsStatus != null) {
                    Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> sat = satellites.iterator();
                    int i=0;
                    while (sat.hasNext()) {
                        GpsSatellite satellite = sat.next();
                        if (satellite.usedInFix()) {

                            strGpsStats += (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation() + "\n";
                        }
                    }
                    textSat.setText(strGpsStats);
                }
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d("Sat : ", "onGpsStatusChanged GPS_EVENT_STARTED");
                textSat.setText("onGpsStatusChanged GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d("Sat : ", "onGpsStatusChanged GPS_EVENT_STOPPED");
                break;
        }

    }


}

