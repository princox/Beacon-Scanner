package com.hogervries.beaconscanner.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.assent.Assent;
import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.adapter.BeaconAdapter.OnBeaconSelectedListener;
import com.hogervries.beaconscanner.fragment.BeaconDialogFragment;
import com.hogervries.beaconscanner.fragment.MainFragment;

import org.altbeacon.beacon.Beacon;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class MainActivity extends AppCompatActivity implements OnBeaconSelectedListener {

    private static final String DIALOG_BEACON = "dialog_beacon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        // Sets this activity for Android M and up permissions.
        Assent.setActivity(this, this);
        // Gets fragment manager and current fragment in layout.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        // Check if the fragment exists, if not it adds a new one.
        if (fragment == null) {
            fragment = MainFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
        resetFragment();
    }

    private void resetFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit();
    }

    @Override
    public void onBeaconSelected(Beacon beacon) {
        BeaconDialogFragment dialog = BeaconDialogFragment.newInstance(beacon);
        dialog.show(getSupportFragmentManager(), DIALOG_BEACON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) Assent.setActivity(this, null);
    }

}
