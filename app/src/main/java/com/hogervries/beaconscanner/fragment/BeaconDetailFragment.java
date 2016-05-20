package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogervries.beaconscanner.Beacon;
import com.hogervries.beaconscanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Trifork Netherlands.
 * GuestApp Social.
 * Mitchell de Vries & Mohammed Ali.
 */

public class BeaconDetailFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_field_uuid)
    TextView detailFieldUuid;
    @BindView(R.id.detail_field_bluetooth_address)
    TextView detailFieldBluetoothAddress;
    @BindView(R.id.detail_field_minor)
    TextView detailFieldMinor;
    @BindView(R.id.detail_field_major)
    TextView detailFieldMajor;
    @BindView(R.id.detail_field_rssi)
    TextView detailFieldRssi;
    @BindView(R.id.detail_field_tx_power)
    TextView detailFieldTxPower;

    public static BeaconDetailFragment newInstance() {
        return new BeaconDetailFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacon_detail, container, false);
        ButterKnife.bind(this, view);

        toolbar.setTitle("iBeacon");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        bindViews();
        return view;
    }

    private void bindViews() {
        detailFieldUuid.setText("000000-0000000-0000000-000000-000000");
        detailFieldBluetoothAddress.setText("SDAFG2345");
        detailFieldMinor.setText("22.4");
        detailFieldMajor.setText("13.7");
        detailFieldRssi.setText("-59");
        detailFieldTxPower.setText("10");
    }
}
