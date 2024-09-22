package net.itsjustsomedude.tokens

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import net.itsjustsomedude.tokens.databinding.ActivitySettingsBinding

private const val TAG = "SettingsActivity"

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Options"

        if (savedInstanceState == null)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

//        findPreference<SwitchPreferenceCompat>("service_control_enable_service")?.apply {
//            onPreferenceChangeListener =
//                Preference.OnPreferenceChangeListener { _, newValue ->
//                    val newState = newValue as Boolean
//                    NotificationReader.setServiceEnabled(requireContext(), newState)
//                    Log.i(TAG, "Setting service to $newState")
//                    true
//                }
//        }
//
//        findPreference<Preference>("btn_show_template_info")?.apply {
//            onPreferenceChangeListener =
//                Preference.OnPreferenceChangeListener { _, _ ->
//                    AlertDialog.Builder(requireContext())
//                        .setTitle(R.string.settings_reports_header)
//                        .setMessage(R.string.settings_templating_info)
//                        .create().show()
//                    false
//                }
//        }
    }
}