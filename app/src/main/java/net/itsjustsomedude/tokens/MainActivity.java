package net.itsjustsomedude.tokens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;

	public Coop coop;

	public static final String PREFERENCES = "Prefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Notifications.createChannels(this);
		Notifications.sendActions(this);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		coop = Coop.fetchSelectedCoop(this);

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
				Log.e("", "Failed to get notifications.", err);
				Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
			}
		});

		binding.mainSend.setOnClickListener(view -> {
			startActivity(new Intent(this, SendTokensActivity.class));
		});

		binding.CopyReport.setOnClickListener(view -> {
			Coop toReport = Coop.fetchSelectedCoop(this);

			assert toReport != null;
			String report = Reports.sinkReport(toReport);

			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("SinkReport", report);
			clipboard.setPrimaryClip(clip);
			Log.i("Report", report);
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
			Notifications.sendFake(
					this,
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
}