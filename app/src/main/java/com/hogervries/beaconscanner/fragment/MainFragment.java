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
import android.widget.TextView;

import com.hogervries.beaconscanner.R;

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
    @BindView(R.id.toolbar_title) TextView toolbarTitleText;
    @BindView(R.id.scan_circle) ImageView startButtonOuterCircle;
    @BindView(R.id.start_scan_button) ImageButton startButton;
    @BindView(R.id.stop_scan_button) ImageButton stopButton;
    @BindView(R.id.pulse_ring) ImageView pulsingRing;
    @BindView(R.id.scan_transmit_layout) RelativeLayout switchModeLayout;
    @BindView(R.id.scan_transmit_switch) Switch scanTransmitSwitch;
    @BindView(R.id.scan_switch_button) Button scanModeButton;
    @BindView(R.id.transmit_switch_button) Button transmitModeButton;
    @BindView(R.id.slide_layout) FrameLayout slidingList;
    @BindView(R.id.beacon_recycler_view) RecyclerView beaconRecycler;
    @BindColor(R.color.colorWhite) int white;
    @BindColor(R.color.colorGrey) int grey;

    private Unbinder butterknifeUnbinder;
    private MenuItem stopScanMenuItem;
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
        butterknifeUnbinder = ButterKnife.bind(this, beaconListView);
        // Setting toolbar.
        setToolbar();
        // Setting linear layout manager as layout manager for the beacon recycler view.
        beaconRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Disables dragging on switch button
        disableSwitchDrag();

        return beaconListView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_beacon_list, menu);
        // Initializing item as member so changes to its properties can be done within other methods.
        stopScanMenuItem = menu.findItem(R.id.stop_scanning);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_scanning:
                stopScanning();
                stopScanMenuItem.setVisible(false);
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
        toolbarTitleText.setText(title);
    }

    @OnClick({R.id.start_scan_button, R.id.stop_scan_button, R.id.scan_circle})
    void onScanButtonClick() {
        if (mode == SCANNING) toggleScanning();
        else toggleTransmitting();
    }

    @OnClick(R.id.scan_transmit_switch)
    void switchMode() {
        if (mode == SCANNING) switchToTransmitting();
        else switchToScanning();
    }

    @OnClick(R.id.scan_switch_button)
    void switchToScanning() {
        setToolbarTitleText(R.string.beacon_scanner);
        mode = SCANNING;
        scanTransmitSwitch.setChecked(false);
        scanModeButton.setTextColor(white);
        transmitModeButton.setTextColor(grey);
        startButton.setImageResource(R.drawable.ic_button_scan);
    }

    @OnClick(R.id.transmit_switch_button)
    void switchToTransmitting() {
        setToolbarTitleText(R.string.beacon_transmitter);
        mode = TRANSMITTING;
        scanTransmitSwitch.setChecked(true);
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
        switchModeLayout.setVisibility(View.INVISIBLE);
        startAnimation();
    }

    private void stopScanning() {
        isScanning = false;
        switchModeLayout.setVisibility(View.VISIBLE);
        stopAnimation();
    }

    private void toggleTransmitting() {
        if (!isTransmitting) startTransmitting();
        else stopTransmitting();
    }

    private void startTransmitting() {
        isTransmitting = true;
        switchModeLayout.setVisibility(View.INVISIBLE);
        startAnimation();
    }

    private void stopTransmitting() {
        isTransmitting = false;
        switchModeLayout.setVisibility(View.VISIBLE);
        stopAnimation();
    }

    private void startAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_zoom_in));

        startButton.setImageResource(R.drawable.ic_circle);
        stopButton.setVisibility(View.VISIBLE);
        pulseAnimation();
    }

    private void stopAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_zoom_out));

        startButton.setImageResource(mode == SCANNING ? R.drawable.ic_button_scan : R.drawable.ic_button_transmit);
        stopButton.setVisibility(View.INVISIBLE);
        pulsingRing.clearAnimation();
    }

    private void pulseAnimation() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse));
        pulsingRing.startAnimation(set);
    }

    private void disableSwitchDrag() {
        scanTransmitSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }
}