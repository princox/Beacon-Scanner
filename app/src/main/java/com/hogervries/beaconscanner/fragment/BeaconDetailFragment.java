package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogervries.beaconscanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconDetailFragment extends Fragment {

    @BindView(R.id.detail_field_uuid)
    TextView detailFieldUuid;
    @BindView(R.id.detail_field_blt_address)
    TextView detailFieldBluetoothAddress;
    @BindView(R.id.detail_field_minor)
    TextView detailFieldMinor;
    @BindView(R.id.detail_field_major)
    TextView detailFieldMajor;
    @BindView(R.id.detail_field_rssi)
    TextView detailFieldRssi;
    @BindView(R.id.detail_field_tx)
    TextView detailFieldTxPower;
    @BindView(R.id.detail_field_type)
    TextView detailFieldType;
    @BindView(R.id.detail_field_manufacturer)
    TextView detailFieldManufacturer;

    public static BeaconDetailFragment newInstance() {
        return new BeaconDetailFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacon_detail, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

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
        detailFieldType.setText("IBeacon");
        detailFieldManufacturer.setText("Radius Labs");
    }
}
