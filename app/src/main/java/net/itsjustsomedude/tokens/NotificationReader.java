package net.itsjustsomedude.tokens;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import android.util.Log;
import android.widget.Toast;

import net.itsjustsomedude.tokens.db.CoopRepository;
import net.itsjustsomedude.tokens.db.Event;
import net.itsjustsomedude.tokens.db.EventRepository;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationReader {
	private static final String TAG = "NotificationReader";

	private static final List<String> ALLOWED_PACKAGES = Arrays.asList(
			"com.auxbrain.egginc",
			"net.itsjustsomedude.tokens",
			"net.itsjustsomedude.tokens.debug"
	);

	private static final Pattern personCoopRegex = Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$");
	private static final Pattern tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)");

	public static final String PREF_ENABLED = "service_control_enable_service";
	public static final String PREF_DISMISS = "auto_dismiss";
	// ^^^ Set in the root_preferences file.
	private static final int dismissDelay = 10 * 1000;

	public static void processAllNotifications() {
		NotificationService i = NotificationService.getInstance();
		if (i != null)
			i.processAllNotifications();
	}

	public static boolean isServiceEnabled(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PREF_ENABLED, true);
	}

	public static boolean isServiceRunning() {
		NotificationService i = NotificationService.getInstance();
		return i != null;
	}

	public static void setServiceEnabled(Context ctx, boolean enable) {
		PreferenceManager.getDefaultSharedPreferences(ctx)
				.edit()
				.putBoolean(PREF_ENABLED, enable)
				.apply();

		NotificationService service = NotificationService.getInstance();
		if (enable && service == null) {
			// Enable Service, it's not running.
			ComponentName cn = new ComponentName(ctx, NotificationService.class);
			String flat = Settings.Secure.getString(ctx.getContentResolver(), "enabled_notification_listeners");

			if (flat != null && flat.contains(cn.flattenToString())) {
				Log.i(TAG, "Asking it to start via requestRebind.");
				// TODO: Offer enable/disable from settings if android below N.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					NotificationListenerService.requestRebind(new ComponentName(ctx, NotificationService.class));
				}
			} else {
				Toast.makeText(ctx, ctx.getString(R.string.enable_service_toast), Toast.LENGTH_SHORT).show();
				ctx.startActivity(
						new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
				);
			}
		} else if (!enable && service != null) {
			service.stop();
		}
//		else if (enable && service != null) {
//			// Enable, already running.
//		}
//		else if (!enable && service == null) {
//			// Disable, not running.
//		}
	}

	public static class NotificationService extends NotificationListenerService {
		private Handler dismissHandler;
		private static NotificationService instance;

		public static NotificationService getInstance() {
			return instance;
		}

		@Override
		public void onListenerConnected() {
			instance = this;

			if (isServiceEnabled(this)) {
				dismissHandler = new Handler(Looper.getMainLooper());
			} else {
				stop();
			}
		}

		@Override
		public void onListenerDisconnected() {
			super.onListenerDisconnected();
			destroy();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			destroy();
		}

		public void stop() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				requestUnbind();
			} else {
				stopSelf();
			}
		}

		private void destroy() {
			instance = null;
			if (dismissHandler != null) {
				dismissHandler.removeCallbacksAndMessages(null);
				dismissHandler = null;
			}
		}

		@Override
		public void onNotificationPosted(StatusBarNotification sbn) {
			if (!isServiceEnabled(this) || !shouldProcessNotification(sbn)) {
				return;
			}


			processNotification(new EventRepository(getApplication()), sbn);
		}

		private boolean shouldProcessNotification(StatusBarNotification sbn) {
			if (!ALLOWED_PACKAGES.contains(sbn.getPackageName())) return false;

			Notification innerNote = sbn.getNotification();
			if (innerNote == null) return false;
			if ((innerNote.flags & Notification.FLAG_GROUP_SUMMARY) != 0) return false;

			CharSequence t = innerNote.extras.getCharSequence(Notification.EXTRA_TITLE);
			String title = t != null ?
					t.toString()
					: "";

			if (!title.contains("Gift Received")) return false;

			return true;
		}

		private void processNotification(EventRepository repository, StatusBarNotification sbn) {
			int id = sbn.getId();

			Notification innerNote = sbn.getNotification();

			String key = sbn.getKey();

			CharSequence bigText = innerNote.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
			CharSequence extraText = innerNote.extras.getCharSequence(Notification.EXTRA_TEXT);

			String text = bigText != null ?
					bigText.toString() :
					extraText != null ?
							extraText.toString() :
							"";

			// TODO: Add a pre-processor that extracts just the needed info.
			CharSequence t = innerNote.extras.getCharSequence(Notification.EXTRA_TITLE);
			String title = t != null ?
					t.toString()
					: "";

			String group = innerNote.getGroup();
			Calendar when = Calendar.getInstance();
			when.setTimeInMillis(innerNote.when);

			Log.i(TAG, "Processing note " + group + id + text);

			Matcher matches = personCoopRegex.matcher(text);
			if (!matches.lookingAt() || matches.groupCount() < 2) {
				Log.e(TAG, "Person/Coop Regex didn't match for notification content:");
				Log.e(TAG, text);
				return;
			}

			String person = matches.group(1);
			String coopName = matches.group(2);

			assert coopName != null;

			if (repository.blockingExists(coopName, group, id)) {
				Log.i(TAG, "Skipping notification that has already been processed.");
				scheduleRemoval(key);
				return;
			}

			if (title.contains("🐣")) {
				Log.i(TAG, "Processing CR");
				// Count of 0 indicates a CR.
				repository.blockingInsert(new Event(coopName, group, when, 0, person, Event.DIRECTION_SENT, id));
				scheduleRemoval(key);
				return;
			}

			int amount = 0;
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
					Log.e(TAG, "Bad number in note:");
					Log.e(TAG, text);
				}
			}

			repository.blockingInsert(new Event(coopName, group, when, amount, person, Event.DIRECTION_SENT, id));
			Log.i(TAG, "Added event from note:");
			Log.i(TAG, text);

			scheduleRemoval(key);
		}

		public void processAllNotifications() {
			if (!isServiceEnabled(this)) {
				return;
			}

			EventRepository repo = new EventRepository(getApplication());

			StatusBarNotification[] activeNotifications = getActiveNotifications();
			for (StatusBarNotification sbn : activeNotifications) {
				if (shouldProcessNotification(sbn))
					processNotification(repo, sbn);
			}
		}

		private void scheduleRemoval(String key) {
			if (shouldDismiss())
				dismissHandler.postDelayed(() -> cancelNotification(key), dismissDelay);
		}

		// TODO: Make less calls to shared prefs, cache this.
		private boolean shouldDismiss() {
			return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_DISMISS, false);
		}
	}
}