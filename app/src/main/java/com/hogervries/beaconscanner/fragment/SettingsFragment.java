package com.hogervries.beaconscanner.fragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("preference_tracking_age"));
            bindPreferenceSummaryToValue(findPreference("preference_scan_frequency"));
            bindPreferenceSummaryToValue(findPreference("preference_between_scan_period"));
            bindPreferenceSummaryToValue(findPreference("preference_uuid"));
            bindPreferenceSummaryToValue(findPreference("preference_major"));
            bindPreferenceSummaryToValue(findPreference("preference_minor"));
            bindPreferenceSummaryToValue(findPreference("preference_tx_power"));
            bindPreferenceSummaryToValue(findPreference("preference_log_max_lines"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
