package leodarengosse.gps_position;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    private TextView textSat;

    private String strGpsStats;
    private GpsStatus mStatus;

    private ArrayList<Integer> SatList;


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


    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(Position.this);
            //locationManager.removeGpsStatusListener (Position.this );
        }
    }

    /**
     * Start using GPS listener
     * Calling this function will start using GPS in your app
     */
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
                SatInfos();
                getSatUsedList();
                getSatUsedCount();
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d("Sat : ", "onGpsStatusChanged GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d("Sat : ", "onGpsStatusChanged GPS_EVENT_STOPPED");
                break;
        }

    }

    public ArrayList<Integer> getSatUsedList() {
        mStatus = locationManager.getGpsStatus(null);
        if (mStatus != null) {
            Iterator<GpsSatellite> sats = mStatus.getSatellites().iterator();
            SatList = new ArrayList<>();
            while (sats.hasNext()) {
                GpsSatellite sat = sats.next();
                if (sat.usedInFix()) {
                    SatList.add(sat.getPrn());

                }

            }
            return SatList;
        }
        return null;
    }


    public String SatInfos(){

        strGpsStats = "Number: Prn,usedInFix(),Snr,Azimuth,Elevation\n";
        mStatus = locationManager.getGpsStatus(null);
        if(mStatus != null) {
            Iterable<GpsSatellite>satellites = mStatus.getSatellites();
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

        return strGpsStats;
    };


    public int getSatUsedCount() {
        int satUsedCount = 0;
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if (gpsStatus != null) {
            for (GpsSatellite sat : gpsStatus.getSatellites()) {
                if (sat.usedInFix()) {
                    satUsedCount++;
                }
            }
        }
        return satUsedCount;
    }

    /**
     * Function to get str
     */
    public String getstrGpsStats() {

        // return latitude
        return this.strGpsStats;
    }

    /**
     * Function to get str
     */
    public ArrayList<Integer> getSatList() {

        // return latitude
        return this.SatList;
    }




}

