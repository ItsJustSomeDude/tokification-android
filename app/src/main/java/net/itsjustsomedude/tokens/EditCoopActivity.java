package net.itsjustsomedude.tokens;

import android.app.AlertDialog;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.itsjustsomedude.tokens.databinding.ActivityEditCoopBinding;

public class EditCoopActivity extends AppCompatActivity {
	private static final String TAG = "EditCoop";

	public static final String EDIT_ID = "id";
	public static final String PARAM_NEW = "New";

	private ActivityEditCoopBinding binding;

	public Coop coop = null;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityEditCoopBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		setTitle("Edit Co-op");

		binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
		binding.toolbar.setNavigationOnClickListener(v -> {
			startActivity(new Intent(this, ListCoopsActivity.class));
		});

		Database db = new Database(this);

		Intent thisIntent = getIntent();
		if (thisIntent.getBooleanExtra(PARAM_NEW, false)) {
			Log.i(TAG, "Creating new coop.");
			coop = db.createCoop();
		}
		
		String i = thisIntent.getStringExtra(EDIT_ID);
		if (i != null) {
			long id = Long.parseLong(i);
			coop = db.fetchCoop(id);
		}

		if (coop == null) {
			Log.i(TAG, "Grabbing Selected Coop.");
			coop = db.fetchSelectedCoop();
		}
		
		if (coop == null) {
			Log.i(TAG, "Creating new coop... 2");
			coop = db.createCoop();
		}

		binding.editCoopCode.setText(coop.name);
		binding.editCoopMode.setChecked(coop.sinkMode);

		setButtonTexts();

		binding.editCoopStartDateButton.setOnClickListener(v -> {
			int y;
			int m;
			int d;
			if (coop.startTime == null) {
				Calendar now = Calendar.getInstance();
				y = now.get(Calendar.YEAR);
				m = now.get(Calendar.MONTH);
				d = now.get(Calendar.DAY_OF_MONTH);
			} else {
				y = coop.startTime.get(Calendar.YEAR);
				m = coop.startTime.get(Calendar.MONTH);
				d = coop.startTime.get(Calendar.DAY_OF_MONTH);
			}

			DatePickerDialog dialog = new DatePickerDialog(
					v.getContext(),
					(DatePicker view, int year, int month, int day) -> {
						if (coop.startTime == null) {
							coop.startTime = Calendar.getInstance();
						}
						coop.startTime.set(Calendar.YEAR, year);
						coop.startTime.set(Calendar.MONTH, month);
						coop.startTime.set(Calendar.DAY_OF_MONTH, day);
						setButtonTexts();
					},
					y, m, d
			);
			dialog.show();
		});

		binding.editCoopStartTimeButton.setOnClickListener(v -> {
			int h;
			int m;
			if (coop.startTime == null) {
				Calendar now = Calendar.getInstance();
				h = now.get(Calendar.HOUR_OF_DAY);
				m = now.get(Calendar.MINUTE);
			} else {
				h = coop.startTime.get(Calendar.HOUR_OF_DAY);
				m = coop.startTime.get(Calendar.MINUTE);
			}

			TimePickerDialog dialog = new TimePickerDialog(
					v.getContext(),
					(TimePicker view, int hour, int minute) -> {
						if (coop.startTime == null) {
							coop.startTime = Calendar.getInstance();
						}
						coop.startTime.set(Calendar.HOUR_OF_DAY, hour);
						coop.startTime.set(Calendar.MINUTE, minute);
						setButtonTexts();
					},
					h, m,
					DateFormat.is24HourFormat(v.getContext())
			);
			dialog.show();
		});

		binding.editCoopEndDateButton.setOnClickListener(v -> {
			int y;
			int m;
			int d;
			if (coop.endTime == null) {
				Calendar now = Calendar.getInstance();
				y = now.get(Calendar.YEAR);
				m = now.get(Calendar.MONTH);
				d = now.get(Calendar.DAY_OF_MONTH);
			} else {
				y = coop.endTime.get(Calendar.YEAR);
				m = coop.endTime.get(Calendar.MONTH);
				d = coop.endTime.get(Calendar.DAY_OF_MONTH);
			}

			DatePickerDialog dialog = new DatePickerDialog(
					v.getContext(),
					(DatePicker view, int year, int month, int day) -> {
						if (coop.endTime == null) {
							coop.endTime = Calendar.getInstance();
						}
						coop.endTime.set(Calendar.YEAR, year);
						coop.endTime.set(Calendar.MONTH, month);
						coop.endTime.set(Calendar.DAY_OF_MONTH, day);
						setButtonTexts();
					},
					y, m, d
			);
			dialog.show();
		});

		binding.editCoopEndTimeButton.setOnClickListener(v -> {
			int h;
			int m;
			if (coop.endTime == null) {
				Calendar now = Calendar.getInstance();
				h = now.get(Calendar.HOUR_OF_DAY);
				m = now.get(Calendar.MINUTE);
			} else {
				h = coop.endTime.get(Calendar.HOUR_OF_DAY);
				m = coop.endTime.get(Calendar.MINUTE);
			}

			TimePickerDialog dialog = new TimePickerDialog(
					v.getContext(),
					(TimePicker view, int hour, int minute) -> {
						if (coop.endTime == null) {
							coop.endTime = Calendar.getInstance();
						}
						coop.endTime.set(Calendar.HOUR_OF_DAY, hour);
						coop.endTime.set(Calendar.MINUTE, minute);
						setButtonTexts();
					},
					h, m,
					DateFormat.is24HourFormat(v.getContext())
			);
			dialog.show();
		});

		binding.editCoopSave.setOnClickListener(v -> {
			coop.name = binding.editCoopCode.getText().toString();
			coop.sinkMode = binding.editCoopMode.isChecked();
			coop.modified = true;
			Database d = new Database(this);
			d.saveCoop(coop);
			d.close();
			Toast.makeText(this, "Saved Coop!", Toast.LENGTH_SHORT).show();
			//returnHome();
		});

		binding.editCoopSetActive.setOnClickListener(v -> {
			if (coop.id != 0) {
				Coop.setSelectedCoop(this, coop.id);
				Toast.makeText(this, "Coop set as active.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Save the Co-op first.", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void setButtonTexts() {
		if (coop.startTime == null) {
			binding.editCoopStartDateButton.setText("Set Start Date");
			binding.editCoopStartTimeButton.setText("Set Start Time");
		} else {
			binding.editCoopStartDateButton.setText(
					"Start Date: " +
							dateFormat.format(coop.startTime.getTime())
			);
			binding.editCoopStartTimeButton.setText(
					"Start Time: " +
							timeFormat.format(coop.startTime.getTime())
			);
		}

		if (coop.endTime == null) {
			binding.editCoopEndDateButton.setText("Set End Date");
			binding.editCoopEndTimeButton.setText("Set End Time");
		} else {
			binding.editCoopEndDateButton.setText(
					"End Date: " +
							dateFormat.format(coop.endTime.getTime())
			);
			binding.editCoopEndTimeButton.setText(
					"End Time: " +
							timeFormat.format(coop.endTime.getTime())
			);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.coop_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.delete_coop) {
			if (coop.id != 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Delete Coop?");
				builder.setMessage("Are you sure you want to delete this coop?");
				builder.setPositiveButton("Yes", (dialog, which) -> {
					Database db = new Database(this);
				    db.deleteCoop(coop.id);
				    db.close();

				    Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show();
				    returnHome();
				});
				builder.setNegativeButton("No", (dialog, which) -> {
						
				});
				builder.setNeutralButton("Delete Keeping Events", (dialog, which) -> {
					Database db = new Database(this);
				    db.deleteCoop(coop.id);
				    db.close();

				    Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show();
				    returnHome();
				});
				builder.create().show();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void returnHome() {
		Intent home_intent = new Intent(getApplicationContext(), MainActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(home_intent);
	}
}
