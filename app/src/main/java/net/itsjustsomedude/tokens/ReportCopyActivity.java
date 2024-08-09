package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.db.Coop;

public class ReportCopyActivity extends AppCompatActivity {
	public static final String PARAM_COOP_ID = "CoopId";

	// TODO: Scrap this entire thing, replace with a service.
	// TODO: Hmm... maybe broadcast receivers would be better in multiple ways...

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent == null) {
			Toast.makeText(this, "Tell Dude: The report activity had no intent!", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}

		Database db = new Database(this);
		long coopId = intent.getLongExtra(PARAM_COOP_ID, 0);
		Coop coop = db.fetchCoop(coopId);

		if (coop.sinkMode) {
			String report = ReportBuilder.makeBuilder(this, coop).sinkReport();
			ReportBuilder.copyText(this, report);
		} else {
			new NotificationHelper(this).sendActions(coop);
		}

		this.finish();
	}
}
