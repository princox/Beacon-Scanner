package com.hogervries.beaconscanner;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * Singleton class which stores scanned beacons.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconStore {
    // Static reference to this beacon store.
    private static BeaconStore sBeaconStore;

    private List<Beacon> mBeacons;

    /**
     * Creates a new BeaconStore and initializes list
     */
    private BeaconStore() {
        mBeacons = new ArrayList<>();
    }

    /**
     * Gets instance of this beacons store.
     *
     * @return BeaconStore.
     */
    public static BeaconStore getInstance() {
        if (sBeaconStore == null) sBeaconStore = new BeaconStore();
        return sBeaconStore;
    }

    /**
     * Deletes instance of this beacon store.
     */
    public static void deleteInstance() {
        sBeaconStore = null;
    }

    /**
     * Get sorted list of beacons.
     * List is sorted by distance.
     *
     * @return List of beacons.
     */
    public List<Beacon> getBeacons() {
        Collections.sort(mBeacons, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon lhs, Beacon rhs) {
                return Double.compare(lhs.getDistance(), rhs.getDistance());
            }
        });
        return mBeacons;
    }

    /**
     * Initializes list of beacons as the given list.
     *
     * @param beacons List of beacons.
     */
    public void updateBeacons(Collection<Beacon> beacons) {
        mBeacons = (List<Beacon>) beacons;
    }

    /**
     * Removes all beacons in the list.
     */
    public void clearBeacons() {
        mBeacons.clear();
    }
}
