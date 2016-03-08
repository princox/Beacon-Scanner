package com.oncreate.beaconscanner.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.oncreate.beaconscanner.R;
import com.oncreate.beaconscanner.fragment.BeaconFragment;

import org.altbeacon.beacon.Beacon;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconActivity extends SingleFragmentActivity {

    private static final String EXTRA_BEACON = "com.oncreate.beaconscanner.beacon";

    public static Intent newIntent(Context packageContext, Beacon beacon) {
        Intent intent = new Intent(packageContext, BeaconActivity.class);
        intent.putExtra(EXTRA_BEACON, beacon);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Beacon beacon = getIntent().getParcelableExtra(EXTRA_BEACON);
        setTitle(R.string.beacon_detail);
        return BeaconFragment.newInstance(beacon);
    }
}
