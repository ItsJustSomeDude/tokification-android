package net.itsjustsomedude.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class SinkTokensActivity extends AppCompatActivity {
	private static final String TAG = "SinkTokens";
	
	public static final String PARAM_COOP_ID = "CoopId";
	public static final String PARAM_COUNT = "Count";
	
	public static Intent makeIntent(Context ctx, long coopId, int count) {
		return new Intent(ctx, SinkTokensActivity.class)
		    .putExtra(PARAM_COOP_ID, coopId)
		    .putExtra(PARAM_COUNT, count);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Database database = new Database(this);

		Intent b = getIntent();
		if (b == null) {
			Log.e(TAG, "No idea how this happened.");
			Toast.makeText(this, "Tell Dude: The Sink Activity Launched wrongly.", Toast.LENGTH_LONG).show();
			return;
		}

		long coopId = b.getLongExtra(PARAM_COOP_ID, 0);
		int count = b.getIntExtra(PARAM_COUNT, 1);

		// TODO: This DB lookup should be removed.  Just pass in the Coop and KevID and use those directly.
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
		
		this.finish();
	}
}
