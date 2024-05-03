package net.itsjustsomedude.tokens;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationReader {
	private static final String TAG = "Notifications";
	private static final List<String> ALLOWED_PACKAGES = Arrays.asList(
			"com.auxbrain.egginc",
			"net.itsjustsomedude.tokens"
	);

	private static final Pattern personCoopRegex = Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$");
	private static final Pattern tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)");

	private static boolean shouldDismiss = false;
	private static final HashMap<String, Coop> coopCache = new HashMap<>();

	static void processNotifications() {
		NotificationService notificationService = NotificationService.get();
		StatusBarNotification[] notifications = notificationService.getActiveNotifications();

		Context ctx = notificationService.getApplicationContext();
		Database db = new Database(ctx);
		shouldDismiss = ctx.getSharedPreferences(MainActivity.PREFERENCES, Activity.MODE_PRIVATE).getBoolean("AutoDismiss", false);

		for (StatusBarNotification n : notifications) {
			processNotification(db, n);
		}

		saveCache(db);
		db.close();
	}

	public static void processNotification(Database db, StatusBarNotification n) {
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

		if (n.getPackageName() == null)
			return;
		if (!ALLOWED_PACKAGES.contains(n.getPackageName())) {
			Log.i(TAG, "Skipping because Package Name.");
			return;
		}

		if (text.contains("new message") || !title.contains("Gift Received")) {
			Log.i(TAG, "Skipping because of New Messages or not a gift.");
			return;
		}

		Log.i(TAG, "Processing note " + id + title + text);

		Matcher matches = personCoopRegex.matcher(text);
		if (!matches.lookingAt() || matches.groupCount() < 2) {
			Log.e(TAG, "Person/Coop Regex didn't match for notification content:");
			Log.e(TAG, text);
			return;
		}

		String person = matches.group(1);
		String coopName = matches.group(2);

		Coop coop;
		if (coopCache.containsKey(coopName)) {
			Log.i(TAG, "Fetched coop from cache.");
			coop = coopCache.get(coopName);
		} else {
			coop = db.fetchCoopByName(coopName);
			if (coop == null) {
				Log.i(TAG, "Found notification for coop we don't recognize!");
				return;
			}
			coopCache.put(coopName, coop);
		}

		if (coop == null) {
			Log.i(TAG, "Found notification for coop we don't recognize!");
			return;
		}

		for (Event ev : coop.events) {
			// Log.i(TAG, "Checking if event " + ev.notification + " is the same as the current " + id);
			if (ev.notification == id) {
				removeNotification(n);
				return;
			}
		}

		// Theoretically, this isn't needed because we just fetched the coop based on this value.
		if (!coop.name.equals(coopName)) {
			Log.i(TAG, "Skipping notification from wrong coop:");
			Log.i(TAG, "Expected '" + coop.name + "' but found '" + coopName + "'");
			return;
		}

		if (title.contains("🐣")) {
			Log.i(TAG, "Processing CR");
			if (coop.startTime == null || when.before(coop.startTime)) {
				Log.i(TAG, "Found earlier start!");
				coop.startTime = when;
				coop.modified = true;
			}
			removeNotification(n);
			return;
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
					return;
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

		removeNotification(n);
	}

	private static void saveCache(Database db) {
		Log.i(TAG, "Saving All Coops! " + coopCache.keySet() + " len " + coopCache.values());
		for (Coop coop : coopCache.values())
			db.saveCoop(coop);
		coopCache.clear();
	}

	private static void removeNotification(StatusBarNotification n) {
		// TODO: Comment this out in Prod.
		if (n.getPackageName().equals("com.auxbrain.egginc")) return;

		if (!shouldDismiss) return;

		NotificationService service = NotificationService.get();
		if (service == null) return;

		service.cancelNotification(n.getKey());
	}

	private static void askToEnable(Context ctx) {
		Toast.makeText(ctx, "Please give Tokification Notification Access.", Toast.LENGTH_LONG).show();
		ctx.startActivity(
				new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		);
	}

	public static boolean verifyServiceRunning(Context ctx) {
		if (NotificationService.get() == null) {
			askToEnable(ctx);
			return false;
		}

		ComponentName cn = new ComponentName(ctx, NotificationService.class);
		String flat = Settings.Secure.getString(ctx.getContentResolver(), "enabled_notification_listeners");
		final boolean NotificationServiceEnabled = flat != null && flat.contains(cn.flattenToString());
		if (!NotificationServiceEnabled) {
			askToEnable(ctx);
			return false;
		} else {
			return true;
		}
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

			if (sbn.getPackageName() == null || !ALLOWED_PACKAGES.contains(sbn.getPackageName()))
				return;

			Context ctx = _this.getApplicationContext();
			shouldDismiss = ctx.getSharedPreferences(MainActivity.PREFERENCES, Activity.MODE_PRIVATE).getBoolean("AutoDismiss", false);
			Database db = new Database(ctx);
			processNotification(db, sbn);
			saveCache(db);
			db.close();
		}
	}
}
