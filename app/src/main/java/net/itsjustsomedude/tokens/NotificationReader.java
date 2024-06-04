package net.itsjustsomedude.tokens;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationReader {
	private static final String TAG = "Notifications";
	private static final List<String> ALLOWED_PACKAGES = Arrays.asList(
			"com.auxbrain.egginc",
			"net.itsjustsomedude.tokens",
			"net.itsjustsomedude.tokens.debug"
	);

	private static final Pattern personCoopRegex = Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$");
	private static final Pattern tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)");

	public static void stopService() {
		NotificationService service = NotificationService.get();
		if (service != null)
			service.stopSelf();
	}

	static void processNotifications() {
		Log.i(TAG, "Starting manual processing of notifications!");

		NotificationService notificationService = NotificationService.get();
		if (notificationService == null) {
			throw new NullPointerException("BLAH");
		}

		StatusBarNotification[] notifications = notificationService.getActiveNotifications();

		Context ctx = notificationService.getApplicationContext();
		Database db = new Database(ctx);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean shouldDismiss = sharedPreferences.getBoolean("auto_dismiss", false);

		for (StatusBarNotification n : notifications) {
			processNotification(db, n, shouldDismiss);
		}

		db.close();
		Log.i(TAG, "Manual processing complete.");
	}

	public static void processNotification(Database db, StatusBarNotification n, boolean shouldDismiss) {
		int id = n.getId();
		Notification innerNote = n.getNotification();
		if (innerNote == null) {
			Log.i(TAG, "Hit notification with no inner content.");
			return;
		}

		String key = n.getKey() != null ? n.getKey() : "";
		String title = innerNote.extras.getCharSequence(Notification.EXTRA_TITLE) != null ?
				Objects.requireNonNull(innerNote.extras.getCharSequence(Notification.EXTRA_TITLE)).toString()
				: "";

		CharSequence bigText = innerNote.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
		CharSequence extraText = innerNote.extras.getCharSequence(Notification.EXTRA_TEXT);

		String text = bigText != null ?
				bigText.toString() :
				extraText != null ?
						extraText.toString() :
						"";

		String group = innerNote.getGroup() != null ? innerNote.getGroup() : "";
		Calendar when = Calendar.getInstance();
		when.setTimeInMillis(n.getNotification().when);

		if (n.getPackageName() == null || !ALLOWED_PACKAGES.contains(n.getPackageName())) {
			// Log.i(TAG, "Skipping because Package Name.");
			return;
		}

		if ((innerNote.flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
			Log.i(TAG, "Skipping group summary notification");
			return;
		}

		if (text.contains("new message") || !title.contains("Gift Received")) {
			Log.i(TAG, "Skipping because of New Messages or not a gift.");
			return;
		}

		Log.i(TAG, "Processing note " + group + id + title + text);

		Matcher matches = personCoopRegex.matcher(text);
		if (!matches.lookingAt() || matches.groupCount() < 2) {
			Log.e(TAG, "Person/Coop Regex didn't match for notification content:");
			Log.e(TAG, text);
			return;
		}

		String person = matches.group(1);
		String coopName = matches.group(2);

		if (db.eventExists(id)) {
			Log.i(TAG, "Skipping notification that has already been processed.");
			if (shouldDismiss) removeNotification(key);
			return;
		}

		if (title.contains("🐣")) {
			Log.i(TAG, "Processing CR");
			// Count of 0 indicates a CR.
			db.createEvent(coopName, group, when, 0, "sent", person, id);
			if (shouldDismiss) removeNotification(key);
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

		db.createEvent(coopName, group, when, amount, "sent", person, id);
		Log.i(TAG, "Added event from note:");
		Log.i(TAG, text);

		if (shouldDismiss) removeNotification(key);
	}

	private static void removeNotification(String key) {
		NotificationService service = NotificationService.get();
		if (service == null) return;

		Handler dh = NotificationService.dismissHandler;
		if (dh == null) return;

		dh.postDelayed(() -> {
			service.cancelNotification(key);
		}, 10 * 1000);
	}

	private static void askToEnable(Context ctx, String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
		ctx.startActivity(
				new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		);
	}

	private static void askToStart(Context ctx) {
		ctx.startService(new Intent(ctx, net.itsjustsomedude.tokens.NotificationReader.NotificationService.class));
		Toast.makeText(ctx, "Notification Service Started.", Toast.LENGTH_SHORT).show();
	}

	public static boolean verifyServiceRunning(Context ctx) {
		ComponentName cn = new ComponentName(ctx, NotificationService.class);
		String flat = Settings.Secure.getString(ctx.getContentResolver(), "enabled_notification_listeners");
		final boolean NotificationServiceEnabled = flat != null && flat.contains(cn.flattenToString());
		if (!NotificationServiceEnabled) {
			askToEnable(ctx, "Please give Tokification Notification Access.");
			return false;
		} else {
			if (NotificationService.get() == null) {
//				askToEnable(ctx, "The listener is not running! Make sure Tokification access is granted.");
				return false;
			}
			return true;
		}
	}

	// Listener service.
	public static class NotificationService extends NotificationListenerService {
		static NotificationService _this;
		static Handler dismissHandler;

		public static NotificationService get() {
			return _this;
		}

		@Override
		public void onListenerConnected() {
			_this = this;
			dismissHandler = new Handler(Looper.getMainLooper());
		}

		@Override
		public void onListenerDisconnected() {
			_this = null;
			dismissHandler = null;
		}

		@Override
		public void onNotificationPosted(StatusBarNotification sbn) {
			super.onNotificationPosted(sbn);

			// Quick early return before trying to open a DB connection if it will be un-necessary.
			if (sbn.getPackageName() == null || !ALLOWED_PACKAGES.contains(sbn.getPackageName()))
				return;

			Log.i(TAG, "Automatically processing incoming notification.");

			Context ctx = _this.getApplicationContext();
			Database db = new Database(ctx);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
			boolean shouldDismiss = sharedPreferences.getBoolean("auto_dismiss", false);

			processNotification(db, sbn, shouldDismiss);
			db.close();
		}
	}
}
