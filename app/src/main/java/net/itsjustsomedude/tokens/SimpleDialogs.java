package net.itsjustsomedude.tokens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Consumer;

import java.util.Calendar;
import java.util.function.BiConsumer;

public class SimpleDialogs {
	public static void datePicker(Context ctx, Calendar initialDate, Consumer<Calendar> callback) {
		// If initialDate is null, set it to the current date
		Calendar startingDate = initialDate != null
				? (Calendar) initialDate.clone()
				: Calendar.getInstance();

		// Create a DatePickerDialog with the selectedDate
		DatePickerDialog datePickerDialog = new DatePickerDialog(
				ctx,
				// Listener for date selection
				(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
					// Update the selectedDate with the chosen date
					startingDate.set(Calendar.YEAR, selectedYear);
					startingDate.set(Calendar.MONTH, selectedMonth);
					startingDate.set(Calendar.DAY_OF_MONTH, selectedDay);

					callback.accept(startingDate);
				},
				startingDate.get(Calendar.YEAR),
				startingDate.get(Calendar.MONTH),
				startingDate.get(Calendar.DAY_OF_MONTH)
		);
		datePickerDialog.show();
	}

	public static void timePicker(Context ctx, Calendar initialDate, Consumer<Calendar> callback) {
		// If initialDate is null, set it to the current date
		Calendar startingDate = initialDate != null
				? (Calendar) initialDate.clone()
				: Calendar.getInstance();

		// Create a DatePickerDialog with the selectedDate
		TimePickerDialog timePickerDialog = new TimePickerDialog(
				ctx,
				// Listener for date selection
				(TimePicker view, int selectedHour, int selectedMinute) -> {
					// Update the selectedDate with the chosen date
					startingDate.set(Calendar.HOUR_OF_DAY, selectedHour);
					startingDate.set(Calendar.MINUTE, selectedMinute);

					callback.accept(startingDate);
				},
				startingDate.get(Calendar.HOUR_OF_DAY),
				startingDate.get(Calendar.MINUTE),
				DateFormat.is24HourFormat(ctx)
		);
		timePickerDialog.show();
	}

	public static void textPicker(Context ctx, CharSequence title, CharSequence initialText, Consumer<String> callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);

		// Set up the input
		final EditText input = new EditText(ctx);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setText(initialText);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", (dialog, which) -> {
			String finalText = input.getText().toString();
			callback.accept(finalText);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

		builder.show();
	}

	public static void yesNoPicker(
			Context ctx,
			CharSequence title,
			CharSequence message,
			CharSequence positiveText,
			Consumer<DialogInterface> onPositive,
			CharSequence negativeText,
			Consumer<DialogInterface> onNegative,
			CharSequence neutralText,
			Consumer<DialogInterface> onNeutral
	) {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(positiveText, (dialog, which) -> {
			onPositive.accept(dialog);
		});
		builder.setNegativeButton(negativeText, (dialog, which) -> {
			onNegative.accept(dialog);
		});
		builder.setNeutralButton(neutralText, (dialog, which) -> {
			onNeutral.accept(dialog);
		});
		builder.create().show();
	}

	public static ActivityResultLauncher<Intent> registerActivityCallback(ComponentActivity ctx, Consumer<ActivityResult> callback) {
		return ctx.registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(),
				callback::accept
		);
	}

}
