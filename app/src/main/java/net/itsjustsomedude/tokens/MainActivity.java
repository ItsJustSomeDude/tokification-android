package net.itsjustsomedude.tokens;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;
import net.itsjustsomedude.tokens.databinding.ActivityMainBinding;


import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.JsonWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Notification;

public class MainActivity extends AppCompatActivity {
	
    private ActivityMainBinding binding;
	
	public Coop coop;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		coop = Coop.fetchSelectedCoop(this);
		
		binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		binding.fab.setOnClickListener(v -> {
			try {
                Notifications.listNotifications();
			} catch(Exception err) {
				System.out.println("Failed to get notifications.");
				System.err.println(err);
			}
			
            Toast.makeText(MainActivity.this, "Replace with your action", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}