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
	public static final String PARAM_SKIP_REFRESH = "Refresh";
	public static final String PARAM_COPY_REPORT = "Report";
	public static final String PARAM_SKIP_SEND = "SkipSend";

	private ActivitySendTokensBinding binding;

	private Coop coop;
	private final Calendar openedAt = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle("Send Tokens");

		binding = ActivitySendTokensBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//setSupportActionBar(binding.toolbar);

		Bundle b = getIntent().getExtras();

		if (b != null && !b.getBoolean(PARAM_SKIP_REFRESH, false)) {
			try {
				NotificationReader.processNotifications();
				Toast.makeText(this, "Refreshed Notifications.", Toast.LENGTH_SHORT).show();
			} catch (Exception err) {
				Log.e(TAG, "Failed to get notifications.", err);
				Toast.makeText(this, "Failed to refresh!", Toast.LENGTH_SHORT).show();
			}
		}

		Database db = new Database(this);
		if (b != null && b.getLong(PARAM_COOP) != 0) {
			coop = db.fetchCoop(b.getLong(PARAM_COOP));
		} else {
			// No coop, probably from notification, used default.
			coop = db.fetchSelectedCoop();
		}
        db.close();
		
		if (b != null && b.getBoolean(PARAM_SKIP_SEND, false)) {
			if (b.getBoolean(PARAM_COPY_REPORT, false)) {
				copyReport(coop);
			}
			
			this.finish();
			return;
		}
		
		String defaultPlayer = null;
		int defaultCount = 0;
		if (b != null) {
			if (b.getString(PARAM_PLAYER) != null)
				defaultPlayer = b.getString(PARAM_PLAYER);

			if (b.getInt(PARAM_COUNT) != 0)
				defaultCount = b.getInt(PARAM_COUNT);
		}

		binding.test.setText(coop.name + ", " + coop.id);

		String[] people;
		if (defaultPlayer == null) {
			people = coop.getPeople("+Other");
		} else {
			people = new String[]{defaultPlayer};
			//binding.personSpinner.setVisibility(View.GONE);
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

			Database db2 = new Database(this);
			Event newEvent = db2.createEvent(coop.name, "", openedAt, count, "received", person);
			coop.addEvent(newEvent);
			db2.close();

			Toast.makeText(
					SendTokensActivity.this,
					"Recorded: " + person + " received " + count,
					Toast.LENGTH_SHORT
			).show();
				
			if (b != null && b.getBoolean(PARAM_COPY_REPORT, false)) {
				copyReport(coop);
			}

			this.finish();
		});
	}
	
	private void copyReport(Coop coop) {
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
	
	public static void copyReport(Context ctx, boolean refresh) {
		Intent intent = new Intent(ctx, SendTokensActivity.class);
		intent.putExtra(PARAM_SKIP_SEND, true);
		intent.putExtra(PARAM_COPY_REPORT, true);
		
		if (!refresh) intent.putExtra(PARAM_SKIP_REFRESH, true);
		
		ctx.startActivity(intent);
	}
	
	public static void refreshNotes(Context ctx) {
		Intent intent = new Intent(ctx, SendTokensActivity.class);
		
		intent.putExtra(PARAM_SKIP_SEND, true);
		
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
