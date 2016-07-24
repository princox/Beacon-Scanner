package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.fragment.dialog.GeneralDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick(R.id.settings_tracking)
    void onTrackingSettingClick(){
        GeneralDialogFragment fragment = new GeneralDialogFragment();
        fragment.newInstance("TrackingAge", "test", new GeneralDialogFragment.OnDialogFragmentClickListener() {
            @Override
            public void onSaveClicked() {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("Test", "test").apply();
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    @OnClick(R.id.settings_notifications)
    void onNotificationSettingClick(){
        GeneralDialogFragment fragment = new GeneralDialogFragment();
        fragment.newInstance("Notification", "test", new GeneralDialogFragment.OnDialogFragmentClickListener() {
            @Override
            public void onSaveClicked() {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("Test", "test").apply();
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

}
