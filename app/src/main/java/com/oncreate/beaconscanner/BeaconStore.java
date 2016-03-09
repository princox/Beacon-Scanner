package com.oncreate.beaconscanner;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconStore {

    private static BeaconStore sBeaconStore;

    private List<Beacon> mBeacons;

    private BeaconStore() {
        mBeacons = new ArrayList<>();
    }

    public static BeaconStore getInstance() {
        if (sBeaconStore == null) sBeaconStore = new BeaconStore();
        return sBeaconStore;
    }

    public static void deleteInstance() {
        sBeaconStore = null;
    }

    public List<Beacon> getBeacons() {
        Collections.sort(mBeacons, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon lhs, Beacon rhs) {
                return Double.compare(lhs.getDistance(), rhs.getDistance());
            }
        });
        return mBeacons;
    }

    public void updateBeacons(Collection<Beacon> beacons) {
        mBeacons = (List<Beacon>) beacons;
    }
}
