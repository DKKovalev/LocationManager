package locmanager.dkovalev.com.locationmanager.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.Toast;

import com.activeandroid.query.Delete;

import locmanager.dkovalev.com.locationmanager.R;
import locmanager.dkovalev.com.locationmanager.assets.NewProfile;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_settings);

        Preference button = findPreference("clear");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(SettingsActivity.this, "Database has been cleared", Toast.LENGTH_SHORT).show();

                new Delete().from(NewProfile.class).execute();
                return true;
            }
        });

        final Preference vibratePref = findPreference("vibrate");
        vibratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                vibratePref.setEnabled(true);
                return false;
            }
        });
    }
}
