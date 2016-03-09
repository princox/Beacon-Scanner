package com.oncreate.beaconscanner.fragment;

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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.oncreate.beaconscanner.BeaconStore;
import com.oncreate.beaconscanner.R;
import com.oncreate.beaconscanner.adapter.BeaconAdapter;
import com.oncreate.beaconscanner.adapter.BeaconAdapter.OnBeaconSelectedListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.ANCHORED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconListFragment extends Fragment implements BeaconConsumer {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static final int TRACKING_AGE = 5000;
    private static final long FOREGROUND_SCAN_PERIOD = 1100L;
    private static final long FOREGROUND_BETWEEN_SCAN_PERIOD = 0L;
    private static final String REGION_ID = "Beacon_Scanner_Region";
    private static final String APPLE_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.sliding_layout) SlidingUpPanelLayout mPanelLayout;
    @Bind(R.id.scan_circle) ImageView mScanButton;
    @Bind(R.id.beacon_recycler_view) RecyclerView mBeaconRecyclerView;
    @BindColor(R.color.colorPrimary) int mPrimaryColor;

    private boolean mScanning;
    private BeaconManager mBeaconManager;
    private OnBeaconSelectedListener mCallback;
    private BeaconAdapter mBeaconAdapter;
    private BeaconStore mBeaconStore;

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
        View beaconListView = inflater.inflate(R.layout.fragment_beacon_list, container, false);
        ButterKnife.bind(this, beaconListView);

        initToolbar();

        requestPermissions();

        mPanelLayout.setTouchEnabled(false);

        mBeaconRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBeaconStore = BeaconStore.getInstance();

        setUpBeaconManager();

        return beaconListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        verifyBluetooth();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_beacon_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_visit_site:
                CustomTabsIntent visitSiteIntent = new CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .setToolbarColor(mPrimaryColor)
                        .build();
                visitSiteIntent.launchUrl(getActivity(), Uri.parse("http://www.google.com"));
                return true;
            case R.id.menu_item_reset:
                mBeaconStore.clearBeacons();
                updateLayout(mBeaconStore.getBeacons());
                onScanClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) result = getResources().getDimensionPixelSize(resourceId);
        return result;
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

    @OnClick({R.id.scan_button, R.id.scan_circle})
    void onScanClick() {
        if (!mBeaconManager.checkAvailability() && !mScanning) {
            verifyBluetooth();
        } else {
            mScanning = !mScanning;
            scan();
        }
    }

    private void updateUI() {
        if (isAdded()) {
            List<Beacon> beacons = mBeaconStore.getBeacons();

            if (mBeaconAdapter == null) {
                mBeaconAdapter = new BeaconAdapter(beacons, mCallback, getActivity());
                mBeaconRecyclerView.setAdapter(mBeaconAdapter);
            } else {
                mBeaconAdapter.setBeacons(beacons);
                mBeaconAdapter.notifyDataSetChanged();
            }

            updateLayout(beacons);
        }
    }

    private void updateLayout(List<Beacon> beacons) {
        PanelState panelState = beacons.isEmpty() ? COLLAPSED : ANCHORED;
        mPanelLayout.setPanelState(panelState);
    }

    private void animateScanButton() {
        mScanButton.startAnimation(mScanning ?
                AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_in) :
                AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_out));
    }

    private void scan() {
        if (mScanning) mBeaconManager.bind(this);
        else mBeaconManager.unbind(this);
        animateScanButton();
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
                            if (mScanning) updateUI();
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
                    // Intentionally left
                }
            }, PERMISSION_REQUEST_COARSE_LOCATION, Assent.ACCESS_COARSE_LOCATION);
        }
    }

    private void verifyBluetooth() {
        if (!mBeaconManager.checkAvailability()) {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mBeaconManager.unbind(this);
        BeaconStore.deleteInstance();
    }
}
