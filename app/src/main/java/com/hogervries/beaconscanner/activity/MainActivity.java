package com.hogervries.beaconscanner.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.adapter.BeaconAdapter.OnBeaconSelectedListener;
import com.hogervries.beaconscanner.fragment.BeaconFragment;
import com.hogervries.beaconscanner.fragment.MainFragment;

import org.altbeacon.beacon.Beacon;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class MainActivity extends SingleFragmentActivity implements OnBeaconSelectedListener {

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    public void onBeaconSelected(Beacon beacon) {
        BeaconFragment dialog = BeaconFragment.newInstance(beacon);
        dialog.show(getSupportFragmentManager(), "dialog");
        // Overriding screen transition animation.
        overridePendingTransition(R.anim.anim_transition_from_right, R.anim.anim_transition_fade_out);
    }
}
