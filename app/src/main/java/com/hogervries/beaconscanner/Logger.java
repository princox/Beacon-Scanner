package com.hogervries.beaconscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.opencsv.CSVWriter;

import org.altbeacon.beacon.Beacon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Trifork
 * GuestAppSocial
 */
public class Logger {

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    public Logger(List<Beacon> beacons, Context context, SharedPreferences preferences) {
        logToFile(beacons, context, preferences);
    }

    // Checks if external storage is available for read and write.
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void requestWriteStoragePermission() {
        Assent.requestPermissions(new AssentCallback() {
            @Override
            public void onPermissionResult(PermissionResultSet permissionResultSet) {
                // Intentionally left blank
            }
        }, PERMISSION_WRITE_EXTERNAL_STORAGE, Assent.WRITE_EXTERNAL_STORAGE);
    }

    // Writes data from all beacons in scan range to a csv file in the downloads folder.
    private void logToFile(List<Beacon> beacons, Context context, SharedPreferences preferences) {
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            requestWriteStoragePermission();
        } else if (isExternalStorageWritable() && preferences.getBoolean("key_logging", false)) {
            String fileName = "BeaconData.csv";
            File beaconDataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            CSVWriter writer;

            try {
                if (beaconDataFile.exists() && !beaconDataFile.isDirectory()) {
                    FileWriter fileWriter = new FileWriter(beaconDataFile, true);
                    writer = new CSVWriter(fileWriter);
                } else {
                    writer = new CSVWriter(new FileWriter(beaconDataFile));
                }

                List<String[]> data = new ArrayList<>();
                for (Beacon beacon : beacons) {

                    data.add(new String[]{"UUID", beacon.getId1().toString(),
                            "Major", beacon.getId2().toString(),
                            "Minor", beacon.getId3().toString(),
                            "Last distance measured", context.getString(R.string.distance, String.format("%.2f", beacon.getDistance())),
                            "Bluetooth Address", beacon.getBluetoothAddress(),
                            "Rssi", String.valueOf(beacon.getRssi()),
                            "TX-power", String.valueOf(beacon.getTxPower()),
                            "Time stamp", SimpleDateFormat.getDateTimeInstance().format(new Date())
                    });
                }

                writer.writeAll(data);

                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
