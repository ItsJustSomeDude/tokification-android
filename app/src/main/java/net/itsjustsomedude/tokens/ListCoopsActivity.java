package net.itsjustsomedude.tokens;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.itsjustsomedude.tokens.databinding.ActivityListCoopsBinding;

public class ListCoopsActivity extends AppCompatActivity {

	SimpleCursorAdapter adapter;
	ActivityResultLauncher<Intent> returnHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityListCoopsBinding binding = ActivityListCoopsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		setTitle("Edit Co-op");

		returnHandler = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(),
				result -> {
					//if (result.getResultCode() != Activity.RESULT_OK) return;

					Database db2 = new Database(this);
					Cursor coops2 = db2.fetchCoops();
					adapter.changeCursor(coops2);
					adapter.notifyDataSetChanged();
				}
		);

		binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
		binding.toolbar.setNavigationOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
		});

		Database db = new Database(this);
		Cursor coops = db.fetchCoops();

		Log.i("Heh", "Found " + coops.getCount());

		adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_2,
				coops,
				new String[]{DatabaseHelper._ID, DatabaseHelper.COOP_NAME},
				new int[]{android.R.id.text1, android.R.id.text2},
				0);
		adapter.notifyDataSetChanged();

		binding.listView.setEmptyView(binding.empty);

		binding.listView.setAdapter(adapter);

		binding.listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			TextView idView = view.findViewById(android.R.id.text1);
			String id = idView.getText().toString();

			Intent editIntent = new Intent(getApplicationContext(), EditCoopActivity.class);
			editIntent.putExtra(EditCoopActivity.EDIT_ID, id);

			returnHandler.launch(editIntent);
		});

		db.close();
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
			Intent editIntent = new Intent(getApplicationContext(), EditCoopActivity.class);
			editIntent.putExtra(EditCoopActivity.PARAM_NEW, true);
			returnHandler.launch(editIntent);
		}
		return super.onOptionsItemSelected(item);
	}
}
