package net.itsjustsomedude.tokens;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationHelper {
	private static final String TAG = "Notifications";

	private static final String ACTION_CHANNEL = "Actions";
	private static final String FAKE_CHANNEL = "Fake";
	private static final int ACTIONS_ID = 770;

	Context ctx;

	public NotificationHelper(Context ctx) {
		this.ctx = ctx;
	}

	public void createChannels() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

		// Action Channel
		CharSequence name = ctx.getString(R.string.action_channel_name);
		String desc = ctx.getString(R.string.action_channel_desc);
		int importance = NotificationManager.IMPORTANCE_DEFAULT;
		NotificationChannel channel = new NotificationChannel(ACTION_CHANNEL, name, importance);
		channel.setDescription(desc);
		channel.enableVibration(false);
		channel.setSound(null, null);

		// Fake Channel
		CharSequence name2 = ctx.getString(R.string.fake_channel_name);
		String desc2 = ctx.getString(R.string.fake_channel_desc);
		int importance2 = NotificationManager.IMPORTANCE_DEFAULT;
		NotificationChannel channel2 = new NotificationChannel(FAKE_CHANNEL, name2, importance2);
		channel2.setDescription(desc2);
		channel2.enableVibration(false);
		channel2.setSound(null, null);

		NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);
		notificationManager.createNotificationChannel(channel2);
	}

	public Notification createSinkActions() {
		Intent openMenu = new Intent(ctx, MainActivity.class);
		openMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent openMenuPending = PendingIntent.getActivity(ctx, 0, openMenu, PendingIntent.FLAG_IMMUTABLE);

		Intent send = new Intent(ctx, SendTokensActivity.class);
		send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent sendPending = PendingIntent.getActivity(ctx, 0, send, PendingIntent.FLAG_IMMUTABLE);

		Intent editCoop = new Intent(ctx, EditCoopActivity.class);
		editCoop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent editCoopPending = PendingIntent.getActivity(ctx, 0, editCoop, PendingIntent.FLAG_IMMUTABLE);

		Intent quickRefresh = new Intent(ctx, SendTokensActivity.class);
		quickRefresh.putExtra(SendTokensActivity.PARAM_SKIP_SEND, true);
		quickRefresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent quickRefreshPending = PendingIntent.getActivity(ctx, 0, quickRefresh, PendingIntent.FLAG_IMMUTABLE);

		return new NotificationCompat.Builder(ctx, ACTION_CHANNEL)
				.setSmallIcon(android.R.drawable.ic_menu_compass)
				.setContentTitle("Tokification")
				.setContentText("Click to open the menu.")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setAutoCancel(false)
				.setContentIntent(openMenuPending)
				.addAction(
						android.R.drawable.arrow_up_float,
						"Send Tokens",
						sendPending
				).addAction(
						android.R.drawable.edit_text,
						"Edit Coop",
						editCoopPending
				).addAction(
						android.R.drawable.edit_text,
						"Copy Report",
						quickRefreshPending
				).build();
	}
	
	public Notification createNormalActions() {
		Intent openMenu = new Intent(ctx, MainActivity.class);
		openMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent openMenuPending = PendingIntent.getActivity(ctx, 0, openMenu, PendingIntent.FLAG_IMMUTABLE);

		Intent send1 = new Intent(ctx, SendTokensActivity.class);
		send1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		send1.putExtra(SendTokensActivity.PARAM_PLAYER, "Sink");
		send1.putExtra(SendTokensActivity.PARAM_COUNT, 1);
		PendingIntent send1Pending = PendingIntent.getActivity(ctx, 0, send1, PendingIntent.FLAG_IMMUTABLE);

		Intent send = new Intent(ctx, EditCoopActivity.class);
		send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		send.putExtra(SendTokensActivity.PARAM_PLAYER, "Sink");
		send.putExtra(SendTokensActivity.PARAM_COUNT, 2);
		PendingIntent sendPending = PendingIntent.getActivity(ctx, 0, send, PendingIntent.FLAG_IMMUTABLE);

		Intent quickRefresh = new Intent(ctx, SendTokensActivity.class);
		quickRefresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		quickRefresh.putExtra(SendTokensActivity.PARAM_SKIP_SEND, true);
		PendingIntent quickRefreshPending = PendingIntent.getActivity(ctx, 0, quickRefresh, PendingIntent.FLAG_IMMUTABLE);

		return new NotificationCompat.Builder(ctx, ACTION_CHANNEL)
				.setSmallIcon(android.R.drawable.ic_menu_compass)
				.setContentTitle("Tokification")
				.setContentText("<Insert TVal info here>")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setAutoCancel(false)
				.setContentIntent(openMenuPending)
				.addAction(
						android.R.drawable.arrow_up_float,
						"Sink 1 Token",
						send1Pending
				).addAction(
						android.R.drawable.edit_text,
						"Sink Tokens",
						sendPending
				).addAction(
						android.R.drawable.edit_text,
						"Refresh",
						quickRefreshPending
				).build();
	}

	public Notification createFake(String player, String coop, boolean isCR) {
		int rand = new Random().nextInt((20) + 1);
		String textContent;
		if (isCR) {
			textContent = player +
					" (" + coop + ") has " +
					"hatched " + rand + "chickens for you!";
		} else {
			textContent = player +
					" (" + coop + ") has sent you a " +
					(rand == 1 ? "Boost Token!" : "gift of " + rand + " Boost Tokens!");
		}

		return new NotificationCompat.Builder(ctx, FAKE_CHANNEL)
				.setSmallIcon(android.R.drawable.star_off)
				.setContentTitle(isCR ? "\uD83D\uDC23 Gift Received" : "\uD83D\uDCE6 Gift Received")
				.setContentText(textContent)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
//				.setGroup("fake-coop-2001")
				.build();
	}

	public void sendSinkActions() {
		sendNotification(ACTIONS_ID, createSinkActions());
	}

	public void sendFake(String player, String coop, boolean isCR) {
		int id = new Random().nextInt();

		sendNotification(id, createFake(player, coop, isCR));
	}

	private void sendNotification(int id, Notification note) {
		if (ActivityCompat.checkSelfPermission(
				ctx,
				Manifest.permission.POST_NOTIFICATIONS
		) != PackageManager.PERMISSION_GRANTED
		) {
			if (!(ctx instanceof Activity)) {
				Log.e(TAG, "Attempted to send a notification from something a context that's not an activity, and we don't have permissions!");
				return;
			}
			if (!ActivityCompat.shouldShowRequestPermissionRationale(
					(Activity) ctx,
					Manifest.permission.POST_NOTIFICATIONS)
			) {
				Toast.makeText(ctx, "Please grant notification permissions in settings/App Info!", Toast.LENGTH_LONG).show();
				return;
			}

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
				String[] toRequest = new String[]{Manifest.permission.POST_NOTIFICATIONS};
				ActivityCompat.requestPermissions((Activity) ctx, toRequest, 1);
			}

			return;
		}

		NotificationManagerCompat.from(ctx).notify(id, note);
	}
}
