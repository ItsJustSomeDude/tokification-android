package net.itsjustsomedude.tokens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class CopyReportActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Coop toReport = Coop.fetchSelectedCoop(this);
		assert toReport != null;
				
		String report = new ReportBuilder(toReport, "ItsJustSomeDude").sinkReport();

		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("SinkReport", report);
		clipboard.setPrimaryClip(clip);
		Log.i("Report", report);
		
		this.finish();
	}
}
