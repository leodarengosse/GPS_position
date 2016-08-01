package leodarengosse.gps_position;


import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    //Initialisation variables
    protected TextView textCoord, textSat;
    /* private double latitude, longitude, altitude;
     private float accuracy, bearing, speed;
     private long time;*/
    protected String msg, provider;
    protected LocationManager lm;


    private Button button_pause;


    public Satellite satellite;
    private Position position;
    private double lat;
    private String sat;
    private Button button_stop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Gestion de la position
        textCoord = (TextView) findViewById(R.id.textCoord);
        textSat = (TextView) findViewById(R.id.textSat);



        position = new Position(getApplicationContext(), textCoord, textSat);

        button_stop = (Button) findViewById(R.id.button_stop);
        //lm = position.locationManager;
        OnClickListener oclbutton_stop = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // change text of the TextView (tvOut)
                tvOut.setText("Button OK clicked");

            }

            // assign click listener to the OK button (btnOK)
            btnOk.setOnClickListener(oclBtnOk);
        };

        //lat = position.getLatitude();
        //textCoord.setText(String.valueOf(lat));
        //textCoord.append("provider"+ String.valueOf(position.getProvider()));

/*
        satellite = new Satellite(lm, textSat);
        //sat = satellite.getstrGpsStats();

        lm.addGpsStatusListener(satellite);
        textSat.setText("coucou");
        //textSat.setText(String.valueOf(sat));

        //lm.removeGpsStatusListener(satellite);*/



    }


    protected void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

}

 /* //Gestion de la localisation
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        satellite = new Satellite();

        //Test to verify if the GPS is allowed
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationListener locationChange = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();
                accuracy = location.getAccuracy();
                bearing = location.getBearing();
                provider = location.getProvider();
                speed = location.getSpeed();
                time = location.getTime();


                msg = String.format(
                        getResources().getString(R.string.new_location), time, provider, latitude,
                        longitude, altitude, accuracy, speed, bearing, satellite.getSatUsedCount(),
                        satellite.getSatUsedPrn(), satellite.getSatellitesList());
                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                textCoord.setText(msg);
                Log.d("GPS", msg);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                String newStatus = "";
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        newStatus = "OUT_OF_SERVICE";
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        newStatus = "TEMPORARILY_UNAVAILABLE";
                        break;
                    case LocationProvider.AVAILABLE:
                        if (!Objects.equals(newStatus, "AVAILABLE")) {
                            newStatus = "AVAILABLE";
                        }
                        break;
                }
                msg = String.format(getResources().getString(R.string.provider_new_status), provider, newStatus);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.d("GPS", msg);

            }

            @Override
            public void onProviderEnabled(String provider) {
                msg = String.format(
                        getResources().getString(R.string.provider_enabled), provider);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.d("GPS", msg);

            }

            @Override
            public void onProviderDisabled(String provider) {
                msg = String.format(
                        getResources().getString(R.string.provider_disabled), provider);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.d("GPS", msg);


            }

        };


        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationChange);*/


   /* private void setStarted(boolean navigating) {
        if (navigating != mNavigating) {
            if (navigating) {

            } else {
                mLatitudeView.setText(EMPTY_LAT_LONG);
                mLongitudeView.setText(EMPTY_LAT_LONG);
                mFixTime = 0;
                updateFixTime();
                mTTFFView.setText("");
                mAltitudeView.setText("");
                mAccuracyView.setText("");
                mSpeedView.setText("");
                mBearingView.setText("");
                mSvCount = 0;
                mAdapter.notifyDataSetChanged();
            }
            mNavigating = navigating;
        }
    }

    private void updateStatus(GpsStatus status) {

        setStarted(true);
        // update the fix time regularly, since it is displaying relative time
        updateFixTime();

        Iterator<GpsSatellite> satellites = status.getSatellites().iterator();

        if (mPrns == null) {
            int length = status.getMaxSatellites();
            mPrns = new int[length];
            mSnrs = new float[length];
            mSvElevations = new float[length];
            mSvAzimuths = new float[length];
        }

        mSvCount = 0;
        mEphemerisMask = 0;
        mAlmanacMask = 0;
        mUsedInFixMask = 0;
        while (satellites.hasNext()) {
            GpsSatellite satellite = satellites.next();
            int prn = satellite.getPrn();
            int prnBit = (1 << (prn - 1));
            mPrns[mSvCount] = prn;
            mSnrs[mSvCount] = satellite.getSnr();
            mSvElevations[mSvCount] = satellite.getElevation();
            mSvAzimuths[mSvCount] = satellite.getAzimuth();
            if (satellite.hasEphemeris()) {
                mEphemerisMask |= prnBit;
            }
            if (satellite.hasAlmanac()) {
                mAlmanacMask |= prnBit;
            }
            if (satellite.usedInFix()) {
                mUsedInFixMask |= prnBit;
            }
            mSvCount++;
        }

        mAdapter.notifyDataSetChanged();
    }*/

//Criteria critere = new Criteria();
       /* // Pour indiquer la précision voulue
        // On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
        critere.setAccuracy(Criteria.ACCURACY_FINE);
        // Est-ce que le fournisseur doit être capable de donner une altitude ?
        critere.setAltitudeRequired(true);
        // Est-ce que le fournisseur doit être capable de donner une direction ?
        critere.setBearingRequired(true);
        // Est-ce que le fournisseur peut être payant ?
        critere.setCostAllowed(false);
        // Pour indiquer la consommation d'énergie demandée
        // Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et Criteria.POWER_LOW pour une basse consommation
        critere.setPowerRequirement(Criteria.POWER_HIGH);
        // Est-ce que le fournisseur doit être capable de donner une vitesse ?
        critere.setSpeedRequired(true);
*/


        /*
        public boolean startListen()
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            listening = true;
            return locationManager.addNmeaListener(this);

        }

        public void stopListen()
        {
            locationManager.removeNmeaListener(this);
            locationManager.removeUpdates(this);
            listening = false;
        }*/

       /* button_pause = (Button) findViewById(R.id.button_pause);

        button_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    numberSat = getSatCount();
                    numberSatList = getSatellitesUsedInFix();
                    textCoord.setText(String.valueOf(mLastLocation.getLatitude()) +
                            String.valueOf(mLastLocation.getLongitude())+String.valueOf(numberSat)+
                            String.valueOf(numberSatList));

                }
            }

            });


    }*/

