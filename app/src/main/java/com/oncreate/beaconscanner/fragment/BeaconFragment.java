package com.oncreate.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oncreate.beaconscanner.R;

import org.altbeacon.beacon.Beacon;

import java.util.concurrent.Callable;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconFragment extends Fragment {

    private static final String ARG_BEACON = "arg_beacon";

    private Beacon mBeacon;

    public static BeaconFragment newInstance(Beacon beacon) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_BEACON, beacon);
        BeaconFragment fragment = new BeaconFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeacon = getArguments().getParcelable(ARG_BEACON);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View beaconView = inflater.inflate(R.layout.fragment_beacon, container, false);
        return beaconView;
    }
}
