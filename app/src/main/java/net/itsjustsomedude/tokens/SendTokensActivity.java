package net.itsjustsomedude.tokens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import net.itsjustsomedude.tokens.databinding.ActivitySendTokensBinding;

public class SendTokensActivity extends AppCompatActivity {
	private static final String TAG = "SendTokens";

	public static final String PARAM_COOP = "Coop";
	public static final String PARAM_PLAYER = "Player";
	public static final String PARAM_COUNT = "Count";
	public static final String PARAM_REFRESH = "Refresh";
	public static final String PARAM_COPY_REPORT = "Report";
	public static final String PARAM_NO_SEND = "SkipSend";
	public static final String PARAM_AUTO_SEND = "AutoSend";
	public static final String PARAM_UPDATE_NOTIFICATION = "RefreshNote";

	private ActivitySendTokensBinding binding;

	private Coop coop;
	private Database database;
	private final Calendar openedAt = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle("Send Tokens");

		binding = ActivitySendTokensBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//setSupportActionBar(binding.toolbar);

		database = new Database(this);

		Intent b = getIntent();
		if (b == null) {
			Log.e(TAG, "No idea how this happened.");
			Toast.makeText(this, "Tell Dude: The Send Activity Launched wrongly.", Toast.LENGTH_LONG).show();
			return;
		}

		boolean refresh = b.getBooleanExtra(PARAM_REFRESH, false);
		boolean copyReport = b.getBooleanExtra(PARAM_COPY_REPORT, false);
		boolean noSend = b.getBooleanExtra(PARAM_NO_SEND, false);
		boolean autoSend = b.getBooleanExtra(PARAM_AUTO_SEND, false);
		boolean updateNotification = b.getBooleanExtra(PARAM_UPDATE_NOTIFICATION, false);
		long coopId = b.getLongExtra(PARAM_COOP, 0);
		String defaultPlayer = b.getCharSequenceExtra(PARAM_PLAYER) != null
				? b.getCharSequenceExtra(PARAM_PLAYER).toString()
				: null;
		int defaultCount = b.getIntExtra(PARAM_COUNT, 6);

		if (coopId != 0) {
			coop = database.fetchCoop(coopId);
		} else {
			// No coop, probably from notification, used default.
			coop = database.fetchSelectedCoop();
		}

		Log.i(TAG, coop.toString());

		preDialogActions(refresh);

		if (noSend) {
			postDialogActions(copyReport, updateNotification);

			this.finish();
			return;
		}

		if (autoSend) {
			if (defaultPlayer == null || defaultCount == 0) {
				Toast.makeText(this, "Tell Dude that not all defaults were set!", Toast.LENGTH_LONG).show();
				return;
			}

			Event newEvent = database.createEvent(coop.name, "", openedAt, defaultCount, "received", defaultPlayer);
			coop.addEvent(newEvent);

			Toast.makeText(
					SendTokensActivity.this,
					"Recorded: " + defaultPlayer + " received " + defaultCount,
					Toast.LENGTH_SHORT
			).show();

			postDialogActions(copyReport, updateNotification);
			this.finish();
			return;
		}

		binding.test.setText(coop.name + ", " + coop.id);

		String[] people;
		if (defaultPlayer == null) {
			people = coop.getPeople("+Other");
		} else {
			people = new String[]{defaultPlayer};
			binding.personSpinner.setEnabled(false);
			binding.personName.setEnabled(false);
		}

		ArrayAdapter<String> personAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				people);
		// Supposed to fix a radio button quirk or something, idk.
		personAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		binding.personSpinner.setAdapter(personAdapter);

		String[] numberOptions = new String[10];
		for (int i = 0; i < 10; i++)
			numberOptions[i] = i + 1 + "";
		ArrayAdapter<String> numAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				numberOptions);
		// Supposed to fix a radio button quirk or something, idk.
		numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		binding.sendCountDropdown.setAdapter(numAdapter);
		binding.sendCountDropdown.setSelection(defaultCount == 0 ? 6 - 1 : defaultCount - 1);

		binding.sendButton.setOnClickListener(v -> {
			int count = binding.sendCountDropdown.getSelectedItemPosition() + 1;
			String person = (String) binding.personSpinner.getSelectedItem();

			if (person.equals("+Other")) {
				person = binding.personName.getText().toString();
			}

			if (person.isEmpty()) {
				Toast.makeText(this, "Select or enter a person!", Toast.LENGTH_SHORT).show();
				return;
			}

			Event newEvent = database.createEvent(coop.name, "", openedAt, count, "received", person);
			coop.addEvent(newEvent);

			Toast.makeText(
					SendTokensActivity.this,
					"Recorded: " + person + " received " + count,
					Toast.LENGTH_SHORT
			).show();

			postDialogActions(copyReport, updateNotification);
			this.finish();
		});
	}

	private void preDialogActions(boolean refresh) {
		if (refresh) {
			try {
				NotificationReader.processNotifications();
				Toast.makeText(this, "Refreshed Notifications.", Toast.LENGTH_SHORT).show();
			} catch (Exception err) {
				Log.e(TAG, "Failed to get notifications.", err);
				Toast.makeText(this, "Failed to refresh!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void postDialogActions(boolean copyReport, boolean updateNote) {
		if (copyReport) {
			SharedPreferences sharedPref = getSharedPreferences(
					MainActivity.PREFERENCES,
					Context.MODE_PRIVATE
			);
			String savedName = sharedPref.getString("PlayerName", "Sink");

			String report = new ReportBuilder(coop, savedName).sinkReport();

			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("SinkReport", report);
			clipboard.setPrimaryClip(clip);
		}

		if (updateNote) {
			NotificationHelper nh = new NotificationHelper(this);

			if (coop.sinkMode)
				nh.sendSinkActions();
			else {
				String report = new ReportBuilder(coop, "You").normalReport();
				nh.sendNormalActions(report);
			}
		}
	}

	public static void copyReport(Context ctx, long coopId, boolean refresh) {
		Intent intent = new Intent(ctx, SendTokensActivity.class);
		intent.putExtra(PARAM_NO_SEND, true);
		intent.putExtra(PARAM_COPY_REPORT, true);

		if (refresh) intent.putExtra(PARAM_REFRESH, true);

		if (coopId != 0) intent.putExtra(PARAM_COOP, coopId);

		ctx.startActivity(intent);
	}

	public static void refreshNotes(Context ctx) {
		Intent intent = new Intent(ctx, SendTokensActivity.class);

		intent.putExtra(PARAM_NO_SEND, true);
		intent.putExtra(PARAM_REFRESH, true);

		ctx.startActivity(intent);
	}

	public static void sendTokens(Context ctx) {
		Intent intent = new Intent(ctx, SendTokensActivity.class);
		ctx.startActivity(intent);
	}

	public static void sinkTokens(Context ctx, int count) {
		Intent intent = new Intent(ctx, SendTokensActivity.class);
		intent.putExtra(PARAM_PLAYER, "Sink");
		intent.putExtra(PARAM_COUNT, count);
		ctx.startActivity(intent);
	}
}
