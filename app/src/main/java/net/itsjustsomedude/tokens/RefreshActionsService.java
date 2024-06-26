package net.itsjustsomedude.tokens;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RefreshActionsService extends Service {
	public static final String PARAM_COOP_ID = "CoopId";

	public RefreshActionsService() {
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Database db = new Database(this);
		long coopId = intent.getLongExtra(PARAM_COOP_ID, 0);
		Coop coop = db.fetchCoop(coopId);

		if (coop.sinkMode) {
			String report = ReportBuilder.makeBuilder(this, coop).sinkReport();
			ReportBuilder.copyText(this, report);
		} else {
			new NotificationHelper(this).sendActions(coop);
		}

		stopSelf();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}