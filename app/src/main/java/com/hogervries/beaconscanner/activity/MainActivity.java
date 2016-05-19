package com.hogervries.beaconscanner.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.hogervries.beaconscanner.fragment.MainFragment;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class MainActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

}
