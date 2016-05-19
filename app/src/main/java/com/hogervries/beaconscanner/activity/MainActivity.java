package com.hogervries.beaconscanner.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.fragment.MainFragment;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class MainActivity extends AppCompatActivity {

    private static final String DIALOG_BEACON = "dialog_beacon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
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

}
