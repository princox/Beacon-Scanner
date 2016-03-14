package com.hogervries.beaconscanner.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.fragment.BeaconDetailsFragment;

import org.altbeacon.beacon.Beacon;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconDetailsActivity extends SingleFragmentActivity {

    private static final String EXTRA_BEACON = "com.hogervries.beaconscanner.beacon";

    /**
     * Intent builder method for building and returning an intent for this activity.
     *
     * @param context Context.
     * @param beacon Given beacon.
     * @return Intent for this activity.
     */
    public static Intent newIntent(Context context, Beacon beacon) {
        Intent intent = new Intent(context, BeaconDetailsActivity.class);
        intent.putExtra(EXTRA_BEACON, beacon);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Beacon beacon = getIntent().getParcelableExtra(EXTRA_BEACON);
        setTitle(R.string.beacon_detail);
        return BeaconDetailsFragment.newInstance(beacon);
    }
}
