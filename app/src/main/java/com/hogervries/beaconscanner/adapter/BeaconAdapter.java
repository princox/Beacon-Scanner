package com.hogervries.beaconscanner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogervries.beaconscanner.R;

import org.altbeacon.beacon.Beacon;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde.
 * @author Mitchell de Vries.
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconHolder> {

    /**
     * Callback which has to be implemented by the hosting activity.
     * <p/>
     * Callback interface allows for a component to be a completely self-contained,
     * modular component that defines its own layout and behaviour.
     */
    public interface OnBeaconSelectedListener {

        /**
         * Handles on beacon selected event.
         *
         * @param beacon Selected beacon.
         */
        void onBeaconSelected(Beacon beacon);
    }

    private List<Beacon> mBeacons;
    private OnBeaconSelectedListener mCallback;
    private Context mContext;

    /**
     * Creates a new Beacon adapter.
     *
     * @param beacons List of beacons.
     * @param callback OnBeaconSelected callback.
     * @param context Context.
     */
    public BeaconAdapter(List<Beacon> beacons, OnBeaconSelectedListener callback, Context context) {
        mBeacons = beacons;
        mCallback = callback;
        mContext = context;
    }

    /**
     * Sets list of beacons.
     *
     * @param beacons List of beacons.
     */
    public void setBeacons(List<Beacon> beacons) {
        mBeacons = beacons;
    }

    @Override
    public BeaconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View beaconItemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_beacon, parent, false);
        return new BeaconHolder(beaconItemView);
    }

    @Override
    public void onBindViewHolder(BeaconHolder holder, int position) {
        // Binds beacon to view holder.
        holder.bindBeacon(mBeacons.get(position));
    }

    @Override
    public int getItemCount() {
        return mBeacons.size();
    }

    /**
     * View holder for list items displaying beacon data.
     */
    class BeaconHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.list_item_beacon_title) TextView mBeaconTitle;
        @Bind(R.id.list_item_beacon_distance) TextView mBeaconDistance;
        @Bind(R.id.list_item_beacon_major_minor) TextView mBeaconMajorMinor;

        private Beacon mBeacon;

        /**
         * Creates a new Beacon holder.
         *
         * @param itemView View holder layout.
         */
        public BeaconHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Binds beacon to view holder to display its data.
         *
         * @param beacon Beacon
         */
        public void bindBeacon(Beacon beacon) {
            mBeacon = beacon;
            mBeaconTitle.setText(mBeacon.getId1().toString());
            mBeaconDistance.setText(mContext.getString(R.string.list_item_distance, String.format("%.2f", mBeacon.getDistance())));
            mBeaconMajorMinor.setText(mContext.getString(R.string.list_item_major_minor, mBeacon.getId2(), mBeacon.getId3()));
        }

        @Override
        public void onClick(View v) {
            mCallback.onBeaconSelected(mBeacon);
        }
    }
}
