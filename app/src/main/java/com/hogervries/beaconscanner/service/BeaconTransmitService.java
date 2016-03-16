package com.hogervries.beaconscanner.service;

import android.annotation.TargetApi;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

/**
 * Beacon Scanner, file created on 16/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconTransmitService {

    private Context context;
    private BeaconTransmitter beaconTransmitter;

    public BeaconTransmitService(Context context) {
        this.context = context;
        setTransmitter();
        setAdvertisingMode();
    }

    public void startTransmitting() {
        beaconTransmitter.startAdvertising();
    }

    public void stopTransmitting() {
        beaconTransmitter.stopAdvertising();
    }

    private void setTransmitter() {
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT);
        beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        beaconTransmitter.setBeacon(createBeacon());
    }

    private Beacon createBeacon() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new Beacon.Builder()
                    .setId1(preferences.getString("key_beacon_uuid", "0"))
                    .setId2(preferences.getString("key_major", "0"))
                    .setId3(preferences.getString("key_minor", "0"))
                    .setBluetoothName(preferences.getString("key_beacon_name", "My Beacon"))
                    .setManufacturer(0x0118)
                    .setTxPower(Integer.parseInt(preferences.getString("key_power", "-59")))
                    .setDataFields(Arrays.asList(new Long[]{0l}))
                    .build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAdvertisingMode() {
        beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
    }

}
