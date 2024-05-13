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
		
		NotificationChannel actionChannel = new NotificationChannel(
			ACTION_CHANNEL,
			ctx.getString(R.string.action_channel_name),
		    NotificationManager.IMPORTANCE_DEFAULT);
		actionChannel.setDescription(ctx.getString(R.string.action_channel_desc));
		actionChannel.enableVibration(false);
		actionChannel.setSound(null, null);

		NotificationChannel fakeChannel = new NotificationChannel(
			FAKE_CHANNEL,
			ctx.getString(R.string.fake_channel_name),
			NotificationManager.IMPORTANCE_DEFAULT);
		fakeChannel.setDescription(ctx.getString(R.string.fake_channel_desc));
		fakeChannel.enableVibration(false);
		fakeChannel.setSound(null, null);

		NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(actionChannel);
		notificationManager.createNotificationChannel(fakeChannel);
	}

	public void sendActions(Coop coop) {
		// Normal Click always opens menu.
		// First button always opens Send
		// Second is Sink 1 if Normal Mode
		// Second is Copy Report if Sink Mode
		// Third is Refresh if Normal mode.
		
		Intent openMenu = new Intent(ctx, MainActivity.class);
		//openMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /* | Intent.FLAG_ACTIVITY_CLEAR_TASK */);
		PendingIntent openMenuPending = PendingIntent.getActivity(ctx, 11, openMenu, PendingIntent.FLAG_IMMUTABLE);
		
		Intent send = new Intent(ctx, EditEventActivity.class);
		//send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /* | Intent.FLAG_ACTIVITY_CLEAR_TASK */);
		PendingIntent sendPending = PendingIntent.getActivity(ctx, 2, send, PendingIntent.FLAG_IMMUTABLE);
		
		NotificationCompat.Builder note = new NotificationCompat.Builder(ctx, ACTION_CHANNEL)
				.setSmallIcon(android.R.drawable.ic_menu_compass)
				.setContentTitle(coop.name)
				.setAutoCancel(false)
				.setContentIntent(openMenuPending)
				.addAction(
					androidx.appcompat.R.drawable.abc_ic_menu_share_mtrl_alpha,
					"Send Tokens",
					sendPending
			    )
				// These should only matter for versions before Channels were a thing.
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setVibrate(null)
				.setSound(null);
		
		if (coop.sinkMode) {
		    Intent copy = new Intent(ctx, MainActivity.class);
		    //editCoop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		    PendingIntent copyPending = PendingIntent.getActivity(ctx, 3, copy, PendingIntent.FLAG_IMMUTABLE);
			
			note.setContentText("Click to open the menu.")
			    .addAction(
					androidx.appcompat.R.drawable.abc_ic_menu_share_mtrl_alpha,
					"Copy Report",
					copyPending
			    );
		} else {
			Intent sink1 = new Intent(ctx, EditEventActivity.class);
		    //editCoop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		    PendingIntent sink1Pending = PendingIntent.getActivity(ctx, 3, sink1, PendingIntent.FLAG_IMMUTABLE);
			
			Intent refresh = new Intent(ctx, EditEventActivity.class);
		    //editCoop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		    PendingIntent refreshPending = PendingIntent.getActivity(ctx, 3, refreshPending, PendingIntent.FLAG_IMMUTABLE);
			
			String report = new ReportBuilder(coop, "You").normalReport();
			
			note.setContentText(report)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(report))
			    .addAction(
					androidx.appcompat.R.drawable.abc_ic_menu_share_mtrl_alpha,
					"Send 1 Token",
					sink1Pending
			    ).addAction(
					android.R.drawable.ic_menu_rotate,
					"Refresh",
					refreshPending
			    );
		}
		
		sendNotification(ACTIONS_ID, note.build());
	}

	public void sendFake(String player, String coop, boolean isCR) {
		int rand = new Random().nextInt((20) + 1);
		String textContent;
		if (isCR) {
			textContent = player +
					" (" + coop + ") has hatched " + rand + " chickens for you!";
		} else {
			textContent = player +
					" (" + coop + ") has sent you a " +
					(rand == 1 ? "Boost Token!" : "gift of " + rand + " Boost Tokens!");
		}

		Notification note = new NotificationCompat.Builder(ctx, FAKE_CHANNEL)
				.setSmallIcon(android.R.drawable.star_off)
				.setContentTitle(isCR ? "\uD83D\uDC23 Gift Received" : "\uD83D\uDCE6 Gift Received")
				.setContentText(textContent)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
//				.setGroup("fake-coop-2001")
				.build();

		int id = new Random().nextInt();
		sendNotification(id, note);
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

	public void ensurePermissions() {
		if (ActivityCompat.checkSelfPermission(
				ctx,
				Manifest.permission.POST_NOTIFICATIONS
		) != PackageManager.PERMISSION_GRANTED
		) {
			if (!(ctx instanceof Activity)) {
				Log.e(TAG, "Attempted to send a notification from something a context that's not an activity, and we don't have permissions!");
				return;
			}
//			if (!ActivityCompat.shouldShowRequestPermissionRationale(
//					(Activity) ctx,
//					Manifest.permission.POST_NOTIFICATIONS)
//			) {
//				Toast.makeText(ctx, "Please grant notification permissions in settings/App Info!", Toast.LENGTH_LONG).show();
//				return;
//			}

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
				String[] toRequest = new String[]{Manifest.permission.POST_NOTIFICATIONS};
				ActivityCompat.requestPermissions((Activity) ctx, toRequest, 1);
			}
		}
	}
}
