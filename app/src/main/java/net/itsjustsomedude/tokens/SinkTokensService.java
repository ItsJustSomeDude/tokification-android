package net.itsjustsomedude.tokens;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import net.itsjustsomedude.tokens.db.Coop;

import java.util.Calendar;

public class SinkTokensService extends Service {
	public SinkTokensService() {
	}

	public static final String PARAM_COOP_ID = "CoopId";
	public static final String PARAM_COUNT = "Count";

	public static Intent makeIntent(Context ctx, long coopId, int count) {
		return new Intent(ctx, SinkTokensService.class)
				.putExtra(PARAM_COOP_ID, coopId)
				.putExtra(PARAM_COUNT, count);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Database database = new Database(this);
		long coopId = intent.getLongExtra(PARAM_COOP_ID, 0);
		int count = intent.getIntExtra(PARAM_COUNT, 1);
		Coop coop = database.fetchCoop(coopId);

		if (coop.sinkMode) {
			Toast.makeText(this, "Auto send shouldn't be used in Sink Mode.", Toast.LENGTH_LONG).show();
		} else {
			Coop.Event newEvent = database.createEvent(
					coop.name,
					coop.contract,
					Calendar.getInstance(),
					count,
					"received",
					"Sink"
			);
			coop.addEvent(newEvent);

			Toast.makeText(
					this,
					"Recorded: Sink received " + count,
					Toast.LENGTH_SHORT
			).show();

			new NotificationHelper(this).sendActions(coop);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}