package net.itsjustsomedude.tokens;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import net.itsjustsomedude.tokens.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "Settings";
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);
        setTitle("Options");

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat serviceControlSwitch = findPreference("service_control_enable_service");
            if (serviceControlSwitch != null) {
                serviceControlSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isEnabled = (Boolean) newValue;
                    NotificationReader.setServiceEnabled(requireContext(), isEnabled);
                    Log.i(TAG, "Setting service to " + isEnabled);
                    return true;
                });
            }

            Preference templateButton = findPreference("btn_show_template_info");
            if (templateButton != null) {
                templateButton.setOnPreferenceClickListener((preference) -> {
                    SimpleDialogs.infoBox(
                            requireContext(),
                            getString(R.string.settings_reports_header),
                            getString(R.string.settings_templating_info)
                    );
                    return false;
                });
            }
        }
    }
}