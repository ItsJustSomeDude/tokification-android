package net.itsjustsomedude.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityEditEventBinding;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {
	private static final String TAG = "EditEvent";

	public static final String PARAM_COOP_ID = "CoopId";
	public static final String PARAM_EVENT_ID = "EventId";

	// TODO: Replace Strings with android.util.DateFormat...
	// will not be able to be Static after this change.
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

	private ActivityEditEventBinding binding;

	private Coop coop;
	private Coop.Event event;
	private Database database;
	private final Calendar openedAt = Calendar.getInstance();

	public static Intent makeCreateIntent(Context ctx, long coopId) {
		return new Intent(ctx, EditEventActivity.class)
				.putExtra(PARAM_COOP_ID, coopId);
	}

	public static Intent makeEditIntent(Context ctx, long coopId, long eventId) {
		return new Intent(ctx, EditEventActivity.class)
				.putExtra(PARAM_COOP_ID, coopId)
				.putExtra(PARAM_EVENT_ID, eventId);
	}

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
		if (b == null) {
			Log.e(TAG, "No idea how this happened.");
			Toast.makeText(this, "Tell Dude: The Send Activity Launched wrongly.", Toast.LENGTH_LONG).show();
			return;
		}

		// boolean refresh = b.getBooleanExtra(PARAM_REFRESH, false);
		long coopId = b.getLongExtra(PARAM_COOP_ID, 0);
		long eventId = b.getLongExtra(PARAM_EVENT_ID, 0);

		Log.i(TAG, "Getting Coop: " + coopId);
		coop = database.fetchCoop(coopId);
		Log.i(TAG, "Got Coop: " + coop);

		String[] people;
		if (coop.sinkMode) {
			people = coop.getPeople("+Other");
		} else {
			people = new String[]{"Sink"};
			binding.personSpinner.setEnabled(false);
		}

		ArrayAdapter<String> personAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				people);
		// Supposed to fix a radio button quirk or something, idk.
		personAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.personSpinner.setAdapter(personAdapter);

		String info = getString(R.string.all_coop_info, coop.name, coop.contract);
		binding.coopInfo.setText(info);

		binding.dateButton.setOnClickListener(view ->
				SimpleDialogs.datePicker(this, event.time, cal -> {
					event.time = cal;
					//render();
					binding.dateButton.setText(
							dateFormat.format(event.time.getTime())
					);
					binding.timeButton.setText(
							timeFormat.format(event.time.getTime())
					);
				})
		);

		binding.timeButton.setOnClickListener(view ->
				SimpleDialogs.timePicker(this, event.time, cal -> {
					event.time = cal;
					//render();

					binding.dateButton.setText(
							dateFormat.format(event.time.getTime())
					);
					binding.timeButton.setText(
							timeFormat.format(event.time.getTime())
					);
				})
		);

		binding.personSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
						if (arg0.getItemAtPosition(position).equals("+Other"))
							binding.nameEntrySection.setVisibility(View.VISIBLE);
						else
							binding.nameEntrySection.setVisibility(View.GONE);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						binding.nameEntrySection.setVisibility(View.GONE);
					}
				}
		);

		binding.countPlus.setOnClickListener(view -> {
			try {
				int count = Integer.parseInt(binding.count.getText().toString());
				binding.count.setText(String.valueOf(count + 1));
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
			}
		});

		binding.countMinus.setOnClickListener(view -> {
			try {
				int count = Integer.parseInt(binding.count.getText().toString());
				binding.count.setText(String.valueOf(count - 1));
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
			}
		});

		if (eventId == 0) {
			// Creating a new event.
			// Hide all irrelevant things.
			binding.directionToggle.setVisibility(View.GONE);
			binding.sectionTime.setVisibility(View.GONE);

			binding.count.setText(String.valueOf(coop.sinkMode ? 6 : 2));
		} else {
			for (Coop.Event ev : coop.events) {
				if (ev.id == eventId) {
					event = ev;
					break;
				}
			}
			if (event == null) {
				Log.e(TAG, "Passed Event ID was not part of the passed Coop!");
				Toast.makeText(this, "Passed Event ID was not part of the passed Coop!", Toast.LENGTH_LONG).show();
				return;
			}

			binding.directionToggle.setVisibility(View.VISIBLE);
			binding.sectionTime.setVisibility(View.VISIBLE);

			binding.personSpinner.setSelection(Arrays.asList(people).indexOf(event.person));
			binding.count.setText(String.valueOf(event.count));

			binding.dateButton.setText(
					dateFormat.format(event.time.getTime())
			);
			binding.timeButton.setText(
					timeFormat.format(event.time.getTime())
			);

			binding.directionToggle.setChecked(event.direction.equals("received"));
		}

		binding.buttonSave.setOnClickListener(v -> {
			int count;
			try {
				count = Integer.parseInt(binding.count.getText().toString());
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
				return;
			}

			String p = (String) binding.personSpinner.getSelectedItem();
			String person = p.equals("+Other")
					? binding.personName.getText().toString()
					: p;

			if (person.isEmpty()) {
				Toast.makeText(this, "Select or enter a person!", Toast.LENGTH_SHORT).show();
				return;
			}

			if (eventId == 0) {
				Coop.Event newEvent = database.createEvent(coop.name, coop.contract, openedAt, count, "received", person);
				coop.addEvent(newEvent);

				Toast.makeText(
						this,
						"Recorded: " + person + " received " + count,
						Toast.LENGTH_SHORT
				).show();
			} else {
				event.count = count;
				event.person = person;
				event.direction = binding.directionToggle.isChecked() ? "received" : "sent";

				database.saveEvent(event);
				Toast.makeText(
						this,
						"Event Saved!",
						Toast.LENGTH_SHORT
				).show();
			}

			if (!coop.sinkMode)
				new NotificationHelper(this).sendActions(coop);
			this.finish();
		});
	}
}
