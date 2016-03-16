package com.hogervries.beaconscanner.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Beacon Scanner, file created on 16/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconScannerService implements BeaconConsumer {

    private static final String REGION_ID = "Beacon_scanner_region";

    private BeaconManager beaconManager;
    private Context context;
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

    public BeaconScannerService(Context context, OnScanBeaconsListener callback, BeaconManager beaconManager) {
        try {
            onScanBeaconsCallback = callback;
        } catch (ClassCastException notImplementedException) {
            throw new ClassCastException("Class must implement OnScanBeaconsListener");
        }
        this.context = context;
        this.beaconManager = beaconManager;
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
