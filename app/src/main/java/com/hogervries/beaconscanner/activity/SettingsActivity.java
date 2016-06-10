package com.hogervries.beaconscanner.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.hogervries.beaconscanner.fragment.SettingsFragment;

/**
 * Trifork Netherlands.
 * GuestApp Social.
 * Mitchell de Vries & Mohammed Ali.
 */

public class SettingsActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }
}
