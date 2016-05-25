package com.hogervries.beaconscanner.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hogervries.beaconscanner.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class SettingsFragment extends PreferenceFragment {

    private static final int MAX_VALUE_ID = 65535;

    @Bind(R.id.beacon_format_edit_text) EditText beaconFormatEditText;

    private Set<String> beaconFormatList;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_scanning);
        bindPreferenceToSummary();
        setOnPreferenceClickListeners();

        beaconFormatList = new HashSet<>();
        sharedPreferences = getPreferenceManager().getSharedPreferences();

        MultiSelectListPreference list = (MultiSelectListPreference) getPreferenceManager().findPreference("key_beacon_formats");

        Set<String> beaconFormats = sharedPreferences.getStringSet("key_beacon_formats", beaconFormatList);
        CharSequence[] sequences = beaconFormats.toArray(new CharSequence[beaconFormats.size()]);

        list.setEntries(sequences);
        list.setEntryValues(sequences);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        if (!(preference instanceof CheckBoxPreference)) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    private void setOnPreferenceClickListeners() {
        Preference button = getPreferenceManager().findPreference("key_add_beacon_format");
        button.setOnPreferenceClickListener(addBeaconFormat);

        Preference advancedSwitchPreferences = getPreferenceManager().findPreference("key_beacon_use_advanced");
        advancedSwitchPreferences.setOnPreferenceClickListener(advancedSwitch);


    }

    private void bindPreferenceToSummary() {
        bindPreferenceSummaryToValue(findPreference("key_tracking_age"));
        bindPreferenceSummaryToValue(findPreference("key_scan_period"));
        bindPreferenceSummaryToValue(findPreference("key_between_scan_period"));
        bindPreferenceSummaryToValue(findPreference("key_beacon_uuid"));
        bindPreferenceSummaryToValue(findPreference("key_major"));
        bindPreferenceSummaryToValue(findPreference("key_minor"));
        bindPreferenceSummaryToValue(findPreference("key_power"));
        bindPreferenceSummaryToValue(findPreference("key_beacon_advertisement"));
        bindPreferenceSummaryToValue(findPreference("key_logging"));
    }

    private void openBeaconFormatDialog(final Set<String> beaconFormats, final SharedPreferences preferences) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_beacon_format, null);

        ButterKnife.bind(this, dialogView);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Add beacon format");

        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                beaconFormats.add(beaconFormatEditText.getText().toString());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet("key_beacon_formats", beaconFormatList).apply();
                Toast.makeText(getActivity(), preferences.getStringSet("key_beacon_formats", beaconFormatList).toString(), Toast.LENGTH_SHORT).show();

            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        dialogBuilder.create().show();
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (preference instanceof CheckBoxPreference) {
                // Intentionally left blank.
            } else if (!(Integer.parseInt(value.toString()) < MAX_VALUE_ID)) {
                Toast.makeText(getActivity(), "Please enter a value between 0 - " + MAX_VALUE_ID, Toast.LENGTH_LONG).show();
                return false;
            } else preference.setSummary(stringValue);
            return true;
        }
    };

    private Preference.OnPreferenceClickListener advancedSwitch = new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
            boolean enabled = sharedPreferences.getBoolean("key_beacon_use_advanced", false);
            getPreferenceScreen().findPreference("key_beacon_formats").setEnabled(enabled);
            getPreferenceScreen().findPreference("key_add_beacon_format").setEnabled(enabled);
            return true;
        }
    };

    private Preference.OnPreferenceClickListener addBeaconFormat = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            openBeaconFormatDialog(beaconFormatList, sharedPreferences);
            return true;
        }
    };

}
