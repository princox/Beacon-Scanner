package com.hogervries.beaconscanner.fragment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.hogervries.beaconscanner.R;

/**
 * Trifork
 * GuestAppSocial
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_scanning);

        bindPreferenceSummaryToValue(findPreference("key_tracking_age"));
        bindPreferenceSummaryToValue(findPreference("key_scan_period"));
        bindPreferenceSummaryToValue(findPreference("key_between_scan_period"));

        bindPreferenceSummaryToValue(findPreference("key_beacon_name"));
        bindPreferenceSummaryToValue(findPreference("key_beacon_uuid"));
        bindPreferenceSummaryToValue(findPreference("key_major"));
        bindPreferenceSummaryToValue(findPreference("key_minor"));
        bindPreferenceSummaryToValue(findPreference("key_power"));

    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
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
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

}
