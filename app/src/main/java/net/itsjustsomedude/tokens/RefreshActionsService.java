package net.itsjustsomedude.tokens;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RefreshActionsService extends Service {
	public static final String PARAM_COOP_ID = "CoopId";

	public RefreshActionsService() {
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Database db = new Database(this);
		long coopId = intent.getLongExtra(PARAM_COOP_ID, 0);
		Coop coop = db.fetchCoop(coopId);

		new NotificationHelper(this).sendActions(coop);

		if (coop.sinkMode) {
			String report = new ReportBuilder(coop, "Temp").sinkReport();
			ReportBuilder.copyText(this, report);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}