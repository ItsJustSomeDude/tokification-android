package net.itsjustsomedude.tokens;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notifications {
	private static final String ACTION_CHANNEL = "Actions";
	private static final int ACTIONS_ID = 770;
	
	public static void createChannels(Context ctx) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
		
		CharSequence name = ctx.getString(R.string.action_channel_name);
		String desc = ctx.getString(R.string.action_channel_desc);
		int importance = NotificationManager.IMPORTANCE_DEFAULT;
		NotificationChannel channel = new NotificationChannel(ACTION_CHANNEL, name, importance);
        channel.setDescription(desc);
		
		NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);
	}
	
	public static void sendActions(Context ctx) {
		Intent openMenu = new Intent(ctx, MainActivity.class);
		openMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent openMenuPending = PendingIntent.getActivity(ctx, 0, openMenu, PendingIntent.FLAG_IMMUTABLE);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, ACTION_CHANNEL)
		    .setSmallIcon(android.R.drawable.ic_dialog_alert)
		    .setContentTitle("Tokification")
            .setContentText("Click to open the menu.")
		    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
		    .setAutoCancel(false)
		    .setContentIntent(openMenuPending);

        NotificationManagerCompat mgr = NotificationManagerCompat.from(ctx);
		
        if (ActivityCompat.checkSelfPermission(
			ctx,
			Manifest.permission.POST_NOTIFICATIONS
		    ) != PackageManager.PERMISSION_GRANTED) {
			//TODO: ask for permission.

            //String[] toRequest = new String[] { Manifest.permission.POST_NOTIFICATIONS };
				
			//ActivityCompat.requestPermissions(ctx., toRequest, 1);
		}
		
		mgr.notify(ACTIONS_ID, builder.build());
	}
}
