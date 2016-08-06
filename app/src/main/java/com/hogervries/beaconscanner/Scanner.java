package com.hogervries.beaconscanner;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Created by mdvri on 6-8-2016.
 */

public class Scanner implements BeaconConsumer {

    private static final String REGION_ID = "Beacon_scanner_region";

    private Context context;
    private BeaconManager beaconManager;
    private SharedPreferences pref;
    private OnScanBeaconsListener onScanBeaconsCallback;

    /**
     * This callback interface handles on scan events.
     * Interface must be implemented by classes using this service.
     */
    public interface OnScanBeaconsListener {
        /**
         * Called when beacons are scanned.
         *
         * @param beacons List of beacons which are scanned.
         */
        void onScanBeacons(Collection<Beacon> beacons);
    }

    public Scanner(Context context, BeaconManager beaconManager, OnScanBeaconsListener onScanBeaconsCallback) {
        try {
            this.onScanBeaconsCallback = onScanBeaconsCallback;
        } catch (ClassCastException notImplementedException) {
            throw new ClassCastException("Class must implement OnScanBeaconsListener");
        }

        this.context = context;
        this.beaconManager = beaconManager;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        setUpBeaconManager();
        setBeaconLayouts();
    }

    private void setUpBeaconManager() {
        int scanPeriod = Integer.parseInt(pref.getString(context.getString(R.string.pref_scan_frequency), "1100"));
        int betweenScanPeriod = Integer.parseInt(pref.getString(context.getString(R.string.pref_between_scan_period), "0"));
        int maxTrackingAge = Integer.parseInt(pref.getString(context.getString(R.string.pref_tracking_age), "5000"));
        boolean backgroundScanning = pref.getBoolean(context.getString(R.string.pref_background_scanning), false);

        beaconManager.setForegroundScanPeriod(scanPeriod);
        beaconManager.setForegroundBetweenScanPeriod(betweenScanPeriod);
        BeaconManager.setUseTrackingCache(true);
        beaconManager.setMaxTrackingAge(maxTrackingAge);

        beaconManager.setBackgroundMode(backgroundScanning);
        beaconManager.setBackgroundScanPeriod(scanPeriod);
        beaconManager.setBackgroundBetweenScanPeriod(betweenScanPeriod);
    }

    private void setBeaconLayouts() {
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                onScanBeaconsCallback.onScanBeacons(collection);
            }
        });

        try {
            // Starting ranging of beacons in our defined region.
            Region region = new Region(REGION_ID, null, null, null);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return context.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }
}
