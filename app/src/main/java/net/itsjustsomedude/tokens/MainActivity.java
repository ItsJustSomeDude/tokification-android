package net.itsjustsomedude.tokens;

import android.content.Intent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "Main";

	private ActivityMainBinding binding;

	public static final String PREFERENCES = "Prefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!NotificationReader.verifyServiceRunning(this)) {
			this.finish();
			return;
		}

		NotificationHelper notifications = new NotificationHelper(this);

		notifications.createChannels();
		notifications.sendActions();

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		Database db = new Database(this);
		Coop coop = db.fetchSelectedCoop();
		db.close();

		if (coop != null) {
			binding.selectedCoop.setText("Selected Coop: " + coop.name);
		} else {
			binding.selectedCoop.setText("No Coop Selected!");

//			binding.mainRefresh.setEnabled(false);
			binding.mainSend.setEnabled(false);
			binding.CopyReport.setEnabled(false);
			binding.CopyDReport.setEnabled(false);

			//binding.mainEdit.setText("Create Coop");
		}

		binding.mainRefresh.setOnClickListener(view -> {
			try {
				NotificationReader.processNotifications();
				Toast.makeText(this, "This must have worked!", Toast.LENGTH_SHORT).show();
			} catch (Exception err) {
				Log.e(TAG, "Failed to get notifications.", err);
				Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
			}
		});

		binding.mainSend.setOnClickListener(view -> {
			startActivity(new Intent(this, SendTokensActivity.class));
		});

		binding.CopyReport.setOnClickListener(view -> {
			startActivity(new Intent(this, WorkActivity.class));
		});

		// detailed report...

		binding.mainEdit.setOnClickListener(view -> {
			Intent edit = new Intent(this, EditCoopActivity.class);
			if (coop != null)
				edit.putExtra(EditCoopActivity.EDIT_ID, Long.toString(coop.id));
			startActivity(edit);
		});

		// Edit Events...

		binding.mainSwitchCoop.setOnClickListener(view -> {
			startActivity(new Intent(this, ListCoopsActivity.class));
		});

		SharedPreferences sharedPref = getSharedPreferences(
				PREFERENCES,
				Context.MODE_PRIVATE
		);
		String savedName = sharedPref.getString("PlayerName", "");
		binding.mainPlayerName.setText(savedName);
		binding.mainSaveName.setOnClickListener(view -> {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("PlayerName", binding.mainPlayerName.getText().toString());
			editor.apply();

			Toast.makeText(this, "Player Name Saved.", Toast.LENGTH_SHORT).show();
		});

		boolean savedAutoDismiss = sharedPref.getBoolean("AutoDismiss", false);
		binding.mainAutoDismiss.setChecked(savedAutoDismiss);
		binding.mainAutoDismiss.setOnCheckedChangeListener((view, status) -> {
			sharedPref.edit().putBoolean("AutoDismiss", status).apply();
		});

		binding.fakeSend.setOnClickListener(view -> {
			notifications.sendFake(
					binding.fakePlayer.getText().toString(),
					binding.fakeCoop.getText().toString(),
					binding.fakeType.isChecked());
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.binding = null;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] results) {
		super.onRequestPermissionsResult(requestCode, perms, results);
		Toast.makeText(this, "Restart the app to try to resend notifications.", Toast.LENGTH_LONG).show();
	}
}