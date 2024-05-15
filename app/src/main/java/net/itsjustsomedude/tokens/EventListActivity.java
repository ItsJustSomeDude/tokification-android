package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import net.itsjustsomedude.tokens.databinding.ActivityEventListBinding;

public class EventListActivity extends AppCompatActivity {
	
	public static final String PARAM_COOP_ID = "CoopId";
	
    ActivityEventListBinding binding;
	Cursor events;
	SimpleCursorAdapter adapter;
	Database database;
	Coop coop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityEventListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		setTitle("Select Co-op");
		
		Intent b = getIntent();

		database = new Database(this);
		coop = database.fetchCoop(b.getLongExtra(PARAM_COOP_ID, 0));
		events = database.fetchEvents(coop.name, coop.contract);

		binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
		binding.toolbar.setNavigationOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
		});
		
//		

		adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_2,
				events,
			    new String[]{DatabaseHelper.EVENT_PERSON, DatabaseHelper.EVENT_COUNT},
				new int[]{android.R.id.text1, android.R.id.text2},
				0
		);

		binding.listView.setEmptyView(binding.empty);
		binding.listView.setAdapter(adapter);

		binding.listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			startActivity(new Intent(this, MainActivity.class));
		});

		binding.listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			SimpleDialogs.yesNoPicker(
					this,
					"Delete Event",
					"Are you sure you want to delete this event?",
					"Yes",
					v -> {
//						database.deleteCoop(viewId, true);
//						coops = database.fetchCoops();
//
//						adapter.notifyDataSetChanged();
					},
					"No",
					v -> {
					},
					"Delete Keeping Events",
					v -> {
//						database.deleteCoop(viewId, false);
//						coops = database.fetchCoops();
//
//						adapter.notifyDataSetChanged();
					}
			);

			// Consume the event.
			return true;
		});
	}
}
