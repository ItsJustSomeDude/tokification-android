package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.db.Coop;
import net.itsjustsomedude.tokens.db.CoopRepository;

import java.util.Objects;

public class ReportCopyActivity extends AppCompatActivity {
	public static final String PARAM_COOP_ID = "CoopId";

	// TODO: Scrap this entire thing, replace with a service.
	// TODO: Hmm... maybe broadcast receivers would be better in multiple ways...

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		CoopRepository coopRepo = new CoopRepository(getApplication());

		int coopId = intent.getIntExtra(PARAM_COOP_ID, 0);
		Coop coop = Objects.requireNonNull(coopRepo.blockingGetCoop(coopId)).getValue();

		assert coop != null;

		if (coop.sinkMode) {
			String report = ReportBuilder.makeBuilder(this, coop).sinkReport();
			ReportBuilder.copyText(this, report);
		} else {
			new NotificationHelper(this).sendActions(coop);
		}

		this.finish();
	}
}
