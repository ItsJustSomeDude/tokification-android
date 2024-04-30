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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import net.itsjustsomedude.tokens.databinding.ActivityListCoopsBinding;

public class ListCoopsActivity extends AppCompatActivity {
	
	private ActivityListCoopsBinding binding;
	
	SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		binding = ActivityListCoopsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		setTitle("Edit Co-op");
		
		binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
		binding.toolbar.setNavigationOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
		});


		
		Database db = new Database(this);
		db.open();
		Cursor coops = db.fetchCoops();
		coops.moveToFirst();
		
		Log.i("Heh", "Found " + coops.getCount());
		
		adapter = new SimpleCursorAdapter(
			this,
			android.R.layout.simple_list_item_2,
			coops,
			new String[] { DatabaseHelper._ID, DatabaseHelper.COOP_NAME },
			new int[] { android.R.id.text1, android.R.id.text2 },
			0);
		adapter.notifyDataSetChanged();
		
		binding.listView.setEmptyView(binding.empty);
		
		binding.listView.setAdapter(adapter);

		binding.listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long viewId) -> {
			TextView idView = view.findViewById(android.R.id.text1);
			String id = idView.getText().toString();

			Intent editIntent = new Intent(getApplicationContext(), EditCoopActivity.class);
			editIntent.putExtra(EditCoopActivity.EDIT_ID, id);

			startActivity(editIntent);
		});

		db.close();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		// TODO: Implement this method
		
		Database db = new Database(this);
		db.open();
		Cursor coops = db.fetchCoops();
		coops.moveToFirst();
		
		adapter.notifyDataSetChanged();

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
			startActivity(editIntent);
		}
		return super.onOptionsItemSelected(item);
	}
}
