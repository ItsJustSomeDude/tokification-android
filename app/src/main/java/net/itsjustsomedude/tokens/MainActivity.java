package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.preference.PreferenceManager;

import net.itsjustsomedude.tokens.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "Main";
	private ActivityMainBinding binding;
	public static final String PREFERENCES = "Prefs";
	private ActivityResultLauncher<Intent> activityCallback;

	private boolean isServiceRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NotificationHelper notifications = new NotificationHelper(this);

		notifications.createChannels();
		notifications.ensurePermissions();

		if (!NotificationReader.verifyServiceRunning(this)) {
			Toast.makeText(this, "Service not Running! Start it to process notifications.", Toast.LENGTH_SHORT).show();
			isServiceRunning = false;
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		activityCallback = SimpleDialogs.registerActivityCallback(this, result -> {
			render();
		});

		long selectedCoop = Coop.getSelectedCoop(this);
		CoopInfoFragment fragment = CoopInfoFragment.newInstance(selectedCoop);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = manager.beginTransaction();
		fragmentTransaction.add(R.id.fragmentContainerView, fragment);
		fragmentTransaction.commit();

		if (preferences.getBoolean("enable_notification_debugger", false)) {
			binding.notificationDebuggerSection.setVisibility(View.VISIBLE);
		} else {
			binding.notificationDebuggerSection.setVisibility(View.GONE);
		}

		if (preferences.getBoolean("allow_service_stopping", false)) {
			binding.stopServiceButton.setVisibility(View.VISIBLE);
		} else {
			binding.stopServiceButton.setVisibility(View.GONE);
		}

		binding.fakeSend.setOnClickListener(view -> {
			notifications.sendFake(
					binding.fakePlayer.getText().toString(),
					binding.fakeCoop.getText().toString(),
					binding.fakeContract.getText().toString(),
					binding.fakeType.isChecked());
		});

		binding.stopServiceButton.setOnClickListener(view -> {
			NotificationReader.stopService();
			isServiceRunning = false;
		});
	}

	private void render() {
		if (this.binding == null) {
			// Probably never actually fully started.
			return;
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		CoopInfoFragment fragment = (CoopInfoFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
		if (fragment != null) {
			fragment.refresh();
			fragment.render();
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getBoolean("enable_notification_debugger", false)) {
			binding.notificationDebuggerSection.setVisibility(View.VISIBLE);
		} else {
			binding.notificationDebuggerSection.setVisibility(View.GONE);
		}

		if (preferences.getBoolean("allow_service_stopping", false)) {
			binding.stopServiceButton.setVisibility(View.VISIBLE);
		} else {
			binding.stopServiceButton.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		render();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.main_refresh) {
			if (isServiceRunning && false) {
				NotificationReader.processNotifications();
				render();
			} else {
				Toast.makeText(this, "Service is not running!", Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.main_settings) {
			activityCallback.launch(new Intent(this, SettingsActivity.class));
		} else if (id == R.id.main_select_coop) {
			activityCallback.launch(new Intent(this, ListCoopsActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] results) {
		super.onRequestPermissionsResult(requestCode, perms, results);
		Toast.makeText(this, "Restart the app to try to resend notifications.", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.binding = null;
	}
}