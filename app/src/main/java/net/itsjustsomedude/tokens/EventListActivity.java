package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityEventListBinding;

public class EventListActivity extends AppCompatActivity {

	public static final String PARAM_COOP_ID = "CoopId";

	ActivityEventListBinding binding;
	Cursor events;
	SimpleCursorAdapter adapter;
	Database database;
	long coopId;
	Coop coop;

	ActivityResultLauncher<Intent> callbackHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityEventListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		setTitle("Select Event");

		callbackHandler = SimpleDialogs.registerActivityCallback(this, result -> {
			coop = database.fetchCoop(coopId);
			events = database.fetchEvents(coop.name, coop.contract);

			adapter = new SimpleCursorAdapter(
					this,
					android.R.layout.simple_list_item_2,
					events,
					new String[]{DatabaseHelper.EVENT_PERSON, DatabaseHelper.EVENT_COUNT},
					new int[]{android.R.id.text1, android.R.id.text2},
					0
			);

			binding.listView.setAdapter(adapter);
		});

		Intent b = getIntent();
		coopId = b.getLongExtra(PARAM_COOP_ID, 0);

		database = new Database(this);
		coop = database.fetchCoop(coopId);
		events = database.fetchEvents(coop.name, coop.contract);

		binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
		binding.toolbar.setNavigationOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
		});

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
			callbackHandler.launch(new Intent(this, EditEventActivity.class)
					.putExtra(EditEventActivity.PARAM_EVENT_ID, viewId)
					.putExtra(EditEventActivity.PARAM_COOP_ID, coop.id)
			);
		});

		binding.listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			SimpleDialogs.yesNoPicker(
					this,
					"Delete Event",
					"Are you sure you want to delete this event?",
					"Yes",
					v -> {
						database.deleteEvent(viewId);
						events = database.fetchEvents(coop.name, coop.contract);

						adapter.notifyDataSetChanged();
					},
					"No",
					v -> {
					},
					"",
					v -> {
					}
			);

			// Consume the event.
			return true;
		});
	}
}
