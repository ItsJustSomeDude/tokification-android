package net.itsjustsomedude.tokens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityListCoopsBinding;

public class ListCoopsActivity extends AppCompatActivity {

	ActivityListCoopsBinding binding;
	SimpleCursorAdapter adapter;
	Database database;
	Cursor coops;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityListCoopsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		setTitle("Select Co-op");

		database = new Database(this);
		coops = database.fetchCoops();

		binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
		binding.toolbar.setNavigationOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
		});

		binding.listView.setEmptyView(binding.empty);

		render();

		binding.listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			Coop.setSelectedCoop(this, viewId);

			startActivity(new Intent(this, MainActivity.class));
		});

		binding.listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			SimpleDialogs.yesNoPicker(
					this,
					"Delete Coop",
					"Are you sure you want to delete this coop?",
					"Yes",
					v -> {
						database.deleteCoop(viewId, true);
						coops = database.fetchCoops();
						
						if (Coop.getSelectedCoop(this) == viewId)
						    Coop.setSelectedCoop(this, 0);

						render();
					},
					"No",
					v -> {
					},
					"Delete Keeping Events",
					v -> {
						database.deleteCoop(viewId, false);
						coops = database.fetchCoops();
						
						if (Coop.getSelectedCoop(this) == viewId)
						    Coop.setSelectedCoop(this, 0);

						render();
					}
			);

			// Consume the event.
			return true;
		});
	}

	private void render() {
		adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_2,
				coops,
				new String[]{DatabaseHelper._ID, DatabaseHelper.COOP_NAME},
				new int[]{android.R.id.text1, android.R.id.text2},
				0);
		adapter.notifyDataSetChanged();

		binding.listView.setAdapter(adapter);
	}

	private void refresh() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.coop_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_coop) {
			Coop newCoop = database.createCoop();
			SharedPreferences sharedPref = getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);
			sharedPref.edit()
					.putLong("SelectedCoop", newCoop.id)
					.apply();
			startActivity(new Intent(this, MainActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		binding = null;
		database.close();
	}
}
