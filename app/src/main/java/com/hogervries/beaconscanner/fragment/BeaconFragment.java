package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.hogervries.beaconscanner.R;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconFragment extends Fragment {

    @BindView(R.id.rssi_chart) LineChart rssiChart;
    @BindView(R.id.rssi_text_view) TextView rssiTextView;
    @BindColor(R.color.colorPrimary) int red;
    @BindView(R.id.detail_field_uuid) TextView detailFieldUuid;
    @BindView(R.id.detail_field_blt_address) TextView detailFieldBltAddress;
    @BindView(R.id.detail_field_minor_major) TextView detailFieldMinorMajor;
    @BindView(R.id.detail_field_tx) TextView detailFieldTx;
    @BindView(R.id.detail_field_manufacturer) TextView detailFieldManufacturer;

    private Unbinder unbinder;

    public static BeaconFragment newInstance() {
        return new BeaconFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beacon_detail_with_graph, container, false);

        unbinder = ButterKnife.bind(this, view);

        bindFields();

        setUpLineChart();

        addMockDataToChart();

        return view;
    }

    private void bindFields() {
        detailFieldUuid.setText("F7826DA64ADSFF5988024BC5BT67REIE");
        detailFieldBltAddress.setText("E3:0B:6F:0B:85:A8");
        detailFieldMinorMajor.setText("49279 / 48202");
        detailFieldTx.setText("-66");
        detailFieldManufacturer.setText("Google inc.");
    }

    private void setUpLineChart() {
        rssiChart.setDescription(getString(R.string.chart_description));
        rssiChart.setDrawGridBackground(false);
        rssiChart.setTouchEnabled(false);
        rssiChart.getAxisLeft().setDrawAxisLine(false);
        rssiChart.getAxisLeft().setDrawGridLines(false);
        rssiChart.getAxisLeft().setTextSize(12f);
        rssiChart.getAxisRight().setEnabled(false);
        rssiChart.getXAxis().setEnabled(false);

        Legend legend = rssiChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12f);

        rssiChart.setData(new LineData());
        rssiChart.getData().addDataSet(createSet());

        rssiChart.invalidate();
    }

    private void addMockDataToChart() {
        for (int i = 0; i < 31; i++) {
            addRssi(getRandomRssi());
        }
    }

    private int getRandomRssi() {
        return -((int) (Math.random() * 39) + 50);
    }

    private void addRssi(int rssi) {
        LineData data = rssiChart.getData();

        ILineDataSet set = data.getDataSetByIndex(0);

        data.addXValue(String.valueOf(set.getEntryCount()));
        data.addEntry(new Entry(rssi, set.getEntryCount()), 0);

        rssiChart.notifyDataSetChanged();
        rssiChart.setVisibleXRangeMaximum(20);
        rssiChart.animateX(2500);
        rssiChart.moveViewToAnimated(data.getXValCount(), 50f, YAxis.AxisDependency.LEFT, 5000);

        rssiTextView.setText(getString(R.string.rssi_text, String.valueOf(rssi)));
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, getString(R.string.rssi_legend_label));

        set.setDrawCubic(true);
        set.setDrawValues(false);
        set.setCubicIntensity(0.2f);
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(red);
        set.setCircleColor(red);
        set.setCircleColorHole(red);
        set.setHighLightColor(red);

        return set;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
