package com.hogervries.beaconscanner.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hogervries.beaconscanner.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent scanTransmitIntent = ScanTransmitActivity.newIntent(this);
                scanTransmitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(scanTransmitIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
