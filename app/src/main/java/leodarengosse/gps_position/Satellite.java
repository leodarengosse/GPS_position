package leodarengosse.gps_position;

import android.app.Service;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by leodarengosse on 21/06/16.
 */
public class Satellite extends Service implements GpsStatus.Listener {


    private String strGpsStats;
    private String SatPrn;
    private ArrayList SatPrnList;
    protected GpsStatus mStatus;
    protected LocationManager lm;
    private TextView textSat;

    public Satellite() {
        super();
        this.mStatus = null;
        this.SatPrnList = new ArrayList();
        this.strGpsStats = "localisation en cours...\n";
        this.SatPrn ="";

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Satellite(LocationManager lm, TextView textSat) {
        this.mStatus = null;
        this.SatPrnList = new ArrayList();
        this.strGpsStats = "localisation en cours...\n";
        this.lm = lm;
        this.textSat = textSat;
    }

    @Override
    public synchronized void onGpsStatusChanged(int event) {
        Log.d("Satellite", "Sat found");

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                textSat.setText(strGpsStats);
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                // Do Something with mStatus info
                textSat.setText(strGpsStats);
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // Do Something with mStatus info
                textSat.setText(strGpsStats);
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // Do Something with mStatus info
                this.mStatus = this.lm.getGpsStatus(null);
                Iterator<GpsSatellite> sats = this.mStatus.getSatellites().iterator();
                strGpsStats = "Number: Prn,usedInFix(),Snr,Azimuth,Elevation\n";
                int i = 0;
                while (sats.hasNext()) {
                    GpsSatellite satellite = sats.next();
                    strGpsStats = (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation() + "\n\n";
                }
                //String satinfo = SatInfos();
                textSat.setText(strGpsStats);
                Log.d("Satellite", "Sat found");
                break;
        }
    }

    public List<GpsSatellite> getSatellitesList() {
        GpsStatus gpsStatus = lm.getGpsStatus(null);
        if (gpsStatus != null) {
            Iterable<GpsSatellite> allSats = gpsStatus.getSatellites();
            ArrayList<GpsSatellite> SatList = new ArrayList<>();
            for (GpsSatellite curSat : allSats) {
                //do not count satellites the phone doesn't have a connection to
                    SatList.add(curSat);
            }
            return SatList;
        }
        return null;
    }

    protected String getSatUsedPrn(){
        Iterator<GpsSatellite> sats = mStatus.getSatellites().iterator();
        int i;
        i = 0;
        while (sats.hasNext()) {
            GpsSatellite sat = sats.next();
            if (sat.usedInFix()) {

                SatPrn = SatPrn + String.format(String.valueOf(sat.getPrn()));
                i++;
            }
        }

        return SatPrn;
    }

    public String SatInfos(GpsStatus mStatus){

        Iterator<GpsSatellite> sats = mStatus.getSatellites().iterator();
        this.strGpsStats = "Number: Prn,usedInFix(),Snr,Azimuth,Elevation\n";
        int i = 0;
        while (sats.hasNext()) {
            GpsSatellite satellite = sats.next();
            this.strGpsStats += (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation() + "\n\n";
        }
      return this.strGpsStats;
    };


    public int getSatUsedCount() {
        int satUsedCount = 0;
        GpsStatus gpsStatus = lm.getGpsStatus(null);
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


}

 /* String SatPrn = "";

    GpsStatus status = lm.getGpsStatus(null);

    double snrs[] = new double[status.getMaxSatellites()];


    int i =0;
    Iterator<GpsSatellite> sats = status.getSatellites().iterator();
while(sats.hasNext()) {
        GpsSatellite sat = sats.next();
        if (sat.usedInFix())
        satFixCount++;
        snrs[i] = sat.getPrn();

        SatPrn = SatPrn + String.format(String.valueOf(sat.getPrn()));

        i++;
        }
        // ascending
        Arrays.sort(snrs);

        numberSat = getSatCount();
*/