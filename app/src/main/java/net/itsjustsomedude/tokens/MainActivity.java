package net.itsjustsomedude.tokens;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
		
		Notifications.createChannels(this);
		Notifications.sendActions(this);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);

		coop = Coop.fetchSelectedCoop(this);

		if(coop != null) {
			binding.selectedCoop.setText("Selected Coop: " + coop.name);
//			binding.
		}

		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		String savedName = sharedPref.getString("PlayerName", "");
		binding.mainPlayerName.setText(savedName);
		binding.mainSaveName.setOnClickListener(view -> {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("PlayerName", view.toString());
			editor.apply();

			Toast.makeText(this, "Player Name Saved.", Toast.LENGTH_SHORT).show();
		});
//
//		binding.CopyReport.setOnClickListener(v -> {
//			try {
//				Coop testing = Coop.fetchSelectedCoop(this);
//                NotificationReader.processNotifications(testing);
//				testing.save(this);
//
//				Toast.makeText(MainActivity.this, "This must have worked!", Toast.LENGTH_SHORT).show();
//			} catch(Exception err) {
//				Log.i("", "Failed to get notifications.", err);
//			}
//        });
//
//		binding.CopyDReport.setOnClickListener(v -> {
//			Coop.createCoop().save(this);
//		});
		
//		Cursor coops = Coop.fetchCoops(this);
//		
//		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//			this,
//			android.R.layout.simple_spinner_item,
//			coops,
//			new String[] { DatabaseHelper.COOP_NAME },
//			new int[] { android.R.id.text1 },
//			0);
//		
//		binding.SelectExistingCoop.setAdapter(adapter);
//		binding.SelectExistingCoop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//					// TODO: Implement this method
//				}
//				
//				@Override
//				public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long id) {
//					// TODO: Implement this method
//					
//					Toast.makeText(parent.getContext(), "You picked " + id, Toast.LENGTH_LONG).show();
//				}
//		});
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}