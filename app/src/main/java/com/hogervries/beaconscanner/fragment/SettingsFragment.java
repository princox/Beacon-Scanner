package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hogervries.beaconscanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Trifork Netherlands.
 * GuestApp Social.
 * Mitchell de Vries & Mohammed Ali.
 */

public class SettingsFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        return view;
    }


}
