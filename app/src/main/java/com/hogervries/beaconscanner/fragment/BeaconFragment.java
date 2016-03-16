package com.hogervries.beaconscanner.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
public class BeaconFragment extends DialogFragment {

    private static final String ARG_BEACON = "arg_beacon";

    @Bind(R.id.detail_field_uuid) TextView detailFieldUuid;
    @Bind(R.id.detail_field_minor) TextView detailFieldMinor;
    @Bind(R.id.detail_field_major) TextView detailFieldMajor;
    @Bind(R.id.detail_field_distance) TextView detailFieldDistance;
    @Bind(R.id.detail_field_bluetooth_address) TextView detailFieldBluetoothAddress;
    @Bind(R.id.detail_field_manufacturer) TextView detailFieldManufacturer;
    @Bind(R.id.detail_field_rssi) TextView detailFieldRssi;
    @Bind(R.id.detail_field_service_uuid) TextView detailFieldServiceUuid;
    @Bind(R.id.detail_field_tx_power) TextView detailFieldTxPower;

    private Beacon mBeacon;

    public static BeaconFragment newInstance(Beacon beacon) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_BEACON, beacon);
        BeaconFragment fragment = new BeaconFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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

        bindBeacon(mBeacon);

        return beaconView;
    }

    private void bindBeacon(Beacon beacon) {
        detailFieldUuid.setText(beacon.getId1().toString());
        detailFieldMajor.setText(beacon.getId2().toString());
        detailFieldMinor.setText(beacon.getId3().toString());
        detailFieldDistance.setText(getString(R.string.distance, String.format("%.2f", beacon.getDistance())));
        detailFieldBluetoothAddress.setText(beacon.getBluetoothAddress());
        detailFieldManufacturer.setText(String.valueOf(beacon.getManufacturer()));
        detailFieldRssi.setText(String.valueOf(beacon.getRssi()));
        detailFieldServiceUuid.setText(String.valueOf(beacon.getServiceUuid()));
        detailFieldTxPower.setText(String.valueOf(beacon.getTxPower()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
