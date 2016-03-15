package com.hogervries.beaconscanner.fragment;

import android.annotation.TargetApi;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.hogervries.beaconscanner.BeaconStore;
import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.activity.Intro;
import com.hogervries.beaconscanner.activity.SettingsActivity;
import com.hogervries.beaconscanner.adapter.BeaconAdapter;
import com.hogervries.beaconscanner.adapter.BeaconAdapter.OnBeaconSelectedListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Beacon Scanner, file created on 10/03/16.
 * <p/>
 * This fragment scans for beacons and transmits as a beacon.
 * If there are beacons in the area a list will be displayed.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class ScanTransmitFragment extends Fragment implements BeaconConsumer {

    private static final int PERMISSION_COARSE_LOCATION = 1;

    private static final int SCANNING = 0;
    private static final int TRANSMITTING = 1;
    private static final String REGION_ID = "Beacon_scanner_region";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_title) TextView toolbarTitleText;
    @Bind(R.id.scan_circle) ImageView startButtonOuterCircle;
    @Bind(R.id.start_scan_button) ImageButton startButton;
    @Bind(R.id.stop_scan_button) ImageButton stopButton;
    @Bind(R.id.pulse_ring) ImageView pulsingRing;
    @Bind(R.id.scan_transmit_layout) RelativeLayout switchModeLayout;
    @Bind(R.id.scan_transmit_switch) Switch scanTransmitSwitch;
    @Bind(R.id.scan_switch_button) Button scanModeButton;
    @Bind(R.id.transmit_switch_button) Button transmitModeButton;
    @Bind(R.id.slide_layout) FrameLayout slidingList;
    @Bind(R.id.beacon_recycler_view) RecyclerView beaconRecycler;
    @BindColor(R.color.colorWhite) int white;
    @BindColor(R.color.colorGrey) int grey;

    private int mode;
    private boolean isScanning;
    private boolean isTransmitting;

    private MenuItem stopScanMenuItem;
    private BeaconManager beaconManager;
    private BeaconTransmitter beaconTransmitter;
    private OnBeaconSelectedListener beaconSelectedCallback;
    private BeaconStore beaconStore;
    private SharedPreferences preferences;


    public static ScanTransmitFragment newInstance() {
        return new ScanTransmitFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            beaconSelectedCallback = (OnBeaconSelectedListener) context;
        } catch (ClassCastException notImplementedException) {
            throw new ClassCastException(context.toString()
                    + " must implement OnBeaconSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        checkFirstRun();

        setHasOptionsMenu(true);
    }

    private void checkFirstRun(){
        if (preferences.getBoolean("firstrun", true)) {
            startActivity(new Intent(getActivity(), Intro.class));
            preferences.edit().putBoolean("firstrun", false).apply();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View beaconListView = inflater.inflate(R.layout.fragment_beacon_list, container, false);
        ButterKnife.bind(this, beaconListView);
        // Setting toolbar.
        setToolbar();
        // Getting instance of beacon store.
        beaconStore = BeaconStore.getInstance();
        // Setting linear layout manager as layout manager for the beacon recycler view.
        beaconRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Updates user interface so that all the right views are displayed.
        updateUI();

        return beaconListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpBeaconManager();
        setUpBeaconTransmitter();
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
                stopBluetoothProcess();
                return true;
            case R.id.settings:
                final Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                if (isScanning || isTransmitting) {
                    stopBluetoothProcess();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openSettings(settingsIntent);
                        }
                    }, 400);
                } else {
                    stopBluetoothProcess();
                    openSettings(settingsIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings(Intent settingsIntent) {
        startActivity(settingsIntent);
        getActivity().overridePendingTransition(R.anim.anim_transition_from_right, R.anim.anim_transition_fade_out);
    }

    private void stopBluetoothProcess() {
        if (isScanning) {
            beaconStore.clearBeacons();
            stopScanning();
            updateUI();
            stopScanMenuItem.setVisible(false);
        } else if (isTransmitting) {
            stopTransmitting();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        beaconManager.unbind(this);
        BeaconStore.deleteInstance();
    }

    private void setToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void setToolbarTitleText(@StringRes int title) {
        toolbarTitleText.setText(title);
    }

    private void setUpBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        // Sets beacon layouts so that the app knows for what type of beacons to look.
        setBeaconLayouts();
        // Sets scanning periods.
        beaconManager.setForegroundScanPeriod(Integer.parseInt(preferences.getString("key_scan_period", "1100")));
        // Sets between scanning periods.
        beaconManager.setForegroundBetweenScanPeriod(Integer.parseInt(preferences.getString("key_between_scan_period", "0")));
        // Initializing cache and setting tracking age.
        BeaconManager.setUseTrackingCache(true);
        beaconManager.setMaxTrackingAge(Integer.parseInt(preferences.getString("key_tracking_age", "5000")));
    }

    private void setBeaconLayouts() {
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
    }

    private void setUpBeaconTransmitter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Beacon beacon = new Beacon.Builder()
                .setId1(preferences.getString("key_beacon_uuid", "0"))
                .setId2(preferences.getString("key_major", "0"))
                .setId3(preferences.getString("key_minor", "0"))
                .setBluetoothName(preferences.getString("key_beacon_name", "My Beacon"))
                .setManufacturer(0x0118)
                .setTxPower(Integer.parseInt(preferences.getString("key_power", "-59")))
                .setDataFields(Arrays.asList(new Long[]{0l}))
                .build();
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT);
        beaconTransmitter = new BeaconTransmitter(getActivity(), beaconParser);
        beaconTransmitter.setBeacon(beacon);
        setAdvertisingMode();
    }

    private void updateUI() {
        List<Beacon> beacons = beaconStore.getBeacons();
        beaconRecycler.setAdapter(new BeaconAdapter(beacons, beaconSelectedCallback, getActivity()));
        setSlidingList(beacons);
    }

    private void setSlidingList(List<Beacon> beacons) {
        if (!beacons.isEmpty() && slidingList.getVisibility() == View.INVISIBLE) {
            slideUpBeaconList(); // Animates sliding up.
            stopScanMenuItem.setVisible(true);
        } else if (beacons.isEmpty() && slidingList.getVisibility() == View.VISIBLE) {
            slideDownBeaconList(); // Animates sliding down.
            stopScanMenuItem.setVisible(false);
        }
    }

    private void slideUpBeaconList() {
        slidingList.setVisibility(View.VISIBLE);
        slidingList.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_up));
    }

    private void slideDownBeaconList() {
        slidingList.setVisibility(View.INVISIBLE);
        slidingList.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_down));
    }

    @OnClick({R.id.start_scan_button, R.id.stop_scan_button, R.id.scan_circle})
    void onScanButtonClick() {
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            requestLocationPermission();
        } else {
            if (mode == SCANNING) {
                toggleScanning();
            } else if (mode == TRANSMITTING) {
                toggleTransmitting();
            }
        }
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
        if (!isScanning) {
            startScanning();
        } else {
            stopScanning();
        }
    }

    private void startScanning() {
        if (!beaconManager.checkAvailability()) {
            requestBluetooth();
        } else {
            isScanning = true;
            switchModeLayout.setVisibility(View.INVISIBLE);
            beaconManager.bind(this); // Beacon manager binds the beacon consumer and starts service.
            startAnimation();
        }
    }

    private void stopScanning() {
        isScanning = false;
        switchModeLayout.setVisibility(View.VISIBLE);
        beaconManager.unbind(this); // Beacon manager unbinds the beacon consumer and stops service.
        stopAnimation();
    }

    private void toggleTransmitting() {
        if (!isTransmitting) startTransmitting();
        else stopTransmitting();
    }

    private void startTransmitting() {
        if (!beaconManager.checkAvailability()) {
            requestBluetooth();
        } else {
            if (!(BeaconTransmitter.checkTransmissionSupported(getActivity()) == BeaconTransmitter.SUPPORTED)) {
                notifyTransmittingNotSupported();
            } else {
                isTransmitting = true;
                switchModeLayout.setVisibility(View.INVISIBLE);
                beaconTransmitter.startAdvertising();
                startAnimation();
            }
        }
    }

    private void stopTransmitting() {
        isTransmitting = false;
        switchModeLayout.setVisibility(View.VISIBLE);
        beaconTransmitter.stopAdvertising();
        stopAnimation();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAdvertisingMode() {
        beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
    }

    private void startAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_in));
        startButton.setImageResource(R.drawable.ic_circle);
        stopButton.setVisibility(View.VISIBLE);
        pulseAnimation();
    }

    private void stopAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_out));
        startButton.setImageResource(mode == SCANNING ? R.drawable.ic_button_scan : R.drawable.ic_button_transmit);
        stopButton.setVisibility(View.INVISIBLE);
        pulsingRing.clearAnimation();
    }

    private void pulseAnimation() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse));
        pulsingRing.startAnimation(set);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                beaconStore.updateBeacons(collection);
                // If this fragment is added to its parent activity it will update the UI.
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // If the app is scanning then update the UI.
                            // This prevents showing a list when scanning has already stopped.
                            if (isScanning) updateUI();
                        }
                    });
                }
            }
        });

        try {
            // Starting ranging of beacons in our defined region.
            beaconManager.startRangingBeaconsInRegion(new Region(REGION_ID, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
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
                }).show();
    }

    private void notifyTransmittingNotSupported() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.transmitting_not_supported))
                .setMessage(getString(R.string.transmitting_not_supported_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        Assent.requestPermissions(new AssentCallback() {
            @Override
            public void onPermissionResult(PermissionResultSet permissionResultSet) {
                // Intentionally left blank
            }
        }, PERMISSION_COARSE_LOCATION, Assent.ACCESS_COARSE_LOCATION);
    }
}
