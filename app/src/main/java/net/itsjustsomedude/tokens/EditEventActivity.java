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

import net.itsjustsomedude.tokens.databinding.ActivityEditEventBinding;

public class EditEventActivity extends AppCompatActivity {
	private static final String TAG = "Event";

	public static final String PARAM_COOP_ID = "CoopId";
	public static final String PARAM_EVENT_ID = "EventId";
	public static final String PARAM_COUNT = "Count";
	public static final String PARAM_REFRESH = "Refresh";
	public static final String PARAM_AUTO_SEND = "AutoSend";
	public static final String PARAM_UPDATE_NOTIFICATION = "RefreshNote";

	private ActivityEditEventBinding binding;

	private Coop coop;
	private Database database;
	private final Calendar openedAt = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setTitle("Send Tokens");

		binding = ActivityEditEventBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//setSupportActionBar(binding.toolbar);

		database = new Database(this);

		Intent b = getIntent();
//		if (b == null) {
//			Log.e(TAG, "No idea how this happened.");
//			Toast.makeText(this, "Tell Dude: The Send Activity Launched wrongly.", Toast.LENGTH_LONG).show();
//			return;
//		}

		boolean refresh = b.getBooleanExtra(PARAM_REFRESH, false);
		boolean autoSend = b.getBooleanExtra(PARAM_AUTO_SEND, false);
		long coopId = b.getLongExtra(PARAM_COOP_ID, 0);
		long eventId = b.getLongExtra(PARAM_EVENT_ID, 0);
		int defaultCount = b.getIntExtra(PARAM_COUNT, 6);

		if (refresh) {
			try {
				NotificationReader.processNotifications();
				Toast.makeText(this, "Refreshed Notifications.", Toast.LENGTH_SHORT).show();
			} catch (Exception err) {
				Log.e(TAG, "Failed to get notifications.", err);
				Toast.makeText(this, "Failed to refresh!", Toast.LENGTH_SHORT).show();
			}
		}

		coop = database.fetchCoop(coopId);
		
		if (eventId == 0) {
			// Hide all irrelevant things.
		}
		
		if (autoSend) {
			if (coop.sinkMode) {
				Toast.makeText(this, "Auto send doesn't work in Sink Mode.", Toast.LENGTH_LONG).show();
			} else {
				if (defaultCount == 0) {
					Toast.makeText(this, "Tell Dude that not all defaults were set!", Toast.LENGTH_LONG).show();
					return;
				}

				Event newEvent = database.createEvent(coop.name, coop.contract, openedAt, defaultCount, "received", "Sink");
				coop.addEvent(newEvent);

				Toast.makeText(
						this,
						"Recorded: Sink received " + defaultCount,
						Toast.LENGTH_SHORT
				).show();

				updateNote();
				this.finish();
				return;
			}
		}

		binding.coopInfo.setText(coop.name + ", " + coop.id);

		String[] people;
		if (coop.sinkMode) {
			people = coop.getPeople("+Other");
		} else {
			people = new String[]{"Sink"};
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
		binding.sendCountDropdown.setSelection(defaultCount - 1);

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
					this,
					"Recorded: " + person + " received " + count,
					Toast.LENGTH_SHORT
			).show();

			if (!coop.sinkMode) updateNote();
			this.finish();
		});
	}

	private void updateNote() {
		new NotificationHelper(this).sendActions(coop);
	}

	public static void sendTokens(Context ctx, long coopId) {
		Intent intent = new Intent(ctx, EditEventActivity.class);
		intent.putExtra(PARAM_COOP_ID, coopId);
		ctx.startActivity(intent);
	}

	public static void sendTokens(Context ctx, long coopId, int count) {
		Intent intent = new Intent(ctx, EditEventActivity.class);
		intent.putExtra(PARAM_COOP_ID, coopId);
		intent.putExtra(PARAM_COUNT, count);
		ctx.startActivity(intent);
	}
}
