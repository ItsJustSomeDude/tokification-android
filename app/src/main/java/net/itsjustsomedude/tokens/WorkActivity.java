package net.itsjustsomedude.tokens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class WorkActivity extends AppCompatActivity {
	private static final String TAG = "Work";

	public static final String PARAM_REFRESH = "RefreshNotes";
	public static final String PARAM_COPY_REPORT = "CopyReport";
	public static final String PARAM_SEND = "Send";
	public static final String PARAM_SINK_1 = "Sink1";
	public static final String PARAM_SINK_2 = "Sink2";
	public static final String PARAM_SINK_MENU = "SinkMenu";

	Database db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent thisIntent = getIntent();
		boolean shouldRefresh = thisIntent.getBooleanExtra(PARAM_REFRESH, false);
		boolean shouldSend = thisIntent.getBooleanExtra(PARAM_SEND, false);
		boolean shouldSink1 = thisIntent.getBooleanExtra(PARAM_SINK_1, false);
		boolean shouldSink2 = thisIntent.getBooleanExtra(PARAM_SINK_2, false);
		boolean shouldSinkMenu = thisIntent.getBooleanExtra(PARAM_SINK_MENU, false);
		boolean shouldCopyReport = thisIntent.getBooleanExtra(PARAM_COPY_REPORT, false);

		if (shouldRefresh)
			refreshNotes();

		ActivityResultLauncher<Intent> returnHandler = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(),
				result -> {
					if (shouldCopyReport) copyReport();
					this.finish();
				}
		);

		if (shouldSend)
			returnHandler.launch(new Intent(this, SendTokensActivity.class));
		else if (shouldSink2)
			returnHandler.launch(
					new Intent(this, SendTokensActivity.class)
							.putExtra(SendTokensActivity.PARAM_PLAYER, "Sink")
							.putExtra(SendTokensActivity.PARAM_COUNT, 2)
			);
		else if (shouldSink1)
			returnHandler.launch(
					new Intent(this, SendTokensActivity.class)
							.putExtra(SendTokensActivity.PARAM_PLAYER, "Sink")
							.putExtra(SendTokensActivity.PARAM_COUNT, 1)
			);
		else if (shouldSinkMenu)
			returnHandler.launch(
					new Intent(this, SendTokensActivity.class)
							.putExtra(SendTokensActivity.PARAM_PLAYER, "Sink")
							.putExtra(SendTokensActivity.PARAM_COUNT, 2)
			);

		// If something is supposed to happen after that activity, bail out.
		// The "after work" will be done in the returnHandler callback.
		if (shouldSend || shouldSink2 || shouldSink1 || shouldSinkMenu) return;

		if (shouldCopyReport)
			copyReport();
		
		this.finish();
	}

	private void refreshNotes() {
		try {
			NotificationReader.processNotifications();
			Toast.makeText(this, "This must have worked!", Toast.LENGTH_SHORT).show();
		} catch (Exception err) {
			Log.e(TAG, "Failed to get notifications.", err);
			Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
		}
	}

	private void copyReport() {
		Database db = new Database(this);
		Coop toReport = db.fetchSelectedCoop();
		assert toReport != null;

		SharedPreferences sharedPref = getSharedPreferences(
				MainActivity.PREFERENCES,
				Context.MODE_PRIVATE
		);
		String savedName = sharedPref.getString("PlayerName", "Me :-)");

		String report = new ReportBuilder(toReport, savedName).sinkReport();

		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("SinkReport", report);
		clipboard.setPrimaryClip(clip);
	}


}
