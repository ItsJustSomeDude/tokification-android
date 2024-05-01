package net.itsjustsomedude.tokens;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationReader {
	private static final String TAG = "Notifications";
	private static final String PACKAGE = "com.auxbrain.egginc_IGNORE";
	private static final String ALT_PACKAGE = "com.termux.api";

	private static final Pattern personCoopRegex = Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$");
	private static final Pattern tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)");

	static void processNotifications() {
        NotificationService notificationService = NotificationService.get();
		StatusBarNotification[] notifications = notificationService.getActiveNotifications();

		Context ctx = notificationService.getApplicationContext();
		HashMap<String, Coop> coopCache = new HashMap<>();
		Database db = new Database(ctx).open();

		boolean shouldDismiss = ctx.getSharedPreferences(MainActivity.PREFERENCES, Activity.MODE_PRIVATE).getBoolean("AutoDismiss", false);
		ArrayList<String> toDismiss = new ArrayList<>();

        for (StatusBarNotification n : notifications) {
			String k = processNotification(n, db, coopCache);
			if(shouldDismiss && k != null) {
				Log.i(TAG, "Preparing to dismiss " + k);
				toDismiss.add(k);
			}
        }

		saveCache(db, coopCache);

		if(shouldDismiss) {
			Log.i(TAG, "Dismissing stuff." + String.join(", ", toDismiss.toArray(new String[0])));
			notificationService.cancelNotifications(toDismiss.toArray(new String[0]));
		}
    }

	public static String processNotification(StatusBarNotification n, Database db) {
		HashMap<String, Coop> cache = new HashMap<>();
		String toDismiss = processNotification(n, db, new HashMap<>());
		saveCache(db, cache);
		return toDismiss;
	}

	public static String processNotification(StatusBarNotification n, Database db, HashMap<String, Coop> cache) {
		int id = n.getId();
//		String key = "";
		String title = "";
		String text = "";
//		CharSequence[] lines = null;
//		String tag = "";
//		String group = "";
		Calendar when = Calendar.getInstance();
		when.setTimeInMillis(n.getNotification().when);

		if (n.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE) != null) {
			title = n.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
		}
		if (n.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null) {
			text = n.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();
		} else if (n.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
			text = n.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
		}
//		if (n.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES) != null) {
//			lines = n.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
//		}
//		if (n.getNotification().getGroup() != null) {
//			group = n.getNotification().getGroup();
//		}
//		if (n.getKey() != null) {
//			key = n.getKey();
//		}

		if (n.getPackageName() == null)
			return null;
		if(!n.getPackageName().equals(PACKAGE) && !n.getPackageName().equals(ALT_PACKAGE))
			return null;

		if (text.contains("new message") || !title.contains("Gift Received"))
			return null;

		// Hacky workaround to make Termux Notifications work.
		try {
			if(n.getPackageName().equals(ALT_PACKAGE)) id = Integer.parseInt(n.getTag());
		} catch (Exception ignored) {}

		Log.i(TAG, "Processing note " + id + title +  text);

		Matcher matches = personCoopRegex.matcher(text);
		if (!matches.lookingAt() || matches.groupCount() < 2) {
			Log.e(TAG, "Person/Coop Regex didn't match for notification content:");
			Log.e(TAG, text);
			return null;
		}

		String person = matches.group(1);
		String coopName = matches.group(2);

		Coop coop;
		if(cache.containsKey(coopName)) {
			Log.i(TAG, "Fetched coop from cache.");
			coop = cache.get(coopName);
		} else {
			coop = db.fetchCoopByName(coopName);
			if(coop == null) {
				Log.i(TAG, "Found notification for coop we don't recognize!");
				return null;
			}
			cache.put(coopName, coop);
		}

		if(coop == null) {
			Log.i(TAG, "Found notification for coop we don't recognize!");
			return null;
		}

		for (Event ev : coop.events) {
			// Log.i(TAG, "Checking if event " + ev.notification + " is the same as the current " + id);
			if (ev.notification == id) return n.getKey();
		}

		// Theoretically, this isn't needed because we just fetched the coop based on this value.
		if (!coop.name.equals(coopName)) {
			Log.i(TAG, "Skipping notification from wrong coop:");
			Log.i(TAG, "Expected '" + coop.name + "' but found '" + coopName + "'");
			return null;
		}

		if (title.contains("🐣")) {
			Log.i(TAG, "Processing CR");
			if (coop.startTime == null || when.before(coop.startTime)) {
				Log.i(TAG, "Found earlier start!");
				coop.startTime = when;
			}
			return n.getKey();
		}

		int amount;
		if (text.contains("has sent you a Boost Token"))
			amount = 1;
		else {
			try {
				Matcher countMatch = tokenCountRegex.matcher(text);
				if (!countMatch.find() || countMatch.group(1) == null) {
					Log.e(TAG, "Count Regex didn't match for notification content:");
					Log.e(TAG, text);
					return null;
				}
				amount = Integer.parseInt(Objects.requireNonNull(countMatch.group(1)));
			} catch (NumberFormatException err) {
				amount = 0;
				Log.e(TAG, "Bad number in note:");
				Log.e(TAG, text);
			}
		}
		coop.addEvent(when, amount, "sent", person, id);
		Log.i(TAG, "Added event from note:");
		Log.i(TAG, text);

		return n.getKey();
	}

	private static void saveCache(Database db, HashMap<String, Coop> cache) {
		for (Coop coop : cache.values())
			db.saveCoop(coop);
	}
	
	// Listener service.
    public static class NotificationService extends NotificationListenerService {
        static NotificationService _this;

        public static NotificationService get() {
            return _this;
        }

        @Override
        public void onListenerConnected() {
            _this = this;
        }

        @Override
        public void onListenerDisconnected() {
            _this = null;
        }

		@Override
		public void onNotificationPosted(StatusBarNotification sbn) {
			super.onNotificationPosted(sbn);

			if(sbn.getPackageName() == null || ( !sbn.getPackageName().equals(PACKAGE) && !sbn.getPackageName().equals(ALT_PACKAGE)))
				return;

			Context ctx = _this.getApplicationContext();
			boolean shouldDismiss = ctx.getSharedPreferences(MainActivity.PREFERENCES, Activity.MODE_PRIVATE).getBoolean("AutoDismiss", false);
			Database db = new Database(ctx).open();
			String toDismiss = processNotification(sbn, db);
			if(shouldDismiss && toDismiss != null) {
				Log.i(TAG, "Dismissing stuff.");
				_this.cancelNotification(toDismiss);
			}
			db.close();
		}
	}
}
