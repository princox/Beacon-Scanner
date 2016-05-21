package com.hogervries.beaconscanner.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.hogervries.beaconscanner.fragment.BeaconDetailFragment;

/**
 * Trifork Netherlands.
 * GuestApp Social.
 * Mitchell de Vries & Mohammed Ali.
 */

public class BeaconDetailActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment createFragment() {
        return BeaconDetailFragment.newInstance();
    }
}
