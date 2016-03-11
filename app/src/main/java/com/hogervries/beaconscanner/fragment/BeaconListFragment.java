package com.hogervries.beaconscanner.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.hogervries.beaconscanner.BeaconStore;
import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.adapter.BeaconAdapter;
import com.hogervries.beaconscanner.adapter.BeaconAdapter.OnBeaconSelectedListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Beacon Scanner, file created on 10/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconListFragment extends Fragment implements BeaconConsumer {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int TRACKING_AGE = 5000;
    private static final long FOREGROUND_SCAN_PERIOD = 1100L;
    private static final long FOREGROUND_BETWEEN_SCAN_PERIOD = 0L;
    private static final String REGION_ID = "Beacon_scanner_region";
    private static final String APPLE_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String URL_SITE = "https://github.com/Boyd261/Beacon-Scanner";

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.scan_circle) ImageView mScanCircleView;
    @Bind(R.id.start_scan_button) ImageButton mStartScanButton;
    @Bind(R.id.stop_scan_button) ImageButton mStopScanButton;
    @Bind(R.id.pulse_ring) ImageView mPulsingRing;
    @Bind(R.id.slide_layout) FrameLayout mSlideLayout;
    @Bind(R.id.beacon_recycler_view) RecyclerView mBeaconRecyclerView;
    @BindColor(R.color.colorPrimary) int mColorPrimary;

    private boolean mIsScanning;
    private MenuItem mStopScanItem;
    private BeaconManager mBeaconManager;
    private BeaconAdapter mBeaconAdapter;
    private OnBeaconSelectedListener mCallback;
    private BeaconStore mBeaconStore = BeaconStore.getInstance();

    public static BeaconListFragment newInstance() {
        return new BeaconListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnBeaconSelectedListener) context;
        } catch (ClassCastException notImplementedException) {
            throw new ClassCastException(context.toString()
                    + " must implement OnBeaconSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View beaconListView = inflater.inflate(R.layout.fragment_beacon_test, container, false);
        ButterKnife.bind(this, beaconListView);

        setToolbar();

        requestPermissions();

        setUpBeaconManager();

        mBeaconRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return beaconListView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_beacon_list, menu);
        mStopScanItem = menu.findItem(R.id.stop_scanning);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_scanning:
                mBeaconStore.clearBeacons();
                stopScan();
                updateUI();
                mStopScanItem.setVisible(false);
                return true;
            case R.id.about_us:
                launchWebsite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchWebsite() {
        CustomTabsIntent siteIntent = new CustomTabsIntent.Builder()
                .setToolbarColor(mColorPrimary)
                .setShowTitle(true)
                .build();
        siteIntent.launchUrl(getActivity(), Uri.parse(URL_SITE));
    }

    private void setToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    private void setUpBeaconManager() {
        mBeaconManager = BeaconManager.getInstanceForApplication(getActivity());
        // Sets beacon layouts so that the app knows for what type of beacons to look
        setBeaconLayouts();
        // Sets scanning periods
        mBeaconManager.setForegroundScanPeriod(FOREGROUND_SCAN_PERIOD);
        mBeaconManager.setForegroundBetweenScanPeriod(FOREGROUND_BETWEEN_SCAN_PERIOD);
        // Initializing cache and setting tracking age
        BeaconManager.setUseTrackingCache(true);
        mBeaconManager.setMaxTrackingAge(TRACKING_AGE);
    }

    private void setBeaconLayouts() {
        mBeaconManager.getBeaconParsers().clear();
        // Detect Apple iBeacon frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(APPLE_BEACON_LAYOUT));
        // Detect the Eddystone main identifier (UID) frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the Eddystone telemetry (TLM) frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the Eddystone URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
    }

    private void updateUI() {
        List<Beacon> beacons = mBeaconStore.getBeacons();

        if (mBeaconAdapter == null) {
            mBeaconAdapter = new BeaconAdapter(beacons, mCallback, getActivity());
            mBeaconRecyclerView.setAdapter(mBeaconAdapter);
        } else {
            mBeaconAdapter.setBeacons(beacons);
            mBeaconAdapter.notifyDataSetChanged();
        }

        setSlideLayout(beacons);
    }

    private void setSlideLayout(List<Beacon> beacons) {
        if (!beacons.isEmpty() && mSlideLayout.getVisibility() == View.INVISIBLE) {
            slideUpBeaconList();
            mStopScanItem.setVisible(true);
        } else if (beacons.isEmpty() && mSlideLayout.getVisibility() == View.VISIBLE) {
            slideDownBeaconList();
            mStopScanItem.setVisible(false);
        }
    }

    private void slideUpBeaconList() {
        mSlideLayout.setVisibility(View.VISIBLE);
        mSlideLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_up));
    }

    private void slideDownBeaconList() {
        mSlideLayout.setVisibility(View.INVISIBLE);
        mSlideLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_down));
    }

    @OnClick({R.id.start_scan_button, R.id.stop_scan_button, R.id.scan_circle})
    void onScanButtonClick() {
        if (!mIsScanning) {
            startScan();
        } else {
            stopScan();
        }
    }

    private void startScan() {
        if (!mBeaconManager.checkAvailability()) {
            requestBluetooth();
        } else {
            mIsScanning = true;
            mBeaconManager.bind(this);
            startScanAnimation();
        }
    }

    private void startScanAnimation() {
        mScanCircleView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_in));
        mStartScanButton.setImageResource(R.drawable.ic_pulse_circle);
        mStopScanButton.setVisibility(View.VISIBLE);
        pulseAnimation();
    }

    private void pulseAnimation() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse));
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_fade));
        mPulsingRing.startAnimation(set);
    }

    private void stopScan() {
        mIsScanning = false;
        mBeaconManager.unbind(this);
        stopScanAnimation();
    }

    private void stopScanAnimation() {
        mScanCircleView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_out));
        mStartScanButton.setImageResource(R.drawable.ic_bluetooth_scan);
        mStopScanButton.setVisibility(View.INVISIBLE);
        mPulsingRing.clearAnimation();
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                mBeaconStore.updateBeacons(collection);
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsScanning) updateUI();
                        }
                    });
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region(REGION_ID, null, null, null));
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

    private void requestPermissions() {
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet permissionResultSet) {
                    // Intentionally left blank
                }
            }, PERMISSION_REQUEST_COARSE_LOCATION, Assent.ACCESS_COARSE_LOCATION);
        }
    }

    private void requestBluetooth() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.bluetooth_not_enabled))
                .setMessage(getString(R.string.please_enable_bluetooth))
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent bltSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(bltSettingsIntent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mBeaconManager.unbind(this);
        BeaconStore.deleteInstance();
    }
}
