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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: Maybe move all this to its own service?
		NotificationHelper notifications = new NotificationHelper(this);
		notifications.createChannels();
		notifications.ensurePermissions();

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		activityCallback = SimpleDialogs.registerActivityCallback(this, result -> {
			render();
		});

		render();

		binding.fakeSend.setOnClickListener(view -> {
			notifications.sendFake(
					binding.fakePlayer.getText().toString(),
					binding.fakeCoop.getText().toString(),
					binding.fakeContract.getText().toString(),
					binding.fakeType.isChecked());
		});

//		binding.toggleServiceButton.setOnClickListener(view -> {
//			if (NotificationReader.isServiceRunning()) {
//				NotificationReader.setServiceEnabled(this, false);
//				binding.toggleServiceButton.setText(R.string.button_start_service);
//			} else {
//				NotificationReader.setServiceEnabled(this, true);
//				binding.toggleServiceButton.setText(R.string.button_stop_service);
//			}
//		});
	}

	private void render() {
		if (this.binding == null) {
			// Probably never actually fully started.
			return;
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		CoopInfoFragment fragment = (CoopInfoFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);

		if (fragment == null) {
			long selectedCoop = Coop.getSelectedCoop(this);
			CoopInfoFragment newFragment = CoopInfoFragment.newInstance(selectedCoop);

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.fragmentContainerView, newFragment);
			fragmentTransaction.commit();
		} else {
			fragment.refresh();
			fragment.render();
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getBoolean("enable_notification_debugger", false)) {
			binding.notificationDebuggerSection.setVisibility(View.VISIBLE);
		} else {
			binding.notificationDebuggerSection.setVisibility(View.GONE);
		}

//		if (NotificationReader.isServiceRunning()) {
//			binding.toggleServiceButton.setText(R.string.button_stop_service);
//		} else {
//			binding.toggleServiceButton.setText(R.string.button_start_service);
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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
			if (NotificationReader.isServiceRunning()) {
				NotificationReader.processAllNotifications();
				render();
			} else {
				SimpleDialogs.yesNoPicker(
						this,
						"Start Service?",
						"Service must be started to read notifications.",
						"Yes", (a) -> {
							NotificationReader.setServiceEnabled(this, true);
							NotificationReader.processAllNotifications();
						}, "No", (a) -> {
						}, "", (a) -> {
						});
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