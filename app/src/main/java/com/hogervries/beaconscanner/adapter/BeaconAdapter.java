package com.hogervries.beaconscanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.activity.BeaconDetailActivity;
import com.hogervries.beaconscanner.fragment.BeaconDetailFragment;

import org.altbeacon.beacon.Beacon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde.
 * @author Mitchell de Vries.
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconHolder> {

    private List<com.hogervries.beaconscanner.Beacon> beacons;
    private OnBeaconSelectedListener beaconSelectedListener;
    private Context context;

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

    /**
     * Creates a new Beacon adapter.
     *
     * @param beacons  List of beacons.
     * @param beaconSelectedListener OnBeaconSelected callback.
     * @param context  Context.
     */
    public BeaconAdapter(List<com.hogervries.beaconscanner.Beacon> beacons, OnBeaconSelectedListener beaconSelectedListener, Context context) {
        this.beacons = beacons;
        this.beaconSelectedListener = beaconSelectedListener;
        this.context = context;
    }

    @Override
    public BeaconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View beaconItemView = LayoutInflater.from(context).inflate(R.layout.list_item_beacon, parent, false);
        return new BeaconHolder(beaconItemView);
    }

    @Override
    public void onBindViewHolder(BeaconHolder holder, int position) {
        // Binds beacon to view holder.
        holder.bindBeacon(beacons.get(position));
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    /**
     * View holder for list items displaying beacon data.
     */
    class BeaconHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.beacon_type_text) TextView beaconTypeTextView;
        @BindView(R.id.beacon_uuid_text) TextView uuidTextView;
        @BindView(R.id.beacon_distance_text) TextView distanceTextView;

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
        public void bindBeacon(com.hogervries.beaconscanner.Beacon beacon) {
            beaconTypeTextView.setText("Beacon");
            uuidTextView.setText("UUID");
            distanceTextView.setText("Distance");
        }

        @Override
        public void onClick(View v) {
            // TODO: 19/05/16 implement listener action.
            context.startActivity(new Intent(context, BeaconDetailActivity.class));
        }
    }
}
