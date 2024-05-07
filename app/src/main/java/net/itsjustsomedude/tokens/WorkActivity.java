package net.itsjustsomedude.tokens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class WorkActivity extends AppCompatActivity {
	private static final String TAG = "Work";
	
	public static final String PARAM_REFRESH = "RefreshNotes";
	public static final String PARAM_SEND_MENU = "SendMenu";
	public static final String PARAM_COPY_REPORT = "CopyReport";
	public static final String PARAM_SEND_6 = "Send6";
	public static final String PARAM_SINK_1 = "Sink1";
	public static final String PARAM_SINK_2 = "Sink2";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setTheme(R.style.)
		
		Intent thisIntent = getIntent();
		boolean shouldRefresh = thisIntent.getBooleanExtra(PARAM_REFRESH, false);
		boolean shouldSendMenu = thisIntent.getBooleanExtra(PARAM_SEND_MENU, false);

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
		Log.i("Report", report);

		this.finish();
	}
	
	private void refreshNotes() {
	}
	
	private void sendMenu() {
	}
	
	
}
