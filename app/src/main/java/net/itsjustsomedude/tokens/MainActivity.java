package net.itsjustsomedude.tokens;

import android.content.Intent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "Main";

	private ActivityMainBinding binding;

	public static final String PREFERENCES = "Prefs";

	Coop coop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!NotificationReader.verifyServiceRunning(this)) {
			this.finish();
			return;
		}

		NotificationHelper notifications = new NotificationHelper(this);

		notifications.createChannels();
		notifications.sendSinkActions();

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		Database db = new Database(this);
		coop = db.fetchSelectedCoop();
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

		binding.mainSend.setOnClickListener(view -> {
			if (coop.sinkMode)
				SendTokensActivity.sendTokens(this);
			else
				SendTokensActivity.sinkTokens(this, 2);
		});

		binding.CopyReport.setOnClickListener(view -> {
			SendTokensActivity.copyReport(this, true);
		});

		// detailed report...

		ActivityResultLauncher<Intent> returnHandler = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(),
				result -> {
					//if (result.getResultCode() != Activity.RESULT_OK) return;

					Database db2 = new Database(this);
					coop = db2.fetchSelectedCoop();
					if (coop != null) {
						binding.selectedCoop.setText("Selected Coop: " + coop.name);
					} else {
						binding.selectedCoop.setText("No Coop Selected!");

						binding.mainSend.setEnabled(false);
						binding.CopyReport.setEnabled(false);
						binding.CopyDReport.setEnabled(false);

						binding.mainEdit.setEnabled(false);
					}
				}
		);

		binding.mainEdit.setOnClickListener(view -> {
			Intent edit = new Intent(this, EditCoopActivity.class);
			if (coop != null)
				edit.putExtra(EditCoopActivity.EDIT_ID, Long.toString(coop.id));
			returnHandler.launch(edit);
		});

		// Edit Events...

		binding.mainSwitchCoop.setOnClickListener(view -> {
			returnHandler.launch(new Intent(this, ListCoopsActivity.class));
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mainRefresh) {
			SendTokensActivity.refreshNotes(this);
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