package loozer.billgen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class UserSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String GHEE_KEY = "prefGheeRate";
    private final String BUTTER_KEY = "prefButterRate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference etpGhee = (EditTextPreference)findPreference(GHEE_KEY);
        EditTextPreference etpButter = (EditTextPreference)findPreference(BUTTER_KEY);

        etpGhee.setSummary("Current Rate is: Rs. "+sp.getString(GHEE_KEY,"0.0")+".");
        etpButter.setSummary("Current Rate is: Rs. "+sp.getString(BUTTER_KEY,"0.0")+".");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(preference instanceof EditTextPreference){
            EditTextPreference etp = (EditTextPreference)preference;
            preference.setSummary("Current Rate is: Rs. "+etp.getText()+".");
        }
    }
}
