package net.itsjustsomedude.tokens;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import net.itsjustsomedude.tokens.db.Coop;
import net.itsjustsomedude.tokens.db.CoopRepository;
import net.itsjustsomedude.tokens.db.Event;
import net.itsjustsomedude.tokens.db.EventRepository;

import java.util.Calendar;
import java.util.Objects;

public class SinkTokensService extends Service {
	public SinkTokensService() {
	}

	public static final String PARAM_COOP_NAME = "Coop";
	public static final String PARAM_KEV_ID = "KevID";
	public static final String PARAM_PLAYER = "Player";
	public static final String PARAM_COUNT = "Count";

	public static Intent makeIntent(Context ctx, String coop, String kevId, String player, int count) {
		return new Intent(ctx, SinkTokensService.class)
				.putExtra(PARAM_COOP_NAME, coop)
				.putExtra(PARAM_PLAYER, player)
				.putExtra(PARAM_KEV_ID, kevId)
				.putExtra(PARAM_COUNT, count);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		EventRepository eventRepo = new EventRepository(getApplication());

		String coopName = intent.getStringExtra(PARAM_COOP_NAME);
		String kevId = intent.getStringExtra(PARAM_KEV_ID);
		String player = intent.getStringExtra(PARAM_PLAYER);
		int count = intent.getIntExtra(PARAM_COUNT, 1);

		eventRepo.blockingInsert(new Event(
				coopName,
				kevId,
				Calendar.getInstance(),
				count,
				player,
				Event.DIRECTION_RECEIVED,
				0
		));

		Toast.makeText(
				this,
				"Recorded: Sink received " + count,
				Toast.LENGTH_SHORT
		).show();

		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}