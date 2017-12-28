package com.developers.telelove.ui.activities;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.developers.telelove.App;
import com.developers.telelove.R;
import com.developers.telelove.ui.MainFragment;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content,
                new PrefFrag()).commit();
    }

    public static class PrefFrag extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {

        @Inject
        SharedPreferences sharedPreferences;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((App) getActivity().getApplication()).getNetComponent().inject(this);
            addPreferencesFromResource(R.xml.settings_activity);
            bindPreference(findPreference(getString(R.string.preferences_key)));
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
                    MainFragment.changed=true;
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return false;
        }
    }
}
