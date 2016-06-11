package com.hogervries.beaconscanner.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.fragment.SettingsFragment;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class SettingsActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_transition_fade_in, R.anim.anim_transition_exit_right);
    }
}
