package com.hogervries.beaconscanner.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.assent.Assent;
import com.hogervries.beaconscanner.R;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Creates and returns new fragment.
     * Method must be implemented by sub classes.
     *
     * @return Fragment.
     */
    protected abstract Fragment createFragment();

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
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) Assent.setActivity(this, null);
    }
}
