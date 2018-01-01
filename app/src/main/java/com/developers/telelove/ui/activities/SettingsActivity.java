package com.developers.telelove.ui.activities;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.developers.telelove.App;
import com.developers.telelove.R;
import com.developers.telelove.service.QuoteJobService;
import com.developers.telelove.ui.MainFragment;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import javax.inject.Inject;

import static com.firebase.jobdispatcher.FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content,
                new PrefFrag()).commit();
    }

    public static class PrefFrag extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {

        private static int periodicityForDayOne = 86400;
        private static int periodicityForTwoTimesInDay = 43200;
        private static int periodicityForThreeTimesInDay = 28800;
        @Inject
        SharedPreferences sharedPreferences;
        @Inject
        FirebaseJobDispatcher dispatcher;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((App) getActivity().getApplication()).getNetComponent().inject(this);
            addPreferencesFromResource(R.xml.settings_activity);
            bindPreference(findPreference(getString(R.string.preferences_key)));
            Preference switchPreference = findPreference("quote_preference");
            Preference listPreferenceForTime = findPreference("time");
            switchPreference.setOnPreferenceChangeListener((preference, o) -> {
                if (preference instanceof SwitchPreference) {
                    boolean quotePref = (Boolean) o;
                    if (quotePref) {
                        preference.setSummary("Enabled");
                        listPreferenceForTime.setEnabled(true);
                    } else {
                        preference.setSummary("Disabled");
                        dispatcher.cancelAll();
                        listPreferenceForTime.setEnabled(false);
                    }
                }
                return true;
            });
            listPreferenceForTime.setOnPreferenceChangeListener((preference, o) -> {
                String stringValue = o.toString();
                if (preference instanceof ListPreference) {
                    ListPreference listPreferenceTime = (ListPreference) preference;
                    int prefIndex = listPreferenceTime.findIndexOfValue(stringValue);
                    if (prefIndex >= 0) {
                        Log.d("SettingsFrag", "Clicked: " + listPreferenceTime.getEntries()[prefIndex]);
                        String clicked = String.valueOf
                                (listPreferenceTime.getEntries()[prefIndex]);
                        if (clicked.equals("Once a day")) {
                            dispatcher.cancelAll();
                            Job quoteJob = dispatcher.newJobBuilder()
                                    .setService(QuoteJobService.class)
                                    .setTag("quote")
                                    .setRecurring(true)
                                    .setTrigger(Trigger
                                            .executionWindow(periodicityForDayOne,
                                                    periodicityForDayOne + 30))
                                    .build();
                            dispatcher.schedule(quoteJob);
                            if (dispatcher.schedule(quoteJob) == SCHEDULE_RESULT_SUCCESS) {
                                Toast.makeText(getActivity(),
                                        "Quotes set to receive one time in a Day",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (clicked.equals("Twice a day")) {
                            dispatcher.cancelAll();
                            Job quoteJob = dispatcher.newJobBuilder()
                                    .setService(QuoteJobService.class)
                                    .setTag("quote")
                                    .setRecurring(true)
                                    .setTrigger(Trigger
                                            .executionWindow(periodicityForTwoTimesInDay,
                                                    periodicityForTwoTimesInDay + 30))
                                    .build();
                            dispatcher.schedule(quoteJob);
                            if (dispatcher.schedule(quoteJob) == SCHEDULE_RESULT_SUCCESS) {
                                Toast.makeText(getActivity(),
                                        "Quotes set to receive two time in a Day",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (clicked.equals("Thrice a day")) {
                            dispatcher.cancelAll();
                            Job quoteJob = dispatcher.newJobBuilder()
                                    .setService(QuoteJobService.class)
                                    .setTag("quote")
                                    .setRecurring(true)
                                    .setTrigger(Trigger
                                            .executionWindow(periodicityForThreeTimesInDay,
                                                    periodicityForThreeTimesInDay + 30))
                                    .build();
                            dispatcher.schedule(quoteJob);
                            if (dispatcher.schedule(quoteJob) == SCHEDULE_RESULT_SUCCESS) {
                                Toast.makeText(getActivity(),
                                        "Quotes set to receive three time in a Day",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        preference.setSummary(listPreferenceTime.getEntries()[prefIndex]);
                    }
                }
                return true;
            });
        }

        private void bindPreference(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference, PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {

            String stringValue = o.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    sharedPreferences.edit().putString(getString(R.string.preferences_key)
                            , String.valueOf(prefIndex)).apply();
                    MainFragment.changed = true;
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                // For other preferences, set the summary
                // to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return false;
        }
    }
}
