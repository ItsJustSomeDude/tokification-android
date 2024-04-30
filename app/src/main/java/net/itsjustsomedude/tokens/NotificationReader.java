package net.itsjustsomedude.tokens;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationReader {
	private static final String TAG = "Notifications";
	
    static void processNotifications(Coop coop) throws Exception {
        NotificationService notificationService = NotificationService.get();
        StatusBarNotification[] notifications = notificationService.getActiveNotifications();
		Pattern personCoopRegex = Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$");
		Pattern tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)");

        for (StatusBarNotification n : notifications) {
            int id = n.getId();
            String key = "";
            String title = "";
            String text = "";
            CharSequence[] lines = null;
            String packageName = "";
            String tag = "";
            String group = "";
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            if (n.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES) != null) {
                lines = n.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            }
            if (n.getTag() != null) {
                tag = n.getTag();
            }
            if (n.getNotification().getGroup() != null) {
                group = n.getNotification().getGroup();
            }
            if (n.getKey() != null) {
                key = n.getKey();
            }
            if (n.getPackageName() != null) {
                packageName = n.getPackageName();
            }
			
			if (!packageName.equals("com.auxbrain.egginc"))
				continue;
			
			Log.i(TAG, "Processing note " + packageName + ", " + id + title + when + text);
			
			if (text.contains("new message"))
				continue;
			if (!title.contains("Gift Received"))
			    continue;
			
			boolean isExisting = false;
			for (Event ev : coop.events) {
				Log.i(TAG, "Checking if event " + ev.notification + " is the same as the current " + id);
				if (ev.notification == id) {
					isExisting = true;
					break;
				}
			}
			if (isExisting) continue;
			
			Matcher matches = personCoopRegex.matcher(text);
			if (!matches.lookingAt()) {
				Log.e(TAG, "P/C Regex didn't match for notification content:");
				Log.e(TAG, text);
				continue;
			}
			
			String person = matches.group(1);
			String coopName = matches.group(2);
			
			if (!coop.name.equals(coopName)) {
				Log.i(TAG, "Skipping notification from wrong coop:");
				Log.i(TAG, "Expected '" + coop.name + "' but found '" + coopName + "'");
				continue;
			}
			
			if (title.contains("🐣")) {
				Log.i(TAG, "Processing CR");
				if (coop.startTime == null || when.before(coop.startTime)) {
					Log.i(TAG, "Found earlier start!");
					coop.startTime = when;
				}
				continue;
			}
			
			int amount = 0;
			if (text.contains("has sent you a Boost Token"))
			    amount = 1;
			else {
				try {
					Matcher countMatch = tokenCountRegex.matcher(text);
				    if (!countMatch.find()) {
					    Log.e(TAG, "Count Regex didn't match for notification content:");
						Log.e(TAG, text);
					    continue;
				    }
				    amount = Integer.parseInt(countMatch.group(1));
				} catch (NumberFormatException err) {
					Log.e(TAG, "Bad number in note:");
					Log.e(TAG, text);
				}
			}
			coop.addEvent(when, amount, "sent", person, id);
			Log.i(TAG, "Added event from note:");
			Log.i(TAG, text);
        }
    }
	
	//public void processNotification(StatusBarNotification n) {
	//}
	
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
    }
}
