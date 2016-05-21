package com.hogervries.beaconscanner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hogervries.beaconscanner.fragment.BeaconActionFragment;
import com.hogervries.beaconscanner.fragment.BeaconDetailFragment;
import com.hogervries.beaconscanner.fragment.BeaconLogFragment;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BeaconDetailFragment.newInstance();
            case 1:
                return BeaconLogFragment.newInstance();
            case 2:
                return BeaconActionFragment.newInstance();
            default:
                return BeaconDetailFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
