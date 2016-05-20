package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.adapter.BeaconAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Beacon Scanner, file created on 10/03/16.
 * <p>
 * This fragment scans for beacons and transmits as a beacon.
 * If there are beacons in the area a list will be displayed.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class MainFragment extends Fragment {

    private static final int SCANNING = 0;
    private static final int TRANSMITTING = 1;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.start_button_circle) ImageView startButtonCircle;
    @BindView(R.id.start_button) ImageButton startButton;
    @BindView(R.id.pulse_ring) ImageView pulseRing;
    @BindView(R.id.switch_layout) RelativeLayout switchLayout;
    @BindView(R.id.mode_switch) Switch modeSwitch;
    @BindView(R.id.scan_mode_button) Button scanModeButton;
    @BindView(R.id.transmit_mode_button) Button transmitModeButton;
    @BindView(R.id.list_layout) FrameLayout listLayout;
    @BindView(R.id.beacon_recycler) RecyclerView beaconRecycler;
    @BindColor(R.color.colorWhite) int white;
    @BindColor(R.color.colorGrey) int grey;

    private Unbinder unbinder;
    private MenuItem stopMenuItem;
    private boolean isScanning;
    private boolean isTransmitting;
    private int mode;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View beaconListView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, beaconListView);
        // Setting toolbar.
        setToolbar();
        // Setting linear layout manager as layout manager for the beacon recycler view.
        beaconRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        beaconRecycler.setAdapter(new BeaconAdapter(getBeacons(), null, getActivity()));
        // Disables dragging on switch button
        disableSwitchDrag();

        return beaconListView;
    }

    private List<com.hogervries.beaconscanner.Beacon> getBeacons() {
        List<com.hogervries.beaconscanner.Beacon> beacons = new ArrayList<>();
        for (int i = 0; i < 100; i++) beacons.add(new com.hogervries.beaconscanner.Beacon());
        return beacons;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_beacon_list, menu);
        // Initializing item as member so changes to its properties can be done within other methods.
        stopMenuItem = menu.findItem(R.id.stop_scanning);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_scanning:
                stopScanning();
                stopMenuItem.setVisible(false);
                return true;
            case R.id.settings:
                // TODO: 19/05/16 implement settings intent and anim.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void setToolbarTitleText(@StringRes int title) {

    }

    @OnClick({R.id.start_button, R.id.start_button_circle})
    void onScanButtonClick() {
        if (mode == SCANNING) toggleScanning();
        else toggleTransmitting();
    }

    @OnClick(R.id.mode_switch)
    void switchMode() {
        if (mode == SCANNING) switchToTransmitting();
        else switchToScanning();
    }

    @OnClick(R.id.scan_mode_button)
    void switchToScanning() {
        setToolbarTitleText(R.string.beacon_scanner);
        mode = SCANNING;
        modeSwitch.setChecked(false);
        scanModeButton.setTextColor(white);
        transmitModeButton.setTextColor(grey);
        startButton.setImageResource(R.drawable.ic_button_scan);
    }

    @OnClick(R.id.transmit_mode_button)
    void switchToTransmitting() {
        setToolbarTitleText(R.string.beacon_transmitter);
        mode = TRANSMITTING;
        modeSwitch.setChecked(true);
        scanModeButton.setTextColor(grey);
        transmitModeButton.setTextColor(white);
        startButton.setImageResource(R.drawable.ic_button_transmit);
    }

    private void toggleScanning() {
        if (!isScanning) startScanning();
        else stopScanning();
    }

    private void startScanning() {
        isScanning = true;
        switchLayout.setVisibility(View.INVISIBLE);
        startAnimation();
        listLayout.setVisibility(View.VISIBLE);
        listLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_up));
        stopMenuItem.setVisible(true);
    }

    private void stopScanning() {
        isScanning = false;
        switchLayout.setVisibility(View.VISIBLE);
        stopAnimation();
        listLayout.setVisibility(View.INVISIBLE);
        listLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_down));
    }

    private void toggleTransmitting() {
        if (!isTransmitting) startTransmitting();
        else stopTransmitting();
    }

    private void startTransmitting() {
        isTransmitting = true;
        switchLayout.setVisibility(View.INVISIBLE);
        startAnimation();
    }

    private void stopTransmitting() {
        isTransmitting = false;
        switchLayout.setVisibility(View.VISIBLE);
        stopAnimation();
    }

    private void startAnimation() {
        startButtonCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_zoom_in));

        startButton.setImageResource(R.drawable.ic_button_stop);
        pulseRing.setVisibility(View.VISIBLE);
        pulseAnimation();
    }

    private void stopAnimation() {
        startButtonCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_zoom_out));

        startButton.setImageResource(mode == SCANNING ? R.drawable.ic_button_scan : R.drawable.ic_button_transmit);
        pulseRing.clearAnimation();
        pulseRing.setVisibility(View.GONE);
    }

    private void pulseAnimation() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse));
        pulseRing.startAnimation(set);
    }

    private void disableSwitchDrag() {
        modeSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}