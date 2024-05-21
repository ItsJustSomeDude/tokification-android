package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.itsjustsomedude.tokens.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "Main";
	private ActivityMainBinding binding;
	public static final String PREFERENCES = "Prefs";
	private ActivityResultLauncher<Intent> activityCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NotificationHelper notifications = new NotificationHelper(this);

		notifications.createChannels();
		notifications.ensurePermissions();

		if (!NotificationReader.verifyServiceRunning(this)) {
			this.finish();
			return;
		}

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		activityCallback = SimpleDialogs.registerActivityCallback(this, result -> {
			refreshCoop();
		});

		renderCoop();

		binding.fakeSend.setOnClickListener(view -> {
			notifications.sendFake(
					binding.fakePlayer.getText().toString(),
					binding.fakeCoop.getText().toString(),
					binding.fakeContract.getText().toString(),
					binding.fakeType.isChecked());
		});
	}

	private void renderCoop() {
		long selectedCoop = Coop.getSelectedCoop(this);

		CoopInfoFragment fragment = CoopInfoFragment.newInstance(selectedCoop);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = manager.beginTransaction();
		fragmentTransaction.add(R.id.fragmentContainerView, fragment);
		fragmentTransaction.commit();
	}

	private void refreshCoop() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		CoopInfoFragment fragment = (CoopInfoFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
		if (fragment != null) {
			fragment.refresh();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.main_refresh) {
			NotificationReader.processNotifications();
			refreshCoop();
		} else if (id == R.id.main_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
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