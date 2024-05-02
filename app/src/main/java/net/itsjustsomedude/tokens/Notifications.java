package net.itsjustsomedude.tokens;

import android.Manifest;
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

public class Notifications {
	private static final String TAG = "Notifications";

	private static final String ACTION_CHANNEL = "Actions";
	private static final String FAKE_CHANNEL = "Fake";
	private static final int ACTIONS_ID = 770;

	public static void createChannels(Context ctx) {
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

	public static Notification createActions(Context ctx) {
		Intent openMenu = new Intent(ctx, MainActivity.class);
		openMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent openMenuPending = PendingIntent.getActivity(ctx, 0, openMenu, PendingIntent.FLAG_IMMUTABLE);

		Intent send = new Intent(ctx, SendTokensActivity.class);
		openMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent sendPending = PendingIntent.getActivity(ctx, 0, send, PendingIntent.FLAG_IMMUTABLE);

		Intent editCoop = new Intent(ctx, SendTokensActivity.class);
		editCoop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent editCoopPending = PendingIntent.getActivity(ctx, 0, editCoop, PendingIntent.FLAG_IMMUTABLE);

		return new NotificationCompat.Builder(ctx, ACTION_CHANNEL)
				.setSmallIcon(android.R.drawable.ic_menu_compass)
				.setContentTitle("Tokification")
				.setContentText("Click to open the menu.")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setAutoCancel(false)
				.setContentIntent(openMenuPending)
				.addAction(
						android.R.drawable.arrow_up_float,
						"Send 6",
						sendPending
				).addAction(
						android.R.drawable.edit_text,
						"Edit Coop",
						editCoopPending
				).build();
	}

	public static Notification createFake(Context ctx, String player, String coop, boolean isCR) {
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

	public static void sendActions(Context ctx) {
		if (ActivityCompat.checkSelfPermission(
				ctx,
				Manifest.permission.POST_NOTIFICATIONS
		) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(ctx, "//TODO: Request notification permissions :)", Toast.LENGTH_LONG).show();
			Log.i(TAG, "We don't have notification permissions!'");

			//String[] toRequest = new String[] { Manifest.permission.POST_NOTIFICATIONS };
			//ActivityCompat.requestPermissions(ctx., toRequest, 1);
			return;
		}

		NotificationManagerCompat mgr = NotificationManagerCompat.from(ctx);
		mgr.notify(ACTIONS_ID, createActions(ctx));
	}

	public static void sendFake(Context ctx, String player, String coop, boolean isCR) {
		if (ActivityCompat.checkSelfPermission(
				ctx,
				Manifest.permission.POST_NOTIFICATIONS
		) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(ctx, "//TODO: Request notification permissions :)", Toast.LENGTH_LONG).show();
			Log.i(TAG, "We don't have notification permissions!'");

			//String[] toRequest = new String[] { Manifest.permission.POST_NOTIFICATIONS };
			//ActivityCompat.requestPermissions(ctx., toRequest, 1);
			return;
		}

		int id = new Random().nextInt();

		NotificationManagerCompat mgr = NotificationManagerCompat.from(ctx);
		mgr.notify(id, createFake(ctx, player, coop, isCR));
	}


}
