package net.itsjustsomedude.tokens;

import android.content.Intent;
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
	
	public static final String PREFERENCES = "Prefs";
	
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
		} else {
			binding.selectedCoop.setText("No Coop Selected!");
			
			binding.mainRefresh.setEnabled(false);
			binding.mainSend.setEnabled(false);
			
			//binding.mainEdit.setText("Create Coop");
		}
		
		binding.mainRefresh.setOnClickListener(view -> {
			try {
				NotificationReader.processNotifications(coop);
				coop.modified = true;
				coop.save(this);
				Toast.makeText(this, "This must have worked!", Toast.LENGTH_SHORT).show();
			} catch(Exception err) {
				Log.e("", "Failed to get notifications.", err);
				Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
			}
		});
		
		binding.mainSend.setOnClickListener(view -> {
			startActivity(new Intent(this, SendTokensActivity.class));
		});
		
		binding.CopyReport.setOnClickListener(v -> {
			Log.i("Report", Reports.sinkReport(coop));
		});
		
		// detailed report...
		
		binding.mainEdit.setOnClickListener(view -> {
			Intent edit = new Intent(this, EditCoopActivity.class);
			if (coop != null)
			    edit.putExtra(EditCoopActivity.EDIT_ID, Long.toString(coop.id));
			startActivity(edit);
		});
		
		// Edit Events...
		
		binding.mainSwitchCoop.setOnClickListener(view -> {
			startActivity(new Intent(this, ListCoopsActivity.class));
		});

		SharedPreferences sharedPref = getSharedPreferences(
			PREFERENCES,
			Context.MODE_PRIVATE
		);
		String savedName = sharedPref.getString("PlayerName", "");
		binding.mainPlayerName.setText(savedName);
		binding.mainSaveName.setOnClickListener(view -> {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("PlayerName", binding.mainPlayerName.getText().toString());
			editor.apply();

			Toast.makeText(this, "Player Name Saved.", Toast.LENGTH_SHORT).show();
		});

//		binding.CopyDReport.setOnClickListener(v -> {
//			Coop.createCoop().save(this);
//		});
		
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}