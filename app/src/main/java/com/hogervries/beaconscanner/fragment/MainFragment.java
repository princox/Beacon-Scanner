package com.hogervries.beaconscanner.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.hogervries.beaconscanner.Scanner;
import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.activity.SettingsActivity;
import com.hogervries.beaconscanner.adapter.BeaconAdapter;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BleNotAvailableException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.hogervries.beaconscanner.Scanner.*;

/**
 * Beacon Scanner, file created on 10/03/16.
 * <p>
 * This fragment scans for beacons and transmits as a beacon.
 * If there are beacons in the area a list will be displayed.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class MainFragment extends Fragment implements OnScanBeaconsListener {

    private static final int SCANNING = 0;
    private static final int TRANSMITTING = 1;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.start_button_circle) ImageView startButtonCircle;
    @BindView(R.id.pulse_ring) ImageView pulseRing;
    @BindView(R.id.start_button) ImageButton startButton;
    @BindView(R.id.switch_layout) RelativeLayout switchLayout;
    @BindView(R.id.mode_switch) Switch modeSwitch;
    @BindView(R.id.scan_mode_button) Button scanModeButton;
    @BindView(R.id.transmit_mode_button) Button transmitModeButton;
    @BindView(R.id.list_layout) CoordinatorLayout listLayout;
    @BindView(R.id.beacon_recycler) RecyclerView beaconRecycler;
    @BindColor(R.color.colorWhite) int white;
    @BindColor(R.color.colorGrey) int grey;

    private Scanner scanner;
    private BeaconManager beaconManager;
    private Unbinder unbinder;
    private MenuItem stopMenuItem;
    private boolean isScanning;
    private boolean isTransmitting;
    private int mode;

    private List<Beacon> beacons = new ArrayList<>();

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

        setToolbar();

        beaconRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());

        initBeaconScanService();

        disableSwitchDrag();

        return beaconListView;
    }

    private void updateUI() {
        beaconRecycler.setAdapter(new BeaconAdapter(beacons, null, getActivity()));
        if (beacons.size() > 0) {
            listLayout.setVisibility(View.VISIBLE);
            listLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_up));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_beacon_list, menu);
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
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_transition_from_right, R.anim.anim_transition_fade_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void setToolbarTitle(@StringRes int title) {
        toolbar.setTitle(title);
    }

    private void initBeaconScanService() {
        scanner = new Scanner(getActivity(), beaconManager, this);
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
        setToolbarTitle(R.string.beacon_scanner);
        mode = SCANNING;
        modeSwitch.setChecked(false);
        scanModeButton.setTextColor(white);
        transmitModeButton.setTextColor(grey);
        startButton.setImageResource(R.drawable.ic_button_scan);
    }

    @OnClick(R.id.transmit_mode_button)
    void switchToTransmitting() {
        setToolbarTitle(R.string.beacon_transmitter);
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
        boolean bleAvailable = false;

        try {
            bleAvailable = beaconManager.checkAvailability();
        } catch (BleNotAvailableException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (bleAvailable) {
            isScanning = true;
            switchLayout.setVisibility(View.INVISIBLE);
            startAnimation();
            beaconManager.bind(scanner);
            stopMenuItem.setVisible(true);
        } else {
            requestBluetooth();
        }
    }

    private void stopScanning() {
        isScanning = false;
        switchLayout.setVisibility(View.VISIBLE);
        stopAnimation();
        unbindBeaconManager();
        stopMenuItem.setVisible(false);
        if (listLayout.getVisibility() == View.VISIBLE) {
            beacons.clear();
            listLayout.setVisibility(View.INVISIBLE);
            listLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_down));
        }
    }

    private void unbindBeaconManager() {
        beaconManager.unbind(scanner);
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
    public void onPause() {
        super.onPause();
        if (isScanning) unbindBeaconManager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void requestBluetooth() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.bluetooth_not_enabled))
                .setMessage(getString(R.string.please_enable_bluetooth))
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Initializing intent to go to bluetooth settings.
                        Intent bltSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(bltSettingsIntent);
                    }
                })
                .show();
    }

    @Override
    public void onScanBeacons(Collection<Beacon> beacons) {
        this.beacons = (List<Beacon>) beacons;
        if (isAdded()) getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isScanning) {
                    updateUI();
                }
            }
        });
    }
}