package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogervries.beaconscanner.R;

import org.altbeacon.beacon.Beacon;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconFragment extends Fragment {

    private static final String ARG_BEACON = "arg_beacon";

    @Bind(R.id.detail_field_uuid)
    TextView mDetailFieldUuid;
    @Bind(R.id.detail_field_minor)
    TextView mDetailFieldMinor;
    @Bind(R.id.detail_field_major)
    TextView mDetailFieldMajor;
    @Bind(R.id.detail_field_distance)
    TextView mDetailFieldDistance;
    @Bind(R.id.detail_field_bluetooth_address)
    TextView mDetailFieldBluetoothAddress;
    @Bind(R.id.detail_field_manufacturer)
    TextView mDetailFieldManufacturer;
    @Bind(R.id.detail_field_rssi)
    TextView mDetailFieldRssi;
    @Bind(R.id.detail_field_service_uuid)
    TextView mDetailFieldServiceUuid;
    @Bind(R.id.detail_field_tx_power)
    TextView mDetailFieldTxPower;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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
        ButterKnife.bind(this, beaconView);

        setUpActionBar();

        bindBeacon(mBeacon);

        return beaconView;
    }

    private void setUpActionBar(){
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
    }

    private void bindBeacon(Beacon beacon) {
        mDetailFieldUuid.setText(beacon.getId1().toString());
        mDetailFieldMajor.setText(beacon.getId2().toString());
        mDetailFieldMinor.setText(beacon.getId3().toString());
        mDetailFieldDistance.setText(String.valueOf(beacon.getDistance()));
        mDetailFieldBluetoothAddress.setText(beacon.getBluetoothAddress());
        mDetailFieldManufacturer.setText(String.valueOf(beacon.getManufacturer()));
        mDetailFieldRssi.setText(String.valueOf(beacon.getRssi()));
        mDetailFieldServiceUuid.setText(String.valueOf(beacon.getServiceUuid()));
        mDetailFieldTxPower.setText(String.valueOf(beacon.getTxPower()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
